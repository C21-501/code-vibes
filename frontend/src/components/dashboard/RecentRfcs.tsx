import React from 'react';
import { FileText, Clock, User } from 'lucide-react';
import { type RecentRfc, RfcStatus, Priority } from '../../types';

interface RecentRfcsProps {
  rfcs: RecentRfc[];
  isLoading?: boolean;
}

const statusColors = {
  [RfcStatus.REQUESTED_NEW]: 'bg-gray-100 text-gray-800',
  [RfcStatus.WAITING]: 'bg-yellow-100 text-yellow-800',
  [RfcStatus.APPROVED]: 'bg-green-100 text-green-800',
  [RfcStatus.DONE]: 'bg-blue-100 text-blue-800',
  [RfcStatus.DECLINED]: 'bg-red-100 text-red-800',
  [RfcStatus.CANCELLED]: 'bg-red-100 text-red-800',
};

const statusLabels = {
  [RfcStatus.REQUESTED_NEW]: 'Новый запрос',
  [RfcStatus.WAITING]: 'На рассмотрении',
  [RfcStatus.APPROVED]: 'Одобрено',
  [RfcStatus.DONE]: 'Выполнено',
  [RfcStatus.DECLINED]: 'Отклонено',
  [RfcStatus.CANCELLED]: 'Отменено',
};

const priorityColors = {
  [Priority.LOW]: 'text-gray-500',
  [Priority.MEDIUM]: 'text-yellow-500',
  [Priority.HIGH]: 'text-orange-500',
  [Priority.CRITICAL]: 'text-red-500',
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('ru-RU', {
    day: 'numeric',
    month: 'short',
    hour: '2-digit',
    minute: '2-digit',
  });
};

export const RecentRfcs: React.FC<RecentRfcsProps> = ({ rfcs, isLoading }) => {
  if (isLoading) {
    return (
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">
          Последние RFC
        </h3>
        <div className="space-y-4">
          {[...Array(3)].map((_, i) => (
            <div key={i} className="animate-pulse">
              <div className="flex items-start space-x-3">
                <div className="w-10 h-10 bg-gray-200 rounded-lg"></div>
                <div className="flex-1 space-y-2">
                  <div className="h-4 bg-gray-200 rounded w-3/4"></div>
                  <div className="h-3 bg-gray-200 rounded w-1/2"></div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold text-gray-900">
          Последние RFC
        </h3>
        <button className="text-sm text-blue-600 hover:text-blue-800 font-medium">
          Посмотреть все
        </button>
      </div>

      <div className="space-y-4">
        {rfcs.length === 0 ? (
          <div className="text-center py-8">
            <FileText size={48} className="text-gray-400 mx-auto mb-4" />
            <p className="text-gray-500">Нет доступных RFC</p>
          </div>
        ) : (
          rfcs.map((rfc) => (
            <div
              key={rfc.id}
              className="flex items-start space-x-3 p-3 rounded-lg hover:bg-gray-50 transition-colors cursor-pointer"
            >
              <div className="flex-shrink-0">
                <div className="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                  <FileText size={20} className="text-blue-600" />
                </div>
              </div>
              
              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between mb-1">
                  <h4 className="text-sm font-medium text-gray-900 truncate">
                    {rfc.title}
                  </h4>
                  <div className={`w-2 h-2 rounded-full ${priorityColors[rfc.priority]}`} />
                </div>
                
                <div className="flex items-center space-x-4 text-xs text-gray-500">
                  <div className="flex items-center space-x-1">
                    <User size={12} />
                    <span>{rfc.author}</span>
                  </div>
                  <div className="flex items-center space-x-1">
                    <Clock size={12} />
                    <span>{formatDate(rfc.createdAt)}</span>
                  </div>
                </div>
                
                <div className="mt-2">
                  <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${
                    statusColors[rfc.status]
                  }`}>
                    {statusLabels[rfc.status]}
                  </span>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
