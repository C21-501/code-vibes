import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../api';
import { type DashboardData } from '../types';

export const useRfcStats = () => {
  return useQuery<DashboardData>({
    queryKey: ['rfcStats'],
    queryFn: async () => {
      const response = await apiClient.get<DashboardData>('/api/rfcs/stats');
      return response.data;
    },
    staleTime: 1000 * 60 * 5, // 5 minutes
  });
};
