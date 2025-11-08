import React, { createContext, useContext, useState, useEffect } from 'react';
import { usersApi } from '../../users/api/userApi';
import { jwtDecode } from 'jwt-decode';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('accessToken'));
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  // Функция для получения полной информации о пользователе
  const fetchUserProfile = async (token) => {
    try {
      const userData = await usersApi.getCurrentUser();
      return userData;
    } catch (error) {
      console.error('Failed to fetch user profile:', error);
      // Если не удалось получить профиль, создаем минимальный объект из токена
      const decoded = jwtDecode(token);
      return {
        id: decoded.sub || decoded.userId,
        username: decoded.username || decoded.sub,
        firstName: decoded.firstName || 'User',
        lastName: decoded.lastName || '',
        role: decoded.role || 'USER'
      };
    }
  };

  useEffect(() => {
    const initAuth = async () => {
      const storedToken = localStorage.getItem('accessToken');

      if (storedToken) {
        try {
          // Проверяем, что токен не истек
          const decoded = jwtDecode(storedToken);
          if (decoded.exp * 1000 > Date.now()) {
            // Получаем полную информацию о пользователе
            const userData = await fetchUserProfile(storedToken);
            setUser(userData);
            setToken(storedToken);
            localStorage.setItem('user', JSON.stringify(userData));
          } else {
            logout();
          }
        } catch (error) {
          console.error('Invalid token:', error);
          logout();
        }
      }
      setIsLoading(false);
    };

    initAuth();
  }, []);

  const login = async (username, password) => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await usersApi.login({ username, password });
      const { accessToken, refreshToken } = response;

      // Получаем полную информацию о пользователе
      const userData = await fetchUserProfile(accessToken);

      setToken(accessToken);
      setUser(userData);
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('user', JSON.stringify(userData));

      if (refreshToken) {
        localStorage.setItem('refreshToken', refreshToken);
      }

      return response;
    } catch (error) {
      const errorMessage = error.response?.data?.errors?.[0]?.message ||
                          error.message ||
                          'Ошибка входа';
      setError(errorMessage);
      throw new Error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    setError(null);
    localStorage.removeItem('accessToken');
    localStorage.removeItem('user');
    localStorage.removeItem('refreshToken');
  };

  const clearError = () => {
    setError(null);
  };

  const value = {
    user,
    token,
    isLoading,
    error,
    login,
    logout,
    clearError,
    isAuthenticated: !!token && !!user,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};