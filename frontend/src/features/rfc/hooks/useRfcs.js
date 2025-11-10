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

  // Новые методы для approve/confirm
  const approveRfc = async (rfcId, comment = '') => {
    try {
      await rfcApi.approveRfc(rfcId, { comment });
      // Принудительно обновляем данные RFC после согласования
      await loadRfcs(pagination.page, filters);
      return true;
    } catch (err) {
      const errorMessage = err.response?.data?.errors?.[0]?.message || 'Ошибка согласования RFC';
      setError(errorMessage);
      throw new Error(errorMessage);
    }
  };

  const unapproveRfc = async (rfcId, comment = '') => {
    try {
      await rfcApi.unapproveRfc(rfcId, { comment });
      // Принудительно обновляем данные RFC после отмены согласования
      await loadRfcs(pagination.page, filters);
      return true;
    } catch (err) {
      const errorMessage = err.response?.data?.errors?.[0]?.message || 'Ошибка отмены согласования RFC';
      setError(errorMessage);
      throw new Error(errorMessage);
    }
  };

  const confirmSubsystem = async (rfcId, affectedSubsystemId, status, comment = '') => {
    try {
      console.log('Confirming subsystem:', { rfcId, affectedSubsystemId, status, comment });
      await rfcApi.updateSubsystemConfirmation(rfcId, affectedSubsystemId, { status, comment });
      // Принудительно обновляем данные RFC после подтверждения подсистемы
      await loadRfcs(pagination.page, filters);
      return true;
    } catch (err) {
      const errorMessage = err.response?.data?.errors?.[0]?.message || 'Ошибка подтверждения подсистемы';
      setError(errorMessage);
      console.error('Error confirming subsystem:', err);
      throw new Error(errorMessage);
    }
  };

  const updateExecutionStatus = async (rfcId, affectedSubsystemId, status, comment = '') => {
    try {
      console.log('Updating execution status:', { rfcId, affectedSubsystemId, status, comment });
      await rfcApi.updateSubsystemExecution(rfcId, affectedSubsystemId, { status, comment });
      // Принудительно обновляем данные RFC после обновления статуса выполнения
      await loadRfcs(pagination.page, filters);
      return true;
    } catch (err) {
      const errorMessage = err.response?.data?.errors?.[0]?.message || 'Ошибка обновления статуса выполнения';
      setError(errorMessage);
      console.error('Error updating execution status:', err);
      throw new Error(errorMessage);
    }
  };

  // Функция для принудительного обновления конкретного RFC
  const refreshRfc = async (rfcId) => {
    try {
      const updatedRfc = await rfcApi.getRfcById(rfcId);
      // Обновляем RFC в списке
      setRfcs(prevRfcs =>
        prevRfcs.map(rfc => rfc.id === rfcId ? updatedRfc : rfc)
      );
      return updatedRfc;
    } catch (err) {
      console.error('Error refreshing RFC:', err);
      return null;
    }
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
    refetch: () => loadRfcs(pagination.page, filters),
    // Новые методы
    approveRfc,
    unapproveRfc,
    confirmSubsystem,
    updateExecutionStatus,
    refreshRfc
  };
};