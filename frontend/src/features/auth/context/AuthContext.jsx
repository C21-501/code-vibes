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
      console.log('Fetched user profile:', userData);
      return userData;
    } catch (error) {
      console.error('Failed to fetch user profile:', error);
      // Если не удалось получить профиль, создаем минимальный объект из токена
      try {
        const decoded = jwtDecode(token);
        return {
          id: decoded.sub || decoded.userId,
          username: decoded.username || decoded.sub,
          firstName: decoded.firstName || 'User',
          lastName: decoded.lastName || '',
          role: decoded.role || 'USER'
        };
      } catch (decodeError) {
        console.error('Error decoding token:', decodeError);
        throw new Error('Невалидный токен');
      }
    }
  };

  // Инициализация аутентификации
  const initAuth = async () => {
    const storedToken = localStorage.getItem('accessToken');
    console.log('Initializing auth with token:', storedToken ? 'present' : 'absent');

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
          console.log('Auth initialized successfully for user:', userData.username);
        } else {
          console.log('Token expired, logging out');
          logout();
        }
      } catch (error) {
        console.error('Invalid token during init:', error);
        logout();
      }
    }
    setIsLoading(false);
  };

  useEffect(() => {
    initAuth();
  }, []);

  const login = async (username, password) => {
    setIsLoading(true);
    setError(null);

    try {
      console.log('Starting login process for user:', username);
      const response = await usersApi.login({ username, password });
      const { accessToken, refreshToken } = response;

      console.log('Login successful, token received');

      // Получаем полную информацию о пользователе
      const userData = await fetchUserProfile(accessToken);
      console.log('User profile fetched:', userData);

      // Обновляем состояние
      setToken(accessToken);
      setUser(userData);
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('user', JSON.stringify(userData));

      if (refreshToken) {
        localStorage.setItem('refreshToken', refreshToken);
      }

      console.log('Auth context updated successfully');
      return response;
    } catch (error) {
      console.error('Login error in AuthContext:', error);

      // Очищаем невалидные данные при ошибке
      logout();

      const errorMessage = error.response?.data?.errors?.[0]?.message ||
                          error.message ||
                          'Ошибка входа';
      setError(errorMessage);
      throw error; // Пробрасываем ошибку дальше
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    console.log('Logging out user');
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

  // Функция для принудительного обновления данных пользователя
  const refreshUser = async () => {
    const currentToken = localStorage.getItem('accessToken');
    if (currentToken && user) {
      try {
        const userData = await fetchUserProfile(currentToken);
        setUser(userData);
        localStorage.setItem('user', JSON.stringify(userData));
        return userData;
      } catch (error) {
        console.error('Error refreshing user data:', error);
        return user; // Возвращаем старые данные в случае ошибки
      }
    }
  };

  const value = {
    user,
    token,
    isLoading,
    error,
    login,
    logout,
    clearError,
    refreshUser,
    isAuthenticated: !!token && !!user,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};