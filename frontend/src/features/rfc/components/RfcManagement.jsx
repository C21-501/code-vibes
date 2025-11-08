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
import LoadingSpinner from '../../../shared/components/LoadingSpinner';
import Toast from '../../../shared/components/Toast';
import './RfcManagement.css';

const RfcManagement = () => {
  const { user } = useAuth();
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [selectedRfc, setSelectedRfc] = useState(null);
  const [showViewModal, setShowViewModal] = useState(false);
  const [toast, setToast] = useState({ show: false, type: '', title: '', message: '' });

  // Используем хук для управления RFC
  const {
    rfcs,
    loading,
    error,
    pagination,
    filters,
    updateFilters,
    goToPage,
    refetch
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
      await rfcApi.deleteRfc(rfcId);
      showToast('success', 'Успех', 'RFC успешно удален');
      refetch(); // Перезагружаем список
    } catch (error) {
      const errorMessage = error.response?.data?.errors?.[0]?.message || 'Не удалось удалить RFC';
      showToast('error', 'Ошибка', errorMessage);
    }
  };

  const handleStatusAction = async (rfcId, action, comment = '') => {
    try {
      // В зависимости от действия вызываем соответствующий метод API
      if (action === 'APPROVE') {
        await rfcApi.approveRfc(rfcId, { comment });
      } else if (action === 'UNAPPROVE') {
        await rfcApi.unapproveRfc(rfcId, { comment });
      }
      // Добавьте другие действия по необходимости

      showToast('success', 'Успех', 'Статус RFC успешно обновлен');
      refetch(); // Перезагружаем список
      setShowViewModal(false); // Закрываем модалку просмотра
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
          onClose={() => setShowCreateModal(false)}
          onCreate={handleCreateRfc}
        />
      )}

      {showViewModal && selectedRfc && (
        <RfcModal
          rfc={selectedRfc}
          onClose={() => setShowViewModal(false)}
          onStatusAction={handleStatusAction}
          currentUser={user}
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