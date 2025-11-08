  import { useState, useEffect } from 'react';
  import { rfcApi } from '../api/rfcApi';

  export const useRfcs = (initialFilters = {}) => {
    const [rfcs, setRfcs] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [pagination, setPagination] = useState({
      page: 0,
      size: 20,
      totalElements: 0,
      totalPages: 0,
      first: true,
      last: false
    });
    const [filters, setFilters] = useState(initialFilters);

    const loadRfcs = async (page = 0, newFilters = filters) => {
      setLoading(true);
      setError(null);

      try {
        const response = await rfcApi.getRfcs({
          ...newFilters,
          page,
          size: pagination.size
        });

        setRfcs(response.content);
        setPagination({
          page: response.number,
          size: response.size,
          totalElements: response.totalElements,
          totalPages: response.totalPages,
          first: response.first,
          last: response.last
        });
      } catch (err) {
        const errorMessage = err.response?.data?.errors?.[0]?.message || 'Не удалось загрузить список RFC';
        setError(errorMessage);
        console.error('Error loading RFCs:', err);
      } finally {
        setLoading(false);
      }
    };

    const updateFilters = (newFilters) => {
      setFilters(newFilters);
      loadRfcs(0, newFilters);
    };

    const goToPage = (page) => {
      loadRfcs(page, filters);
    };

    useEffect(() => {
      loadRfcs(0, filters);
    }, []);

    return {
      rfcs,
      loading,
      error,
      pagination,
      filters,
      loadRfcs,
      updateFilters,
      goToPage,
      refetch: () => loadRfcs(pagination.page, filters)
    };
  };