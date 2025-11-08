import { api } from '../../../shared/api/config';

export const usersApi = {
  login: (credentials) =>
    api.post('/user/login', credentials).then(response => {
      const data = response.data;
      if (data.accessToken) {
        localStorage.setItem('accessToken', data.accessToken);
      }
      if (data.refreshToken) {
        localStorage.setItem('refreshToken', data.refreshToken);
      }
      return data;
    }),

  getCurrentUser: () =>
    api.get('/user/me').then(response => response.data),

  getUsers: (params = {}) =>
    api.get('/user', { params }).then(response => response.data),

  getUserById: (id) =>
    api.get(`/user/${id}`).then(response => response.data),

  createUser: (userData) =>
    api.post('/user', userData).then(response => response.data),

  updateUser: (id, userData) =>
    api.put(`/user/${id}`, userData).then(response => response.data),

  deleteUser: (id) =>
    api.delete(`/user/${id}`).then(response => response.data)
};

// ИЛИ альтернативно - named exports вместо объекта:
export const getUsers = (params = {}) =>
  api.get('/user', { params }).then(response => response.data);

export const createUser = (userData) =>
  api.post('/user', userData).then(response => response.data);

export const updateUser = (id, userData) =>
  api.put(`/user/${id}`, userData).then(response => response.data);

export const deleteUser = (id) =>
  api.delete(`/user/${id}`).then(response => response.data);

export const login = (credentials) =>
  api.post('/user/login', credentials).then(response => {
    const data = response.data;
    if (data.accessToken) {
      localStorage.setItem('accessToken', data.accessToken);
    }
    if (data.refreshToken) {
      localStorage.setItem('refreshToken', data.refreshToken);
    }
    return data;
  });

export const getCurrentUser = () =>
  api.get('/user/me').then(response => response.data);