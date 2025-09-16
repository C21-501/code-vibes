import React from 'react';
import { useDroppable } from '@dnd-kit/core';
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { RfcCard } from './RfcCard';
import type { Rfc } from '../../types/api';
import { RfcStatus } from '../../types/api';

interface KanbanColumnProps {
  id: string;
  title: string;
  status: RfcStatus;
  rfcs: Rfc[];
  onRfcClick?: (rfc: Rfc) => void;
}

const statusColors: Record<RfcStatus, string> = {
  REQUESTED_NEW: 'bg-blue-500',
  WAITING: 'bg-yellow-500',
  WAITING_FOR_CAB: 'bg-orange-500',
  APPROVED: 'bg-green-500',
  DECLINED: 'bg-red-500',
  DONE: 'bg-gray-500',
  CANCELLED: 'bg-gray-400'
};

export const KanbanColumn: React.FC<KanbanColumnProps> = ({
  id,
  title,
  status,
  rfcs,
  onRfcClick
}) => {
  const {
    isOver,
    setNodeRef
  } = useDroppable({
    id,
  });

  return (
    <div className="flex flex-col h-full bg-gray-50 rounded-lg">
      {/* Column Header */}
      <div className="flex items-center justify-between p-4 border-b border-gray-200">
        <div className="flex items-center space-x-2">
          <div className={`w-3 h-3 rounded-full ${statusColors[status]}`}></div>
          <h3 className="font-medium text-gray-900">{title}</h3>
          <span className="inline-flex items-center justify-center w-6 h-6 text-xs font-medium text-gray-500 bg-gray-200 rounded-full">
            {rfcs.length}
          </span>
        </div>
      </div>

      {/* Column Content */}
      <div
        ref={setNodeRef}
        className={`
          flex-1 p-4 overflow-y-auto
          ${isOver ? 'bg-blue-50 border-2 border-dashed border-blue-300' : ''}
        `}
        style={{ minHeight: '200px' }}
      >
        <SortableContext
          items={rfcs.map(rfc => rfc.id)}
          strategy={verticalListSortingStrategy}
        >
          {rfcs.map((rfc) => (
            <RfcCard
              key={rfc.id}
              rfc={rfc}
              onClick={onRfcClick}
            />
          ))}
          
          {rfcs.length === 0 && (
            <div className="flex items-center justify-center h-32 text-gray-400 text-sm">
              Нет RFC в этом статусе
            </div>
          )}
        </SortableContext>
      </div>
    </div>
  );
};
