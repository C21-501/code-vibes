import React, { useState, useEffect, useCallback, useMemo } from 'react';
import { useRfcs } from '../hooks/useRfcs';
import { useAuth } from '../../auth/context/AuthContext';
import { rfcApi } from '../api/rfcApi';
import { usersApi } from '../../users/api/userApi';
import {
  getStatusLabel,
  getUrgencyLabel,
  getStatusClass,
  getUrgencyClass,
  formatDate,
  RFC_STATUS,
  URGENCY
} from '../utils/rfcUtils';
import RfcTable from './RfcTable';
import RfcFilters from './RfcFilters';
import RfcModal from './RfcModal';
import CreateRfcModal from './CreateRfcModal';
import EditRfcModal from './EditRfcModal';
import StatusActionModal from './StatusActionModal';
import LoadingSpinner from '../../../shared/components/LoadingSpinner';
import Toast from '../../../shared/components/Toast';
import './RfcManagement.css';

const RfcManagement = () => {
  const { user } = useAuth();
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedRfc, setSelectedRfc] = useState(null);
  const [selectedRfcForEdit, setSelectedRfcForEdit] = useState(null);
  const [showViewModal, setShowViewModal] = useState(false);
  const [showStatusModal, setShowStatusModal] = useState(false);
  const [selectedRfcForStatus, setSelectedRfcForStatus] = useState(null);
  const [toast, setToast] = useState({ show: false, type: '', title: '', message: '' });
  const [usersCache, setUsersCache] = useState(new Map());

  // Используем useMemo для начальных фильтров
  const initialFilters = useMemo(() => ({
    status: '',
    urgency: '',
    requesterId: '',
    title: ''
  }), []);

  const {
    rfcs,
    loading,
    error,
    pagination,
    filters,
    updateFilters,
    goToPage,
    refetch,
    approveRfc,
    unapproveRfc,
    confirmSubsystem,
    updateExecutionStatus,
    refreshRfc,
    deleteRfc,
    updateRfc
  } = useRfcs(initialFilters);

  // Функция для получения данных пользователя с кэшированием
  const getUserData = useCallback(async (userId) => {
    if (!userId) return null;

    if (usersCache.has(userId)) {
      return usersCache.get(userId);
    }

    try {
      const userData = await usersApi.getUserById(userId);
      setUsersCache(prev => new Map(prev).set(userId, userData));
      return userData;
    } catch (error) {
      console.error(`Failed to fetch user ${userId}:`, error);
      return null;
    }
  }, [usersCache]);

  // Функция для обогащения RFC данными пользователей
  const enrichRfcWithUserData = useCallback(async (rfc) => {
    if (!rfc) return rfc;

    const enrichedRfc = { ...rfc };

    if (rfc.requesterId) {
      enrichedRfc.requester = await getUserData(rfc.requesterId);
    }

    if (rfc.affectedSystems && Array.isArray(rfc.affectedSystems)) {
      for (let system of enrichedRfc.affectedSystems) {
        if (system.affectedSubsystems && Array.isArray(system.affectedSubsystems)) {
          for (let subsystem of system.affectedSubsystems) {
            if (subsystem.executorId) {
              subsystem.executor = await getUserData(subsystem.executorId);
            }
          }
        }
      }
    }

    if (rfc.attachments && Array.isArray(rfc.attachments)) {
      for (let attachment of enrichedRfc.attachments) {
        if (attachment.uploadedById) {
          attachment.uploadedBy = await getUserData(attachment.uploadedById);
        }
      }
    }

    return enrichedRfc;
  }, [getUserData]);

  // Функции для уведомлений
  const showToast = useCallback((type, title, message) => {
    setToast({ show: true, type, title, message });
    setTimeout(() => setToast(prev => ({ ...prev, show: false })), 5000);
  }, []);

  const hideToast = useCallback(() => {
    setToast(prev => ({ ...prev, show: false }));
  }, []);

  // Обработчики фильтров с useCallback
  const handleStatusFilterChange = useCallback((status) => {
    updateFilters({ ...filters, status });
  }, [filters, updateFilters]);

  const handleUrgencyFilterChange = useCallback((urgency) => {
    updateFilters({ ...filters, urgency });
  }, [filters, updateFilters]);

  const handleRequesterFilterChange = useCallback((requesterId) => {
    updateFilters({ ...filters, requesterId });
  }, [filters, updateFilters]);

  const handleTitleFilterChange = useCallback((title) => {
    updateFilters({ ...filters, title });
  }, [filters, updateFilters]);

  const handleMyRfcFilterChange = useCallback((checked) => {
    if (checked) {
      updateFilters({ ...filters, requesterId: user?.id });
    } else {
      updateFilters({ ...filters, requesterId: '' });
    }
  }, [filters, updateFilters, user]);

  // Обработчики пагинации
  const handlePageChange = useCallback((page) => {
    goToPage(page);
  }, [goToPage]);

  const handlePrevPage = useCallback(() => {
    if (!pagination.first) {
      goToPage(pagination.page - 1);
    }
  }, [pagination.first, pagination.page, goToPage]);

  const handleNextPage = useCallback(() => {
    if (!pagination.last) {
      goToPage(pagination.page + 1);
    }
  }, [pagination.last, pagination.page, goToPage]);

  // Обработчики RFC
  const handleCreateRfc = useCallback(async (rfcData) => {
    try {
      await rfcApi.createRfc(rfcData);
      setShowCreateModal(false);
      showToast('success', 'Успех', 'RFC успешно создан');
      refetch();
    } catch (error) {
      const errorMessage = error.response?.data?.errors?.[0]?.message || 'Не удалось создать RFC';
      showToast('error', 'Ошибка', errorMessage);
    }
  }, [refetch, showToast]);

  const handleViewRfc = useCallback(async (rfcId) => {
    try {
      const rfc = await rfcApi.getRfcById(rfcId);
      const enrichedRfc = await enrichRfcWithUserData(rfc);
      setSelectedRfc(enrichedRfc);
      setShowViewModal(true);
    } catch (error) {
      const errorMessage = error.response?.data?.errors?.[0]?.message || 'Не удалось загрузить данные RFC';
      showToast('error', 'Ошибка', errorMessage);
    }
  }, [enrichRfcWithUserData, showToast]);

  const handleEditRfc = useCallback(async (rfcId) => {
    try {
      const rfc = await rfcApi.getRfcById(rfcId);
      const enrichedRfc = await enrichRfcWithUserData(rfc);
      setSelectedRfcForEdit(enrichedRfc);
      setShowEditModal(true);
    } catch (error) {
      const errorMessage = error.response?.data?.errors?.[0]?.message || 'Не удалось загрузить данные RFC';
      showToast('error', 'Ошибка', errorMessage);
    }
  }, [enrichRfcWithUserData, showToast]);

  const handleUpdateRfc = useCallback(async (rfcId, rfcData) => {
    try {
      await updateRfc(rfcId, rfcData);
      setShowEditModal(false);
      setSelectedRfcForEdit(null);
      showToast('success', 'Успех', 'RFC успешно обновлен');
      refetch();
    } catch (error) {
      showToast('error', 'Ошибка', error.message || 'Не удалось обновить RFC');
    }
  }, [updateRfc, refetch, showToast]);

  const handleDeleteRfc = useCallback(async (rfcId) => {
    if (!window.confirm('Вы уверены, что хотите удалить этот RFC?')) {
      return;
    }

    try {
      await deleteRfc(rfcId);
      showToast('success', 'Успех', 'RFC успешно удален');
    } catch (error) {
      showToast('error', 'Ошибка', error.message || 'Не удалось удалить RFC');
    }
  }, [deleteRfc, showToast]);

  const handleStatusAction = useCallback((rfc) => {
    setSelectedRfcForStatus(rfc);
    setShowStatusModal(true);
  }, []);

  const updateSelectedRfc = useCallback(async (rfcId) => {
    try {
      console.log('Fetching updated RFC data for:', rfcId);
      const rfc = await rfcApi.getRfcById(rfcId);
      const updatedRfc = await enrichRfcWithUserData(rfc);
      setSelectedRfc(updatedRfc);
      return updatedRfc;
    } catch (error) {
      console.error('Error updating selected RFC:', error);
      return null;
    }
  }, [enrichRfcWithUserData]);

  // Обогащенные RFC с кэшированными пользователями
  const enrichedRfcs = useMemo(() => {
    return rfcs.map(rfc => {
      const enriched = { ...rfc };
      if (rfc.requesterId && usersCache.has(rfc.requesterId)) {
        enriched.requester = usersCache.get(rfc.requesterId);
      }
      return enriched;
    });
  }, [rfcs, usersCache]);

  const handleApprove = useCallback(async (rfcId, comment = '') => {
    try {
      console.log('Starting approve process for RFC:', rfcId);
      const finalComment = comment.trim() || 'RFC согласован в соответствии с установленными процедурами';
      await approveRfc(rfcId, finalComment);

      await updateSelectedRfc(rfcId);
      await refetch();

      showToast('success', 'Успех', 'RFC успешно согласован');
    } catch (error) {
      console.error('Error in handleApprove:', error);
      showToast('error', 'Ошибка', error.message || 'Не удалось согласовать RFC');
    }
  }, [approveRfc, updateSelectedRfc, refetch, showToast]);

  const handleUnapprove = useCallback(async (rfcId, comment = '') => {
    try {
      console.log('Starting unapprove process for RFC:', rfcId);
      const finalComment = comment.trim() || 'Согласование RFC отозвано по техническим причинам';
      await unapproveRfc(rfcId, finalComment);

      await updateSelectedRfc(rfcId);
      await refetch();

      showToast('success', 'Успех', 'Согласование RFC отменено');
    } catch (error) {
      console.error('Error in handleUnapprove:', error);
      showToast('error', 'Ошибка', error.message || 'Не удалось отменить согласование RFC');
    }
  }, [unapproveRfc, updateSelectedRfc, refetch, showToast]);

  const handleConfirm = useCallback(async (rfcId, subsystemId, status, comment = '') => {
    try {
      console.log('Confirming subsystem:', { rfcId, subsystemId, status, comment });

      let defaultComment = '';
      if (status === 'CONFIRMED') {
        defaultComment = 'Подсистема готова к выполнению работ по RFC';
      } else {
        defaultComment = 'Подсистема не готова к выполнению работ по RFC';
      }

      const finalComment = comment.trim() || defaultComment;
      await confirmSubsystem(rfcId, subsystemId, status, finalComment);

      await updateSelectedRfc(rfcId);
      const action = status === 'CONFIRMED' ? 'подтверждена' : 'отклонена';
      showToast('success', 'Успех', `Подсистема ${action}`);
    } catch (error) {
      console.error('Error in handleConfirm:', error);
      showToast('error', 'Ошибка', error.message || 'Не удалось выполнить действие с подсистемой');
    }
  }, [confirmSubsystem, updateSelectedRfc, showToast]);

  const handleUpdateExecution = useCallback(async (rfcId, subsystemId, status, comment = '') => {
    try {
      console.log('Updating execution:', { rfcId, subsystemId, status, comment });

      let defaultComment = '';
      if (status === 'IN_PROGRESS') {
        defaultComment = 'Работы по подсистеме начаты';
      } else {
        defaultComment = 'Работы по подсистеме завершены';
      }

      const finalComment = comment.trim() || defaultComment;
      await updateExecutionStatus(rfcId, subsystemId, status, finalComment);

      await updateSelectedRfc(rfcId);
      const action = status === 'IN_PROGRESS' ? 'начато' : 'завершено';
      showToast('success', 'Успех', `Выполнение подсистемы ${action}`);
    } catch (error) {
      console.error('Error in handleUpdateExecution:', error);
      showToast('error', 'Ошибка', error.message || 'Не удалось обновить статус выполнения');
    }
  }, [updateExecutionStatus, updateSelectedRfc, showToast]);

  const renderContent = () => {
    if (loading) {
      return (
        <div className="rfc-loading">
          <LoadingSpinner size="large" />
          <p>Загрузка RFC...</p>
        </div>
      );
    }

    if (error) {
      return (
        <div className="rfc-error">
          <div className="error-icon">⚠️</div>
          <h3>Ошибка загрузки</h3>
          <p>{error}</p>
          <button onClick={refetch} className="btn-retry">
            Попробовать снова
          </button>
        </div>
      );
    }

    return (
      <>
        <RfcTable
          rfcs={enrichedRfcs}
          currentUser={user}
          onViewRfc={handleViewRfc}
          onEditRfc={handleEditRfc}
          onDeleteRfc={handleDeleteRfc}
          onStatusAction={handleStatusAction}
        />

        {enrichedRfcs.length > 0 && (
          <div className="rfc-pagination">
            <div className="pagination-info">
              Показано {pagination.page * pagination.size + 1} -{' '}
              {Math.min((pagination.page + 1) * pagination.size, pagination.totalElements)} из{' '}
              {pagination.totalElements} записей
            </div>

            <div className="pagination-controls">
              <button
                onClick={handlePrevPage}
                disabled={pagination.first}
                className="pagination-btn"
              >
                ← Назад
              </button>

              <div className="page-numbers">
                {Array.from({ length: Math.min(5, pagination.totalPages) }, (_, i) => {
                  const pageNum = i;
                  return (
                    <button
                      key={pageNum}
                      onClick={() => handlePageChange(pageNum)}
                      className={`pagination-btn ${pagination.page === pageNum ? 'active' : ''}`}
                    >
                      {pageNum + 1}
                    </button>
                  );
                })}
              </div>

              <button
                onClick={handleNextPage}
                disabled={pagination.last}
                className="pagination-btn"
              >
                Вперед →
              </button>
            </div>
          </div>
        )}
      </>
    );
  };

  return (
    <div className="rfc-management">
      <div className="rfc-header">
        <h1>Управление RFC</h1>
        <button
          className="btn-primary"
          onClick={() => setShowCreateModal(true)}
        >
          + Создать RFC
        </button>
      </div>

      <RfcFilters
        filters={filters}
        onStatusChange={handleStatusFilterChange}
        onUrgencyChange={handleUrgencyFilterChange}
        onRequesterChange={handleRequesterFilterChange}
        onTitleChange={handleTitleFilterChange}
        onMyRfcChange={handleMyRfcFilterChange}
        currentUser={user}
      />

      <div className="rfc-content">
        {renderContent()}
      </div>

      {showCreateModal && (
        <CreateRfcModal
          isOpen={showCreateModal}
          onClose={() => setShowCreateModal(false)}
          onSubmit={handleCreateRfc}
        />
      )}

      {showEditModal && selectedRfcForEdit && (
        <EditRfcModal
          isOpen={showEditModal}
          onClose={() => {
            setShowEditModal(false);
            setSelectedRfcForEdit(null);
          }}
          onSubmit={handleUpdateRfc}
          rfc={selectedRfcForEdit}
        />
      )}

      {showViewModal && selectedRfc && (
        <RfcModal
          isOpen={showViewModal}
          onClose={() => setShowViewModal(false)}
          rfc={selectedRfc}
          onApprove={handleApprove}
          onUnapprove={handleUnapprove}
          onConfirm={handleConfirm}
          onUpdateExecution={handleUpdateExecution}
        >
          <div className="rfc-detail-content">
            <div className="detail-section">
              <h3>Основная информация</h3>
              <div className="detail-row">
                <span className="detail-label">ID:</span>
                <span className="detail-value">RFC-{selectedRfc.id}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Название:</span>
                <span className="detail-value">{selectedRfc.title}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Статус:</span>
                <span className={`status-badge ${getStatusClass(selectedRfc.status)}`}>
                  {getStatusLabel(selectedRfc.status)}
                </span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Срочность:</span>
                <span className={`urgency-badge ${getUrgencyClass(selectedRfc.urgency)}`}>
                  {getUrgencyLabel(selectedRfc.urgency)}
                </span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Дата реализации:</span>
                <span className="detail-value">{formatDate(selectedRfc.implementationDate)}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Создатель:</span>
                <span className="detail-value">
                  {selectedRfc.requester
                    ? `${selectedRfc.requester.firstName} ${selectedRfc.requester.lastName}`
                    : `ID: ${selectedRfc.requesterId || 'Неизвестно'}`
                  }
                </span>
              </div>
            </div>

            <div className="detail-section">
              <h3>Описание</h3>
              <p className="description-text">{selectedRfc.description || 'Нет описания'}</p>
            </div>

            {selectedRfc.affectedSystems && selectedRfc.affectedSystems.length > 0 && (
              <div className="detail-section">
                <h3>Затронутые системы и подсистемы</h3>
                {selectedRfc.affectedSystems.map((system, index) => (
                  <div key={system.systemId || index} className="system-group">
                    <h4 className="system-name">{system.systemName || `Система ${index + 1}`}</h4>
                    {system.affectedSubsystems && system.affectedSubsystems.map((subsystem, subIndex) => (
                      <div key={subsystem.id || subIndex} className="subsystem-item">
                        <div className="subsystem-info">
                          <span className="subsystem-name">{subsystem.subsystemName}</span>
                          <span className={`status-badge ${getStatusClass(subsystem.confirmationStatus)}`}>
                            Подтверждение: {getStatusLabel(subsystem.confirmationStatus)}
                          </span>
                          <span className={`status-badge ${getStatusClass(subsystem.executionStatus)}`}>
                            Выполнение: {getStatusLabel(subsystem.executionStatus)}
                          </span>
                          {subsystem.executor && (
                            <span className="executor-info">
                              Исполнитель: {subsystem.executor.firstName} {subsystem.executor.lastName}
                            </span>
                          )}
                          {!subsystem.executor && subsystem.executorId && (
                            <span className="executor-info">
                              Исполнитель: ID: {subsystem.executorId}
                            </span>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                ))}
              </div>
            )}
          </div>
        </RfcModal>
      )}

      {showStatusModal && selectedRfcForStatus && (
        <StatusActionModal
          isOpen={showStatusModal}
          onClose={() => {
            setShowStatusModal(false);
            setSelectedRfcForStatus(null);
          }}
          rfc={selectedRfcForStatus}
          currentUser={user}
        />
      )}

      <Toast
        show={toast.show}
        type={toast.type}
        title={toast.title}
        message={toast.message}
        onClose={hideToast}
      />
    </div>
  );
};

export default RfcManagement;