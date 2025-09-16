import React, { useState } from 'react';
import { 
  FileText, 
  Clock, 
  CheckCircle, 
  XCircle, 
  AlertCircle,
  Plus 
} from 'lucide-react';
import { useRfcStats } from '../hooks';
import { 
  StatsCard, 
  RecentRfcs, 
  Calendar, 
  CreateRfcModal 
} from '../components/dashboard';
import { RfcStatus, Priority } from '../types';

export const Dashboard: React.FC = () => {
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const { data, isLoading, error } = useRfcStats();

  // Mock data for development (will be replaced with real data from API)
  const mockData = {
    stats: {
      total: 42,
      draft: 8,
      review: 12,
      approved: 15,
      implemented: 5,
      rejected: 2
    },
    recentRfcs: [
      {
        id: '1',
        title: 'Внедрение микросервисной архитектуры',
        status: RfcStatus.WAITING,
        createdAt: '2024-01-15T10:30:00Z',
        author: 'Иван Петров',
        priority: Priority.HIGH
      },
      {
        id: '2',
        title: 'Обновление системы аутентификации',
        status: RfcStatus.APPROVED,
        createdAt: '2024-01-14T14:20:00Z',
        author: 'Мария Сидорова',
        priority: Priority.MEDIUM
      },
      {
        id: '3',
        title: 'Оптимизация базы данных',
        status: RfcStatus.REQUESTED_NEW,
        createdAt: '2024-01-13T09:15:00Z',
        author: 'Алексей Козлов',
        priority: Priority.LOW
      }
    ],
    upcomingDeadlines: [
      {
        id: '1',
        title: 'Ревью RFC микросервисов',
        deadline: '2024-01-20T18:00:00Z',
        status: RfcStatus.WAITING
      },
      {
        id: '2',
        title: 'Финальная проверка аутентификации',
        deadline: '2024-01-22T12:00:00Z',
        status: RfcStatus.APPROVED
      }
    ]
  };

  const stats = data?.data?.stats || mockData.stats;
  const recentRfcs = data?.data?.recentRfcs || mockData.recentRfcs;
  const upcomingDeadlines = data?.data?.upcomingDeadlines || mockData.upcomingDeadlines;

  if (error) {
    console.error('Error loading dashboard data:', error);
    // Continue with mock data for development
  }

  return (
    <div className="p-6">
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Дашборд</h1>
          <p className="text-gray-600 mt-1">
            Обзор состояния RFC и текущих задач
          </p>
        </div>
        <button
          onClick={() => setIsCreateModalOpen(true)}
          className="inline-flex items-center px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
        >
          <Plus size={16} className="mr-2" />
          Создать RFC
        </button>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-6 mb-8">
        <StatsCard
          title="Всего RFC"
          value={stats.total}
          icon={FileText}
          color="blue"
          trend={{ value: 12, isPositive: true }}
        />
        <StatsCard
          title="Черновики"
          value={stats.draft}
          icon={AlertCircle}
          color="gray"
        />
        <StatsCard
          title="На рассмотрении"
          value={stats.review}
          icon={Clock}
          color="yellow"
        />
        <StatsCard
          title="Одобрено"
          value={stats.approved}
          icon={CheckCircle}
          color="green"
          trend={{ value: 8, isPositive: true }}
        />
        <StatsCard
          title="Отклонено"
          value={stats.rejected}
          icon={XCircle}
          color="red"
        />
      </div>

      {/* Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Recent RFCs - takes 2 columns */}
        <div className="lg:col-span-2">
          <RecentRfcs rfcs={recentRfcs} isLoading={isLoading} />
        </div>

        {/* Calendar - takes 1 column */}
        <div>
          <Calendar upcomingDeadlines={upcomingDeadlines} isLoading={isLoading} />
        </div>
      </div>

      {/* Create RFC Modal */}
      <CreateRfcModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
      />
    </div>
  );
};
