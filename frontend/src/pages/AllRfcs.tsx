import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Plus, RefreshCw } from 'lucide-react';
import { AdvancedRfcTable } from '../components/rfcs/AdvancedRfcTable';
import { AdvancedRfcFilters } from '../components/rfcs/AdvancedRfcFilters';
import { Pagination } from '../components/rfcs/Pagination';
import { CreateRfcModal } from '../components/dashboard/CreateRfcModal';
import { rfcApi } from '../api/rfcApi';
import type { Rfc, RfcFilters as RfcFiltersType, SortOptions, PaginatedResponse } from '../types/api';
import { RfcStatus } from '../types/api';
import { useUserRole } from '../hooks/useUserRole';

export const AllRfcs: React.FC = () => {
  const navigate = useNavigate();
  const { canPerformMassOperations } = useUserRole();
  const [rfcs, setRfcs] = useState<Rfc[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  
  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [total, setTotal] = useState(0);
  const [hasNext, setHasNext] = useState(false);
  const [hasPrev, setHasPrev] = useState(false);
  const pageSize = 10;
  
  // Filters and sorting - exclude "my" filter for all RFCs page
  const [filters, setFilters] = useState<RfcFiltersType>({});
  const [sortOptions, setSortOptions] = useState<SortOptions>({
    field: 'createdAt',
    direction: 'desc'
  });

  // Load RFCs
  const loadRfcs = async (page: number = currentPage) => {
    setLoading(true);
    setError(null);
    
    try {
      const response: PaginatedResponse<Rfc> = await rfcApi.getRfcs(
        page,
        pageSize,
        filters, // No "my: true" filter here
        sortOptions
      );
      
      setRfcs(response.data);
      setTotal(response.total);
      setCurrentPage(response.page);
      setTotalPages(Math.ceil(response.total / pageSize));
      setHasNext(response.hasNext);
      setHasPrev(response.hasPrev);
    } catch (err) {
      console.error('Error loading RFCs:', err);
      setError('Ошибка при загрузке RFC. Попробуйте обновить страницу.');
    } finally {
      setLoading(false);
    }
  };

  // Effects
  useEffect(() => {
    loadRfcs(1);
  }, [filters, sortOptions]);

  // Handlers
  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    loadRfcs(page);
  };

  const handleSort = (field: SortOptions['field']) => {
    setSortOptions(prev => ({
      field,
      direction: prev.field === field && prev.direction === 'asc' ? 'desc' : 'asc'
    }));
  };

  const handleFiltersChange = (newFilters: RfcFiltersType) => {
    setFilters(newFilters);
    setCurrentPage(1);
  };

  const handleClearFilters = () => {
    setFilters({});
    setCurrentPage(1);
  };

  const handleRefresh = () => {
    loadRfcs(currentPage);
  };

  const handleCreateSuccess = () => {
    loadRfcs(1); // Reload first page after creation
  };

  // RFC actions
  const handleViewRfc = (rfc: Rfc) => {
    navigate(`/rfcs/${rfc.id}`);
  };

  const handleEditRfc = (rfc: Rfc) => {
    console.log('Edit RFC:', rfc);
    // TODO: Open edit modal or navigate to edit page
  };

  const handleDeleteRfc = (rfc: Rfc) => {
    if (window.confirm(`Вы уверены, что хотите удалить RFC "${rfc.title}"?`)) {
      console.log('Delete RFC:', rfc);
      // TODO: Implement delete functionality
    }
  };

  // Mass operations handlers
  const handleMassApprove = async (rfcIds: string[]) => {
    try {
      setLoading(true);
      // Approve all selected RFCs
      await Promise.all(
        rfcIds.map(id => 
          rfcApi.changeRfcStatus(id, { newStatus: RfcStatus.APPROVED })
        )
      );
      
      // Reload current page
      await loadRfcs(currentPage);
      
      // Show success message
      alert(`Успешно одобрено ${rfcIds.length} RFC`);
    } catch (error) {
      console.error('Error approving RFCs:', error);
      alert('Ошибка при одобрении RFC. Попробуйте еще раз.');
    } finally {
      setLoading(false);
    }
  };

  const handleMassChangeStatus = async (rfcIds: string[], status: RfcStatus) => {
    try {
      setLoading(true);
      // Change status for all selected RFCs
      await Promise.all(
        rfcIds.map(id => 
          rfcApi.changeRfcStatus(id, { newStatus: status })
        )
      );
      
      // Reload current page
      await loadRfcs(currentPage);
      
      // Show success message
      alert(`Успешно изменен статус ${rfcIds.length} RFC`);
    } catch (error) {
      console.error('Error changing RFC status:', error);
      alert('Ошибка при изменении статуса RFC. Попробуйте еще раз.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Все RFC</h1>
          <p className="text-gray-600 mt-1">
            {loading ? 'Загрузка...' : `Всего найдено: ${total} RFC`}
          </p>
          {canPerformMassOperations() && (
            <p className="text-sm text-blue-600 mt-1">
              Доступны массовые операции - выделите RFC для изменения статуса
            </p>
          )}
        </div>
        
        <div className="flex items-center space-x-3">
          <button
            onClick={handleRefresh}
            disabled={loading}
            className="flex items-center px-3 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
          >
            <RefreshCw size={16} className={`mr-2 ${loading ? 'animate-spin' : ''}`} />
            Обновить
          </button>
          
          <button
            onClick={() => setIsCreateModalOpen(true)}
            className="flex items-center px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            <Plus size={16} className="mr-2" />
            Создать RFC
          </button>
        </div>
      </div>

      {/* Filters */}
      <AdvancedRfcFilters
        filters={filters}
        onFiltersChange={handleFiltersChange}
        onClearFilters={handleClearFilters}
      />

      {/* Error message */}
      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <div className="flex">
            <div className="ml-3">
              <h3 className="text-sm font-medium text-red-800">
                Ошибка загрузки
              </h3>
              <div className="mt-2 text-sm text-red-700">
                <p>{error}</p>
              </div>
              <div className="mt-4">
                <button
                  onClick={handleRefresh}
                  className="text-sm font-medium text-red-800 hover:text-red-700"
                >
                  Попробовать снова
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Loading skeleton */}
      {loading && rfcs.length === 0 && (
        <div className="bg-white rounded-lg shadow">
          <div className="animate-pulse">
            <div className="px-6 py-4 border-b border-gray-200">
              <div className="flex space-x-4">
                {[...Array(7)].map((_, i) => (
                  <div key={i} className="h-4 bg-gray-200 rounded flex-1"></div>
                ))}
              </div>
            </div>
            {[...Array(5)].map((_, i) => (
              <div key={i} className="px-6 py-4 border-b border-gray-200">
                <div className="flex space-x-4">
                  {[...Array(7)].map((_, j) => (
                    <div key={j} className="h-4 bg-gray-200 rounded flex-1"></div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Table */}
      {!loading || rfcs.length > 0 ? (
        <AdvancedRfcTable
          rfcs={rfcs}
          sortOptions={sortOptions}
          onSort={handleSort}
          onView={handleViewRfc}
          onEdit={handleEditRfc}
          onDelete={handleDeleteRfc}
          onMassApprove={canPerformMassOperations() ? handleMassApprove : undefined}
          onMassChangeStatus={canPerformMassOperations() ? handleMassChangeStatus : undefined}
        />
      ) : null}

      {/* Pagination */}
      {!loading && rfcs.length > 0 && (
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={handlePageChange}
          hasNext={hasNext}
          hasPrev={hasPrev}
        />
      )}

      {/* Create RFC Modal */}
      <CreateRfcModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        onSuccess={handleCreateSuccess}
      />
    </div>
  );
};
