import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  DndContext,
  DragOverlay,
  PointerSensor,
  useSensor,
  useSensors,
} from '@dnd-kit/core';
import type {
  DragEndEvent,
  DragOverEvent,
  DragStartEvent,
} from '@dnd-kit/core';
import { RefreshCw, Settings } from 'lucide-react';
import { KanbanColumn } from '../components/kanban/KanbanColumn';
import { RfcCard } from '../components/kanban/RfcCard';
import { rfcApi } from '../api/rfcApi';
import type { Rfc, PaginatedResponse } from '../types/api';
import { RfcStatus } from '../types/api';

// Define column configuration
const COLUMNS = [
  { id: 'REQUESTED_NEW', title: 'Новые запросы', status: 'REQUESTED_NEW' as RfcStatus },
  { id: 'WAITING', title: 'Ожидание', status: 'WAITING' as RfcStatus },
  { id: 'WAITING_FOR_CAB', title: 'Ожидание CAB', status: 'WAITING_FOR_CAB' as RfcStatus },
  { id: 'APPROVED', title: 'Одобрены', status: 'APPROVED' as RfcStatus },
  { id: 'DONE', title: 'Выполнены', status: 'DONE' as RfcStatus },
  { id: 'DECLINED', title: 'Отклонены', status: 'DECLINED' as RfcStatus },
  { id: 'CANCELLED', title: 'Отменены', status: 'CANCELLED' as RfcStatus },
];

export const Kanban: React.FC = () => {
  const navigate = useNavigate();
  const [rfcs, setRfcs] = useState<Rfc[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [activeRfc, setActiveRfc] = useState<Rfc | null>(null);

  // Sensors for drag and drop
  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 8,
      },
    })
  );

  // Load all RFCs
  const loadRfcs = async () => {
    setLoading(true);
    setError(null);
    
    try {
      // Load all RFCs without pagination for kanban view
      const response: PaginatedResponse<Rfc> = await rfcApi.getRfcs(
        1,
        1000, // Large limit to get all RFCs
        {}, // No filters
        { field: 'createdAt', direction: 'desc' }
      );
      
      setRfcs(response.data);
    } catch (err) {
      console.error('Error loading RFCs:', err);
      setError('Ошибка при загрузке RFC. Попробуйте обновить страницу.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRfcs();
  }, []);

  // Group RFCs by status
  const getRfcsByStatus = (status: RfcStatus): Rfc[] => {
    return rfcs.filter(rfc => rfc.status === status);
  };

  // Handle drag start
  const handleDragStart = (event: DragStartEvent) => {
    const { active } = event;
    const rfc = rfcs.find(r => r.id === active.id);
    setActiveRfc(rfc || null);
  };

  // Handle drag over (for visual feedback)
  const handleDragOver = (_event: DragOverEvent) => {
    // Optional: Add visual feedback during drag over
  };

  // Handle drag end
  const handleDragEnd = async (event: DragEndEvent) => {
    const { active, over } = event;
    setActiveRfc(null);

    if (!over) return;

    const rfcId = active.id as string;
    const newStatus = over.id as RfcStatus;
    
    const rfc = rfcs.find(r => r.id === rfcId);
    if (!rfc || rfc.status === newStatus) return;

    try {
      // Optimistically update the UI
      setRfcs(prevRfcs => 
        prevRfcs.map(r => 
          r.id === rfcId 
            ? { ...r, status: newStatus }
            : r
        )
      );

      // Update status on the server
      await rfcApi.changeRfcStatus(rfcId, { newStatus });
      
      // Show success message
      console.log(`RFC ${rfcId} moved to ${newStatus}`);
      
    } catch (error) {
      console.error('Error updating RFC status:', error);
      
      // Revert the optimistic update on error
      setRfcs(prevRfcs => 
        prevRfcs.map(r => 
          r.id === rfcId 
            ? { ...r, status: rfc.status } // Revert to original status
            : r
        )
      );
      
      alert('Ошибка при изменении статуса RFC. Попробуйте еще раз.');
    }
  };

  // Handle RFC click
  const handleRfcClick = (rfc: Rfc) => {
    navigate(`/rfcs/${rfc.id}`);
  };

  // Handle refresh
  const handleRefresh = () => {
    loadRfcs();
  };

  if (loading && rfcs.length === 0) {
    return (
      <div className="p-6">
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="p-6 h-full flex flex-col">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Канбан-доска</h1>
          <p className="text-gray-600 mt-1">
            {loading ? 'Загрузка...' : `Всего RFC: ${rfcs.length}`}
          </p>
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
            className="flex items-center px-3 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            <Settings size={16} className="mr-2" />
            Настройки
          </button>
        </div>
      </div>

      {/* Error message */}
      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
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

      {/* Kanban Board */}
      <div className="flex-1 overflow-hidden">
        <DndContext
          sensors={sensors}
          onDragStart={handleDragStart}
          onDragOver={handleDragOver}
          onDragEnd={handleDragEnd}
        >
          <div className="grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-7 gap-4 h-full">
            {COLUMNS.map((column) => (
              <KanbanColumn
                key={column.id}
                id={column.id}
                title={column.title}
                status={column.status}
                rfcs={getRfcsByStatus(column.status)}
                onRfcClick={handleRfcClick}
              />
            ))}
          </div>

          <DragOverlay>
            {activeRfc ? (
              <RfcCard rfc={activeRfc} />
            ) : null}
          </DragOverlay>
        </DndContext>
      </div>

      {/* Instructions */}
      <div className="mt-4 text-center text-sm text-gray-500">
        Перетащите карточки RFC между колонками для изменения статуса
      </div>
    </div>
  );
};
