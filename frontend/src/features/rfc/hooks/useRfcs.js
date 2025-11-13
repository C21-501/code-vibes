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
      console.log('Approving RFC:', { rfcId, comment });
      const response = await rfcApi.approveRfc(rfcId, { comment });
      console.log('Approve RFC response:', response);

      // Принудительно обновляем данные RFC после согласования
      await loadRfcs(pagination.page, filters);
      return response;
    } catch (err) {
      const errorMessage = err.response?.data?.errors?.[0]?.message || 'Ошибка согласования RFC';
      console.error('Error approving RFC:', err);
      setError(errorMessage);
      throw new Error(errorMessage);
    }
  };

  const unapproveRfc = async (rfcId, comment = '') => {
    try {
      console.log('Unapproving RFC:', { rfcId, comment });
      const response = await rfcApi.unapproveRfc(rfcId, { comment });
      console.log('Unapprove RFC response:', response);

      // Принудительно обновляем данные RFC после отмены согласования
      await loadRfcs(pagination.page, filters);
      return response;
    } catch (err) {
      const errorMessage = err.response?.data?.errors?.[0]?.message || 'Ошибка отмены согласования RFC';
      console.error('Error unapproving RFC:', err);
      setError(errorMessage);
      throw new Error(errorMessage);
    }
  };

  const confirmSubsystem = async (rfcId, subsystemId, status, comment = '') => {
    try {
      console.log('Confirming subsystem:', { rfcId, subsystemId, status, comment });
      const response = await rfcApi.updateSubsystemConfirmation(rfcId, subsystemId, { status, comment });
      console.log('Confirm subsystem response:', response);

      // Принудительно обновляем данные RFC после подтверждения подсистемы
      await loadRfcs(pagination.page, filters);
      return response;
    } catch (err) {
      const errorMessage = err.response?.data?.errors?.[0]?.message || 'Ошибка подтверждения подсистемы';
      console.error('Error confirming subsystem:', err);
      setError(errorMessage);
      throw new Error(errorMessage);
    }
  };

  const updateExecutionStatus = async (rfcId, subsystemId, status, comment = '') => {
    try {
      console.log('Updating execution status:', { rfcId, subsystemId, status, comment });
      const response = await rfcApi.updateSubsystemExecution(rfcId, subsystemId, { status, comment });
      console.log('Update execution status response:', response);

      // Принудительно обновляем данные RFC после обновления статуса выполнения
      await loadRfcs(pagination.page, filters);
      return response;
    } catch (err) {
      const errorMessage = err.response?.data?.errors?.[0]?.message || 'Ошибка обновления статуса выполнения';
      console.error('Error updating execution status:', err);
      setError(errorMessage);
      throw new Error(errorMessage);
    }
  };

  // Функция для удаления RFC
  const deleteRfc = async (id) => {
    try {
      setLoading(true);
      await rfcApi.deleteRfc(id);

      // Удаляем RFC из локального состояния
      setRfcs(prevRfcs => prevRfcs.filter(rfc => rfc.id !== id));

      // Обновляем пагинацию
      setPagination(prev => ({
        ...prev,
        totalElements: prev.totalElements - 1
      }));

      return true;
    } catch (err) {
      const errorMessage = err.response?.data?.errors?.[0]?.message || 'Не удалось удалить RFC';
      setError(errorMessage);
      console.error('Error deleting RFC:', err);
      throw new Error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  // Функция для обновления RFC
  const updateRfc = async (id, rfcData) => {
    try {
      setLoading(true);
      const response = await rfcApi.updateRfc(id, rfcData);

      // Обновляем RFC в локальном состоянии
      setRfcs(prevRfcs =>
        prevRfcs.map(rfc => rfc.id === id ? response : rfc)
      );

      return response;
    } catch (err) {
      const errorMessage = err.response?.data?.errors?.[0]?.message || 'Не удалось обновить RFC';
      setError(errorMessage);
      console.error('Error updating RFC:', err);
      throw new Error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  // Функция для принудительного обновления конкретного RFC
  const refreshRfc = async (rfcId) => {
    try {
      console.log('Refreshing RFC:', rfcId);
      const updatedRfc = await rfcApi.getRfcById(rfcId);
      console.log('Refreshed RFC data:', updatedRfc);

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
    refreshRfc,
    deleteRfc,
    updateRfc // Добавляем метод обновления
  };
};