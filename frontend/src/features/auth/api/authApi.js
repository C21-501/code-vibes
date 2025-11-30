import axios from 'axios';

// Используем переменную окружения или относительный путь через proxy
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
});

// УБЕРИТЕ интерцептор для refresh token, так как его нет в вашем OpenAPI
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ТОЛЬКО базовая обработка 401 ошибки
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authApi = {
  // Логин согласно OpenAPI спецификации
  login: (credentials) =>
    api.post('/user/login', credentials).then(response => {
      const data = response.data;
      // Сохраняем токены
      if (data.accessToken) {
        localStorage.setItem('accessToken', data.accessToken);
      }
      if (data.refreshToken) {
        localStorage.setItem('refreshToken', data.refreshToken);
      }
      return data;
    }),

  // Получение текущего пользователя
  getCurrentUser: () =>
    api.get('/user/me').then(response => response.data),

  // Выход
  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    return Promise.resolve();
  },
};