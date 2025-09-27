import React from 'react';
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { User, Calendar, AlertCircle } from 'lucide-react';
import type { Rfc } from '../../types/api';
import { Priority } from '../../types/api';

interface RfcCardProps {
  rfc: Rfc;
  onClick?: (rfc: Rfc) => void;
}

const priorityColors: Record<Priority, string> = {
  LOW: 'border-l-green-500 bg-green-50',
  MEDIUM: 'border-l-yellow-500 bg-yellow-50',
  HIGH: 'border-l-orange-500 bg-orange-50',
  CRITICAL: 'border-l-red-500 bg-red-50'
};

const priorityTextColors: Record<Priority, string> = {
  LOW: 'text-green-700',
  MEDIUM: 'text-yellow-700',
  HIGH: 'text-orange-700',
  CRITICAL: 'text-red-700'
};

const priorityLabels: Record<Priority, string> = {
  LOW: 'Низкий',
  MEDIUM: 'Средний',
  HIGH: 'Высокий',
  CRITICAL: 'Критический'
};

export const RfcCard: React.FC<RfcCardProps> = ({ rfc, onClick }) => {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({
    id: rfc.id,
  });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('ru-RU', {
      day: '2-digit',
      month: '2-digit'
    });
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...attributes}
      {...listeners}
      className={`
        bg-white rounded-lg border-l-4 shadow-sm hover:shadow-md transition-shadow cursor-pointer p-4 mb-3
        ${priorityColors[rfc.priority]}
        ${isDragging ? 'opacity-50 shadow-lg' : ''}
      `}
      onClick={() => onClick?.(rfc)}
    >
      {/* Header with ID and Priority */}
      <div className="flex items-center justify-between mb-2">
        <span className="text-xs font-medium text-gray-500">#{rfc.id}</span>
        <div className="flex items-center space-x-1">
          {rfc.priority === 'CRITICAL' && (
            <AlertCircle size={14} className="text-red-500" />
          )}
          <span className={`text-xs font-medium ${priorityTextColors[rfc.priority]}`}>
            {priorityLabels[rfc.priority]}
          </span>
        </div>
      </div>

      {/* Title */}
      <h3 className="font-medium text-gray-900 text-sm mb-2 line-clamp-2">
        {rfc.title}
      </h3>

      {/* Description */}
      {rfc.description && (
        <p className="text-xs text-gray-600 mb-3 line-clamp-2">
          {rfc.description}
        </p>
      )}

      {/* System */}
      <div className="mb-3">
        <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
          {rfc.system.name}
        </span>
      </div>

      {/* Executors */}
      {rfc.executors.length > 0 && (
        <div className="mb-3">
          <div className="flex items-center text-xs text-gray-500 mb-1">
            <User size={12} className="mr-1" />
            <span>Исполнители:</span>
          </div>
          <div className="flex flex-wrap gap-1">
            {rfc.executors.slice(0, 2).map((executor) => (
              <div
                key={executor.id}
                className="flex items-center space-x-1 bg-gray-100 rounded-full px-2 py-1"
              >
                <div className="h-4 w-4 rounded-full bg-gray-300 flex items-center justify-center">
                  <span className="text-xs font-medium text-gray-600">
                    {executor.firstName[0]}{executor.lastName[0]}
                  </span>
                </div>
                <span className="text-xs text-gray-700 truncate max-w-16">
                  {executor.firstName}
                </span>
              </div>
            ))}
            {rfc.executors.length > 2 && (
              <div className="flex items-center justify-center h-6 w-6 rounded-full bg-gray-200 text-xs text-gray-600">
                +{rfc.executors.length - 2}
              </div>
            )}
          </div>
        </div>
      )}

      {/* Footer with date */}
      <div className="flex items-center justify-between text-xs text-gray-500 pt-2 border-t border-gray-100">
        <div className="flex items-center space-x-1">
          <Calendar size={12} />
          <span>{formatDate(rfc.createdAt)}</span>
        </div>
        <span className="text-xs text-gray-400">
          {rfc.author.firstName} {rfc.author.lastName}
        </span>
      </div>
    </div>
  );
};
