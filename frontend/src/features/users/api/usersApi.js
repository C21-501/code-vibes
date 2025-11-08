import { api } from '../../../shared/api/config';

export const usersApi = {
  getUsers: (params = {}) =>
    api.get('/users', { params }).then(response => response.data),

  getUserById: (id) =>
    api.get(`/users/${id}`).then(response => response.data),

  createUser: (userData) =>
    api.post('/users', userData).then(response => response.data),

  updateUser: (id, userData) =>
    api.put(`/users/${id}`, userData).then(response => response.data),

  deleteUser: (id) =>
    api.delete(`/users/${id}`).then(response => response.data),
};