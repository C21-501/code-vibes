import React, { useState, useEffect } from 'react';
import { rfcApi } from '../api/rfcApi';
import { useAuth } from '../../../auth/context/AuthContext';
import RfcTable from './RfcTable';
import RfcFilters from './RfcFilters';
import RfcModal from './RfcModal';
import CreateRfcModal from './CreateRfcModal';
import StatusActionModal from './StatusActionModal';

const RfcList = () => {
  const { user } = useAuth();
  const [rfcs, setRfcs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [filters, setFilters] = useState({
    status: '',
    urgency: '',
    requesterId: '',
    myRfc: false,
  });
  const [pagination, setPagination] = useState({
    page: 0,
    size: 20,
    totalElements: 0,
    totalPages: 0,
  });
  const [selectedRfc, setSelectedRfc] = useState(null);
  const [modalType, setModalType] = useState(null); // 'view', 'edit', 'create', 'status'

  useEffect(() => {
    loadRfcs();
  }, [filters, pagination.page]);

  const loadRfcs = async () => {
    setLoading(true);
    try {
      const params = {
        page: pagination.page,
        size: pagination.size,
        ...filters,
      };

      // Если фильтр "Мои RFC" включен
      if (filters.myRfc && user) {
        params.requesterId = user.id;
      }

      const response = await rfcApi.getRfcList(params);
      const data = response.data;

      setRfcs(data.content);
      setPagination(prev => ({
        ...prev,
        totalElements: data.totalElements,
        totalPages: data.totalPages,
      }));
    } catch (error) {
      console.error('Error loading RFCs:', error);
      // Здесь можно добавить уведомление об ошибке
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (newFilters) => {
    setFilters(newFilters);
    setPagination(prev => ({ ...prev, page: 0 }));
  };

  const handlePageChange = (newPage) => {
    setPagination(prev => ({ ...prev, page: newPage }));
  };

  const handleCreateRfc = async (rfcData) => {
    try {
      await rfcApi.createRfc(rfcData);
      setModalType(null);
      loadRfcs(); // Перезагружаем список
      // Показать уведомление об успехе
    } catch (error) {
      console.error('Error creating RFC:', error);
      // Показать уведомление об ошибке
    }
  };

  const handleUpdateRfc = async (id, rfcData) => {
    try {
      await rfcApi.updateRfc(id, rfcData);
      setModalType(null);
      loadRfcs();
      // Показать уведомление об успехе
    } catch (error) {
      console.error('Error updating RFC:', error);
      // Показать уведомление об ошибке
    }
  };

  const handleDeleteRfc = async (id) => {
    try {
      await rfcApi.deleteRfc(id);
      setModalType(null);
      loadRfcs();
      // Показать уведомление об успехе
    } catch (error) {
      console.error('Error deleting RFC:', error);
      // Показать уведомление об ошибке
    }
  };

  const handleStatusUpdate = async (id, statusData) => {
    try {
      await rfcApi.updateRfcStatus(id, statusData);
      setModalType(null);
      loadRfcs();
      // Показать уведомление об успехе
    } catch (error) {
      console.error('Error updating RFC status:', error);
      // Показать уведомление об ошибке
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-6 py-8">
        {/* Заголовок и кнопка создания */}
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold text-gray-900">Список RFC</h1>
          <button
            onClick={() => setModalType('create')}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
          >
            + Создать RFC
          </button>
        </div>

        {/* Фильтры */}
        <RfcFilters
          filters={filters}
          onChange={handleFilterChange}
        />

        {/* Таблица */}
        <RfcTable
          rfcs={rfcs}
          loading={loading}
          onView={(rfc) => {
            setSelectedRfc(rfc);
            setModalType('view');
          }}
          onEdit={(rfc) => {
            setSelectedRfc(rfc);
            setModalType('edit');
          }}
          onStatusAction={(rfc) => {
            setSelectedRfc(rfc);
            setModalType('status');
          }}
        />

        {/* Пагинация */}
        <div className="flex justify-between items-center mt-4">
          <span className="text-sm text-gray-600">
            Показано {rfcs.length} из {pagination.totalElements}
          </span>
          {/* Компонент пагинации */}
        </div>
      </div>

      {/* Модальные окна */}
      {modalType === 'view' && selectedRfc && (
        <RfcModal
          rfc={selectedRfc}
          onClose={() => setModalType(null)}
          onDelete={() => handleDeleteRfc(selectedRfc.id)}
        />
      )}

      {modalType === 'create' && (
        <CreateRfcModal
          onSave={handleCreateRfc}
          onClose={() => setModalType(null)}
        />
      )}

      {modalType === 'edit' && selectedRfc && (
        <CreateRfcModal
          rfc={selectedRfc}
          onSave={(data) => handleUpdateRfc(selectedRfc.id, data)}
          onClose={() => setModalType(null)}
        />
      )}

      {modalType === 'status' && selectedRfc && (
        <StatusActionModal
          rfc={selectedRfc}
          onStatusUpdate={(data) => handleStatusUpdate(selectedRfc.id, data)}
          onClose={() => setModalType(null)}
        />
      )}
    </div>
  );
};

export default RfcList;