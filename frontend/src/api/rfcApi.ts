import apiClient from './axiosConfig';
import type { 
  Rfc, 
  PaginatedResponse, 
  CreateRfcRequest, 
  RfcFilters, 
  SortOptions,
  System,
  Team,
  ChangeStatusRequest
} from '../types/api';

export const rfcApi = {
  // Получить список RFC с фильтрами и пагинацией
  getRfcs: async (
    page: number = 1, 
    limit: number = 10, 
    filters?: RfcFilters, 
    sort?: SortOptions
  ): Promise<PaginatedResponse<Rfc>> => {
    const params = new URLSearchParams({
      page: page.toString(),
      limit: limit.toString(),
    });

    if (filters) {
      if (filters.status) params.append('status', filters.status);
      if (filters.priority) params.append('priority', filters.priority);
      if (filters.systemId) params.append('systemId', filters.systemId);
      if (filters.dateFrom) params.append('dateFrom', filters.dateFrom);
      if (filters.dateTo) params.append('dateTo', filters.dateTo);
      if (filters.my) params.append('my', 'true');
    }

    if (sort) {
      params.append('sortBy', sort.field);
      params.append('sortDir', sort.direction);
    }

    const response = await apiClient.get<PaginatedResponse<Rfc>>(`/api/rfcs?${params}`);
    return response.data;
  },

  // Создать новый RFC
  createRfc: async (rfcData: CreateRfcRequest): Promise<Rfc> => {
    const response = await apiClient.post<Rfc>('/api/rfcs', rfcData);
    return response.data;
  },

  // Получить RFC по ID
  getRfcById: async (id: string): Promise<Rfc> => {
    const response = await apiClient.get<Rfc>(`/api/rfcs/${id}`);
    return response.data;
  },

  // Получить список систем
  getSystems: async (): Promise<System[]> => {
    const response = await apiClient.get<System[]>('/api/systems');
    return response.data;
  },

  // Получить список команд
  getTeams: async (): Promise<Team[]> => {
    const response = await apiClient.get<Team[]>('/api/teams');
    return response.data;
  },

  // Изменить статус RFC
  changeRfcStatus: async (id: string, statusData: ChangeStatusRequest): Promise<Rfc> => {
    const response = await apiClient.put<Rfc>(`/api/rfcs/${id}/status`, statusData);
    return response.data;
  },

  // Подтвердить готовность исполнителя
  confirmExecutorReadiness: async (rfcId: string, teamId: string): Promise<Rfc> => {
    const response = await apiClient.put<Rfc>(`/api/rfcs/${rfcId}/executors/${teamId}/confirm`);
    return response.data;
  },
};
