import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../api';
import { type DashboardData, type ApiResponse } from '../types';

export const useRfcStats = () => {
  return useQuery<ApiResponse<DashboardData>>({
    queryKey: ['rfcStats'],
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<DashboardData>>('/api/rfcs/stats');
      return response.data;
    },
    staleTime: 1000 * 60 * 5, // 5 minutes
  });
};
