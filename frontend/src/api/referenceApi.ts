import apiClient from './axiosConfig';
import type { 
  System,
  Team,
  UserWithRole,
  CreateSystemRequest,
  UpdateSystemRequest,
  CreateTeamRequest,
  UpdateTeamRequest,
  CreateUserRequest,
  UpdateUserRequest
} from '@/types/api';

export const referenceApi = {
  // Systems API
  systems: {
    getAll: async (): Promise<System[]> => {
      const response = await apiClient.get<System[]>('/api/systems');
      return response.data;
    },

    getById: async (id: string): Promise<System> => {
      const response = await apiClient.get<System>(`/api/systems/${id}`);
      return response.data;
    },

    create: async (data: CreateSystemRequest): Promise<System> => {
      const response = await apiClient.post<System>('/api/systems', data);
      return response.data;
    },

    update: async (id: string, data: UpdateSystemRequest): Promise<System> => {
      const response = await apiClient.put<System>(`/api/systems/${id}`, data);
      return response.data;
    },

    delete: async (id: string): Promise<void> => {
      await apiClient.delete(`/api/systems/${id}`);
    }
  },

  // Teams API
  teams: {
    getAll: async (): Promise<Team[]> => {
      const response = await apiClient.get<Team[]>('/api/teams');
      return response.data;
    },

    getById: async (id: string): Promise<Team> => {
      const response = await apiClient.get<Team>(`/api/teams/${id}`);
      return response.data;
    },

    create: async (data: CreateTeamRequest): Promise<Team> => {
      const response = await apiClient.post<Team>('/api/teams', data);
      return response.data;
    },

    update: async (id: string, data: UpdateTeamRequest): Promise<Team> => {
      const response = await apiClient.put<Team>(`/api/teams/${id}`, data);
      return response.data;
    },

    delete: async (id: string): Promise<void> => {
      await apiClient.delete(`/api/teams/${id}`);
    }
  },

  // Users API
  users: {
    getAll: async (): Promise<UserWithRole[]> => {
      const response = await apiClient.get<UserWithRole[]>('/api/users');
      return response.data;
    },

    getById: async (id: string): Promise<UserWithRole> => {
      const response = await apiClient.get<UserWithRole>(`/api/users/${id}`);
      return response.data;
    },

    create: async (data: CreateUserRequest): Promise<UserWithRole> => {
      const response = await apiClient.post<UserWithRole>('/api/users', data);
      return response.data;
    },

    update: async (id: string, data: UpdateUserRequest): Promise<UserWithRole> => {
      const response = await apiClient.put<UserWithRole>(`/api/users/${id}`, data);
      return response.data;
    },

    delete: async (id: string): Promise<void> => {
      await apiClient.delete(`/api/users/${id}`);
    }
  }
};
