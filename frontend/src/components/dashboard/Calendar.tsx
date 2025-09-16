import React from 'react';
import { Calendar as CalendarIcon, Clock } from 'lucide-react';
import { type UpcomingDeadline } from '../../types';

interface CalendarProps {
  upcomingDeadlines: UpcomingDeadline[];
  isLoading?: boolean;
}

const formatDeadline = (dateString: string) => {
  const date = new Date(dateString);
  const now = new Date();
  const diffTime = date.getTime() - now.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  
  if (diffDays < 0) {
    return { text: 'Просрочено', color: 'text-red-600' };
  } else if (diffDays === 0) {
    return { text: 'Сегодня', color: 'text-red-600' };
  } else if (diffDays === 1) {
    return { text: 'Завтра', color: 'text-orange-600' };
  } else if (diffDays <= 7) {
    return { text: `${diffDays} дн.`, color: 'text-yellow-600' };
  } else {
    return { text: date.toLocaleDateString('ru-RU'), color: 'text-gray-600' };
  }
};

export const Calendar: React.FC<CalendarProps> = ({ upcomingDeadlines, isLoading }) => {
  if (isLoading) {
    return (
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">
          Ближайшие дедлайны
        </h3>
        <div className="space-y-3">
          {[...Array(4)].map((_, i) => (
            <div key={i} className="animate-pulse">
              <div className="flex items-center space-x-3">
                <div className="w-8 h-8 bg-gray-200 rounded"></div>
                <div className="flex-1 space-y-1">
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
          Ближайшие дедлайны
        </h3>
        <CalendarIcon size={20} className="text-gray-400" />
      </div>

      <div className="space-y-3">
        {upcomingDeadlines.length === 0 ? (
          <div className="text-center py-6">
            <Clock size={32} className="text-gray-400 mx-auto mb-2" />
            <p className="text-gray-500 text-sm">Нет ближайших дедлайнов</p>
          </div>
        ) : (
          upcomingDeadlines.slice(0, 5).map((deadline) => {
            const deadlineInfo = formatDeadline(deadline.deadline);
            
            return (
              <div
                key={deadline.id}
                className="flex items-center space-x-3 p-2 rounded-lg hover:bg-gray-50 transition-colors"
              >
                <div className="flex-shrink-0">
                  <div className="w-8 h-8 bg-orange-100 rounded flex items-center justify-center">
                    <Clock size={14} className="text-orange-600" />
                  </div>
                </div>
                
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900 truncate">
                    {deadline.title}
                  </p>
                  <p className={`text-xs ${deadlineInfo.color} font-medium`}>
                    {deadlineInfo.text}
                  </p>
                </div>
              </div>
            );
          })
        )}
      </div>

      {upcomingDeadlines.length > 5 && (
        <div className="mt-4 pt-4 border-t border-gray-200">
          <button className="text-sm text-blue-600 hover:text-blue-800 font-medium">
            Показать все ({upcomingDeadlines.length})
          </button>
        </div>
      )}
    </div>
  );
};
