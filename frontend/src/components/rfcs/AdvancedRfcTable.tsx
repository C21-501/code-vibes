import React, { useState } from 'react';
import { ChevronUp, ChevronDown, MoreHorizontal, Eye, Edit, Trash2, Check } from 'lucide-react';
import type { Rfc, SortOptions } from '../../types/api';
import { RfcStatus, Priority } from '../../types/api';
import { useUserRole } from '../../hooks/useUserRole';

interface AdvancedRfcTableProps {
  rfcs: Rfc[];
  sortOptions?: SortOptions;
  onSort: (field: SortOptions['field']) => void;
  onView: (rfc: Rfc) => void;
  onEdit: (rfc: Rfc) => void;
  onDelete: (rfc: Rfc) => void;
  onMassApprove?: (rfcIds: string[]) => void;
  onMassChangeStatus?: (rfcIds: string[], status: RfcStatus) => void;
}

const statusLabels: Record<RfcStatus, string> = {
  REQUESTED_NEW: 'Новый запрос',
  WAITING: 'Ожидание',
  WAITING_FOR_CAB: 'Ожидание CAB',
  APPROVED: 'Одобрен',
  DECLINED: 'Отклонен',
  DONE: 'Выполнен',
  CANCELLED: 'Отменен'
};

const statusColors: Record<RfcStatus, string> = {
  REQUESTED_NEW: 'bg-blue-100 text-blue-800',
  WAITING: 'bg-yellow-100 text-yellow-800',
  WAITING_FOR_CAB: 'bg-orange-100 text-orange-800',
  APPROVED: 'bg-green-100 text-green-800',
  DECLINED: 'bg-red-100 text-red-800',
  DONE: 'bg-gray-100 text-gray-800',
  CANCELLED: 'bg-gray-100 text-gray-800'
};

const priorityLabels: Record<Priority, string> = {
  LOW: 'Низкий',
  MEDIUM: 'Средний',
  HIGH: 'Высокий',
  CRITICAL: 'Критический'
};

const priorityColors: Record<Priority, string> = {
  LOW: 'text-green-600',
  MEDIUM: 'text-yellow-600',
  HIGH: 'text-orange-600',
  CRITICAL: 'text-red-600'
};

export const AdvancedRfcTable: React.FC<AdvancedRfcTableProps> = ({
  rfcs,
  sortOptions,
  onSort,
  onView,
  onEdit,
  onDelete,
  onMassApprove,
  onMassChangeStatus
}) => {
  const { canPerformMassOperations } = useUserRole();
  const [selectedRfcs, setSelectedRfcs] = useState<Set<string>>(new Set());
  const [showMassActions, setShowMassActions] = useState(false);

  const handleSelectAll = (checked: boolean) => {
    if (checked) {
      setSelectedRfcs(new Set(rfcs.map(rfc => rfc.id)));
    } else {
      setSelectedRfcs(new Set());
    }
  };

  const handleSelectRfc = (rfcId: string, checked: boolean) => {
    const newSelected = new Set(selectedRfcs);
    if (checked) {
      newSelected.add(rfcId);
    } else {
      newSelected.delete(rfcId);
    }
    setSelectedRfcs(newSelected);
  };

  const handleMassApprove = () => {
    if (onMassApprove && selectedRfcs.size > 0) {
      onMassApprove(Array.from(selectedRfcs));
      setSelectedRfcs(new Set());
    }
  };

  const handleMassChangeStatus = (status: RfcStatus) => {
    if (onMassChangeStatus && selectedRfcs.size > 0) {
      onMassChangeStatus(Array.from(selectedRfcs), status);
      setSelectedRfcs(new Set());
      setShowMassActions(false);
    }
  };

  const SortButton: React.FC<{ field: SortOptions['field']; children: React.ReactNode }> = ({ field, children }) => {
    const isActive = sortOptions?.field === field;
    const isAsc = isActive && sortOptions?.direction === 'asc';

    return (
      <button
        onClick={() => onSort(field)}
        className="flex items-center space-x-1 hover:text-gray-900 font-medium"
      >
        <span>{children}</span>
        <div className="flex flex-col">
          <ChevronUp 
            size={12} 
            className={`${isActive && isAsc ? 'text-blue-600' : 'text-gray-400'}`} 
          />
          <ChevronDown 
            size={12} 
            className={`${isActive && !isAsc ? 'text-blue-600' : 'text-gray-400'} -mt-1`} 
          />
        </div>
      </button>
    );
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('ru-RU', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  };

  const ActionMenu: React.FC<{ rfc: Rfc }> = ({ rfc }) => {
    const [isOpen, setIsOpen] = useState(false);

    return (
      <div className="relative">
        <button
          onClick={() => setIsOpen(!isOpen)}
          className="p-1 rounded hover:bg-gray-100 transition-colors"
        >
          <MoreHorizontal size={16} className="text-gray-500" />
        </button>
        
        {isOpen && (
          <>
            <div 
              className="fixed inset-0 z-10" 
              onClick={() => setIsOpen(false)}
            />
            <div className="absolute right-0 mt-1 w-32 bg-white rounded-md shadow-lg border border-gray-200 z-20">
              <div className="py-1">
                <button
                  onClick={() => {
                    onView(rfc);
                    setIsOpen(false);
                  }}
                  className="flex items-center w-full px-3 py-2 text-sm text-gray-700 hover:bg-gray-100"
                >
                  <Eye size={14} className="mr-2" />
                  Просмотр
                </button>
                <button
                  onClick={() => {
                    onEdit(rfc);
                    setIsOpen(false);
                  }}
                  className="flex items-center w-full px-3 py-2 text-sm text-gray-700 hover:bg-gray-100"
                >
                  <Edit size={14} className="mr-2" />
                  Редактировать
                </button>
                <button
                  onClick={() => {
                    onDelete(rfc);
                    setIsOpen(false);
                  }}
                  className="flex items-center w-full px-3 py-2 text-sm text-red-600 hover:bg-red-50"
                >
                  <Trash2 size={14} className="mr-2" />
                  Удалить
                </button>
              </div>
            </div>
          </>
        )}
      </div>
    );
  };

  const MassActionsBar: React.FC = () => {
    if (!canPerformMassOperations() || selectedRfcs.size === 0) {
      return null;
    }

    return (
      <div className="bg-blue-50 border-l-4 border-blue-400 p-4 mb-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center">
            <Check size={16} className="text-blue-600 mr-2" />
            <span className="text-sm font-medium text-blue-800">
              Выбрано: {selectedRfcs.size} RFC
            </span>
          </div>
          <div className="flex items-center space-x-2">
            <button
              onClick={handleMassApprove}
              className="px-3 py-1 text-sm font-medium text-white bg-green-600 rounded hover:bg-green-700 transition-colors"
            >
              Одобрить выбранные
            </button>
            <div className="relative">
              <button
                onClick={() => setShowMassActions(!showMassActions)}
                className="px-3 py-1 text-sm font-medium text-blue-700 bg-white border border-blue-300 rounded hover:bg-blue-50 transition-colors"
              >
                Изменить статус
              </button>
              {showMassActions && (
                <>
                  <div 
                    className="fixed inset-0 z-10" 
                    onClick={() => setShowMassActions(false)}
                  />
                  <div className="absolute right-0 mt-1 w-48 bg-white rounded-md shadow-lg border border-gray-200 z-20">
                    <div className="py-1">
                      {Object.entries(statusLabels).map(([status, label]) => (
                        <button
                          key={status}
                          onClick={() => handleMassChangeStatus(status as RfcStatus)}
                          className="flex items-center w-full px-3 py-2 text-sm text-gray-700 hover:bg-gray-100"
                        >
                          <span className={`inline-block w-3 h-3 rounded-full mr-2 ${statusColors[status as RfcStatus].replace('text-', 'bg-').replace('800', '600')}`}></span>
                          {label}
                        </button>
                      ))}
                    </div>
                  </div>
                </>
              )}
            </div>
            <button
              onClick={() => setSelectedRfcs(new Set())}
              className="px-3 py-1 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded hover:bg-gray-50 transition-colors"
            >
              Отменить
            </button>
          </div>
        </div>
      </div>
    );
  };

  const isAllSelected = rfcs.length > 0 && selectedRfcs.size === rfcs.length;
  const isIndeterminate = selectedRfcs.size > 0 && selectedRfcs.size < rfcs.length;

  return (
    <div>
      <MassActionsBar />
      
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                {canPerformMassOperations() && (
                  <th className="px-6 py-3 text-left">
                    <input
                      type="checkbox"
                      checked={isAllSelected}
                      ref={(input) => {
                        if (input) {
                          input.indeterminate = isIndeterminate;
                        }
                      }}
                      onChange={(e) => handleSelectAll(e.target.checked)}
                      className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                    />
                  </th>
                )}
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  ID
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  <SortButton field="title">Название</SortButton>
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  <SortButton field="createdAt">Дата создания</SortButton>
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  <SortButton field="status">Статус</SortButton>
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  <SortButton field="priority">Приоритет</SortButton>
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Автор
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Исполнители
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Действия
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {rfcs.map((rfc) => (
                <tr key={rfc.id} className="hover:bg-gray-50">
                  {canPerformMassOperations() && (
                    <td className="px-6 py-4 whitespace-nowrap">
                      <input
                        type="checkbox"
                        checked={selectedRfcs.has(rfc.id)}
                        onChange={(e) => handleSelectRfc(rfc.id, e.target.checked)}
                        className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                      />
                    </td>
                  )}
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    #{rfc.id}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-medium text-gray-900">{rfc.title}</div>
                    <div className="text-sm text-gray-500 truncate max-w-xs">
                      {rfc.description}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {formatDate(rfc.createdAt)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${statusColors[rfc.status]}`}>
                      {statusLabels[rfc.status]}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`text-sm font-medium ${priorityColors[rfc.priority]}`}>
                      {priorityLabels[rfc.priority]}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <div className="flex items-center">
                      <div className="h-8 w-8 rounded-full bg-gray-200 flex items-center justify-center mr-2">
                        <span className="text-xs font-medium text-gray-600">
                          {rfc?.author?.firstName[0]}{rfc?.author?.lastName[0]}
                        </span>
                      </div>
                      <span>{rfc?.author?.fullName}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {rfc?.executors?.length > 0 ? (
                      <div className="flex flex-wrap gap-1">
                        {rfc.executors.slice(0, 2).map((executor) => (
                          <span
                            key={executor.id}
                            className="inline-flex px-2 py-1 text-xs bg-blue-100 text-blue-800 rounded-full"
                          >
                            {executor.fullName}
                          </span>
                        ))}
                        {rfc.executors.length > 2 && (
                          <span className="inline-flex px-2 py-1 text-xs bg-gray-100 text-gray-600 rounded-full">
                            +{rfc.executors.length - 2}
                          </span>
                        )}
                      </div>
                    ) : (
                      <span className="text-gray-400">Не назначены</span>
                    )}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <ActionMenu rfc={rfc} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        
        {rfcs.length === 0 && (
          <div className="text-center py-12">
            <div className="text-gray-500">
              <p className="text-lg font-medium">RFC не найдены</p>
              <p className="text-sm mt-1">Попробуйте изменить фильтры поиска</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
