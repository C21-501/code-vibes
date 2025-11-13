import React, { useState, useEffect } from 'react';
import { useRfcs } from '../hooks/useRfcs';
import { useAuth } from '../../auth/context/AuthContext';
import { rfcApi } from '../api/rfcApi';
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
import StatusActionModal from './StatusActionModal';
import LoadingSpinner from '../../../shared/components/LoadingSpinner';
import Toast from '../../../shared/components/Toast';
import './RfcManagement.css';

const RfcManagement = () => {
  const { user } = useAuth();
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [selectedRfc, setSelectedRfc] = useState(null);
  const [showViewModal, setShowViewModal] = useState(false);
  const [showStatusModal, setShowStatusModal] = useState(false);
  const [selectedRfcForStatus, setSelectedRfcForStatus] = useState(null);
  const [toast, setToast] = useState({ show: false, type: '', title: '', message: '' });

  // Используем хук для управления RFC с методами для действий
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
    deleteRfc // Добавляем метод удаления из хука
  } = useRfcs({
    status: '',
    urgency: '',
    requesterId: ''
  });

  // Функции для уведомлений
  const showToast = (type, title, message) => {
    setToast({ show: true, type, title, message });
    setTimeout(() => setToast({ ...toast, show: false }), 5000);
  };

  const hideToast = () => {
    setToast({ ...toast, show: false });
  };

  // Обработчики фильтров
  const handleStatusFilterChange = (status) => {
    updateFilters({ ...filters, status });
  };

  const handleUrgencyFilterChange = (urgency) => {
    updateFilters({ ...filters, urgency });
  };

  const handleRequesterFilterChange = (requesterId) => {
    updateFilters({ ...filters, requesterId });
  };

  const handleMyRfcFilterChange = (checked) => {
    if (checked) {
      updateFilters({ ...filters, requesterId: user?.id });
    } else {
      updateFilters({ ...filters, requesterId: '' });
    }
  };

  const handleApplyFilters = (newFilters) => {
    updateFilters(newFilters);
  };

  // Обработчики пагинации
  const handlePageChange = (page) => {
    goToPage(page);
  };

  const handlePrevPage = () => {
    if (!pagination.first) {
      goToPage(pagination.page - 1);
    }
  };

  const handleNextPage = () => {
    if (!pagination.last) {
      goToPage(pagination.page + 1);
    }
  };

  // Обработчики RFC
  const handleCreateRfc = async (rfcData) => {
    try {
      await rfcApi.createRfc(rfcData);
      setShowCreateModal(false);
      showToast('success', 'Успех', 'RFC успешно создан');
      refetch(); // Перезагружаем список
    } catch (error) {
      const errorMessage = error.response?.data?.errors?.[0]?.message || 'Не удалось создать RFC';
      showToast('error', 'Ошибка', errorMessage);
    }
  };

  const handleViewRfc = async (rfcId) => {
    try {
      const rfc = await rfcApi.getRfcById(rfcId);
      setSelectedRfc(rfc);
      setShowViewModal(true);
    } catch (error) {
      const errorMessage = error.response?.data?.errors?.[0]?.message || 'Не удалось загрузить данные RFC';
      showToast('error', 'Ошибка', errorMessage);
    }
  };

  const handleEditRfc = async (rfcId) => {
    try {
      const rfc = await rfcApi.getRfcById(rfcId);
      // Здесь логика для открытия модалки редактирования
      // setSelectedRfc(rfc);
      // setShowEditModal(true);
      showToast('info', 'Информация', 'Функция редактирования в разработке');
    } catch (error) {
      const errorMessage = error.response?.data?.errors?.[0]?.message || 'Не удалось загрузить данные RFC';
      showToast('error', 'Ошибка', errorMessage);
    }
  };

  const handleDeleteRfc = async (rfcId) => {
    if (!window.confirm('Вы уверены, что хотите удалить этот RFC?')) {
      return;
    }

    try {
      await deleteRfc(rfcId); // Используем метод из хука
      showToast('success', 'Успех', 'RFC успешно удален');
      // Не нужно вызывать refetch(), так как хук уже обновил состояние
    } catch (error) {
      showToast('error', 'Ошибка', error.message || 'Не удалось удалить RFC');
    }
  };

  const handleStatusAction = (rfc) => {
    setSelectedRfcForStatus(rfc);
    setShowStatusModal(true);
  };

  // Функция для обновления выбранного RFC
  const updateSelectedRfc = async (rfcId) => {
    try {
      console.log('Fetching updated RFC data for:', rfcId);
      const updatedRfc = await rfcApi.getRfcById(rfcId);
      console.log('Fetched updated RFC:', updatedRfc);
      setSelectedRfc(updatedRfc);
      return updatedRfc;
    } catch (error) {
      console.error('Error updating selected RFC:', error);
      return null;
    }
  };

  // Новые обработчики для действий в RfcModal
  const handleApprove = async (rfcId, comment = '') => {
    try {
      console.log('Starting approve process for RFC:', rfcId);
      const response = await approveRfc(rfcId, comment);
      console.log('Approve API response:', response);

      // Принудительно обновляем выбранный RFC
      const updatedRfc = await updateSelectedRfc(rfcId);
      console.log('Updated RFC after approval:', updatedRfc);

      // Также обновляем список RFC
      await refetch();

      showToast('success', 'Успех', 'RFC успешно согласован');
    } catch (error) {
      console.error('Error in handleApprove:', error);
      showToast('error', 'Ошибка', error.message || 'Не удалось согласовать RFC');
    }
  };

  const handleUnapprove = async (rfcId, comment = '') => {
    try {
      console.log('Starting unapprove process for RFC:', rfcId);
      const response = await unapproveRfc(rfcId, comment);
      console.log('Unapprove API response:', response);

      // Принудительно обновляем выбранный RFC
      const updatedRfc = await updateSelectedRfc(rfcId);
      console.log('Updated RFC after unapproval:', updatedRfc);

      // Также обновляем список RFC
      await refetch();

      showToast('success', 'Успех', 'Согласование RFC отменено');
    } catch (error) {
      console.error('Error in handleUnapprove:', error);
      showToast('error', 'Ошибка', error.message || 'Не удалось отменить согласование RFC');
    }
  };

  const handleConfirm = async (rfcId, subsystemId, status, comment = '') => {
    try {
      console.log('Confirming subsystem:', { rfcId, subsystemId, status, comment });
      await confirmSubsystem(rfcId, subsystemId, status, comment);
      // Обновляем выбранный RFC
      await updateSelectedRfc(rfcId);
      const action = status === 'CONFIRMED' ? 'подтверждена' : 'отклонена';
      showToast('success', 'Успех', `Подсистема ${action}`);
    } catch (error) {
      console.error('Error in handleConfirm:', error);
      showToast('error', 'Ошибка', error.message || 'Не удалось выполнить действие с подсистемой');
    }
  };

  const handleUpdateExecution = async (rfcId, subsystemId, status, comment = '') => {
    try {
      console.log('Updating execution:', { rfcId, subsystemId, status, comment });
      await updateExecutionStatus(rfcId, subsystemId, status, comment);
      // Обновляем выбранный RFC
      await updateSelectedRfc(rfcId);
      const action = status === 'IN_PROGRESS' ? 'начато' : 'завершено';
      showToast('success', 'Успех', `Выполнение подсистемы ${action}`);
    } catch (error) {
      console.error('Error in handleUpdateExecution:', error);
      showToast('error', 'Ошибка', error.message || 'Не удалось обновить статус выполнения');
    }
  };

  const handleStatusUpdate = async (rfcId, action, comment = '') => {
    try {
      // Используем существующий API если нужно
      showToast('info', 'Информация', 'Функция изменения статуса в разработке');
    } catch (error) {
      const errorMessage = error.response?.data?.errors?.[0]?.message || 'Не удалось обновить статус RFC';
      showToast('error', 'Ошибка', errorMessage);
    }
  };

  // Рендер контента в зависимости от состояния
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
          rfcs={rfcs}
          currentUser={user}
          onViewRfc={handleViewRfc}
          onEditRfc={handleEditRfc}
          onDeleteRfc={handleDeleteRfc}
          onStatusAction={handleStatusAction}
        />

        {/* Пагинация */}
        {rfcs.length > 0 && (
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

      {/* Фильтры */}
      <RfcFilters
        filters={filters}
        onStatusChange={handleStatusFilterChange}
        onUrgencyChange={handleUrgencyFilterChange}
        onRequesterChange={handleRequesterFilterChange}
        onMyRfcChange={handleMyRfcFilterChange}
        onApplyFilters={handleApplyFilters}
        currentUser={user}
      />

      {/* Содержимое */}
      <div className="rfc-content">
        {renderContent()}
      </div>

      {/* Модальные окна */}
      {showCreateModal && (
        <CreateRfcModal
          isOpen={showCreateModal}
          onClose={() => setShowCreateModal(false)}
          onSubmit={handleCreateRfc}
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
                  {selectedRfc.requester ? `${selectedRfc.requester.firstName} ${selectedRfc.requester.lastName}` : 'Неизвестно'}
                </span>
              </div>
            </div>

            <div className="detail-section">
              <h3>Описание</h3>
              <p className="description-text">{selectedRfc.description || 'Нет описания'}</p>
            </div>

            {/* Информация о затронутых системах */}
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

      {/* Модальное окно изменения статуса */}
      {showStatusModal && selectedRfcForStatus && (
        <StatusActionModal
          isOpen={showStatusModal}
          onClose={() => {
            setShowStatusModal(false);
            setSelectedRfcForStatus(null);
          }}
          rfc={selectedRfcForStatus}
          currentUser={user}
          onStatusUpdate={handleStatusUpdate}
        />
      )}

      {/* Уведомления */}
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