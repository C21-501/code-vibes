import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Clock, User, Calendar, AlertCircle, CheckCircle, XCircle } from 'lucide-react';
import { rfcApi } from '../api/rfcApi';
import { useAuth } from '../auth';
import type { Rfc, ChangeStatusRequest } from '../types/api';
import { RfcStatus } from '../types/api';

export const RfcDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [rfc, setRfc] = useState<Rfc | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actionLoading, setActionLoading] = useState(false);

  // Load RFC data
  const loadRfc = async () => {
    if (!id) return;
    
    setLoading(true);
    setError(null);
    
    try {
      const rfcData = await rfcApi.getRfcById(id);
      setRfc(rfcData);
    } catch (err) {
      console.error('Error loading RFC:', err);
      setError('Ошибка при загрузке RFC. Попробуйте обновить страницу.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRfc();
  }, [id]);

  // Status change handlers
  const handleStatusChange = async (newStatus: RfcStatus, comment?: string) => {
    if (!rfc || !id) return;
    
    setActionLoading(true);
    
    try {
      const statusData: ChangeStatusRequest = {
        newStatus,
        comment
      };
      
      const updatedRfc = await rfcApi.changeRfcStatus(id, statusData);
      setRfc(updatedRfc);
    } catch (err) {
      console.error('Error changing status:', err);
      alert('Ошибка при изменении статуса RFC');
    } finally {
      setActionLoading(false);
    }
  };

  const handleConfirmReadiness = async (teamId: string) => {
    if (!rfc || !id) return;
    
    setActionLoading(true);
    
    try {
      const updatedRfc = await rfcApi.confirmExecutorReadiness(id, teamId);
      setRfc(updatedRfc);
    } catch (err) {
      console.error('Error confirming readiness:', err);
      alert('Ошибка при подтверждении готовности');
    } finally {
      setActionLoading(false);
    }
  };

  // Check user permissions
  const canChangeStatus = (_targetStatus: RfcStatus): boolean => {
    if (!user || !rfc) return false;
    
    
    // For simplicity, allow any authenticated user to change status
    // In real implementation, check user roles from JWT token
    return true;
  };

  const canConfirmExecutorReadiness = (_teamId: string): boolean => {
    if (!user || !rfc) return false;
    
    // For simplicity, allow any authenticated user to confirm readiness
    // In real implementation, check if user is team leader
    return true;
  };

  // Get status color
  const getStatusColor = (status: RfcStatus): string => {
    switch (status) {
      case RfcStatus.REQUESTED_NEW:
        return 'bg-blue-100 text-blue-800';
      case RfcStatus.WAITING:
        return 'bg-yellow-100 text-yellow-800';
      case RfcStatus.APPROVED:
        return 'bg-green-100 text-green-800';
      case RfcStatus.WAITING_FOR_CAB:
        return 'bg-purple-100 text-purple-800';
      case RfcStatus.DECLINED:
        return 'bg-red-100 text-red-800';
      case RfcStatus.DONE:
        return 'bg-emerald-100 text-emerald-800';
      case RfcStatus.CANCELLED:
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  // Get status label
  const getStatusLabel = (status: RfcStatus): string => {
    switch (status) {
      case RfcStatus.REQUESTED_NEW:
        return 'Новый запрос';
      case RfcStatus.WAITING:
        return 'Ожидает рассмотрения';
      case RfcStatus.APPROVED:
        return 'Одобрен';
      case RfcStatus.WAITING_FOR_CAB:
        return 'Ожидает CAB';
      case RfcStatus.DECLINED:
        return 'Отклонен';
      case RfcStatus.DONE:
        return 'Выполнен';
      case RfcStatus.CANCELLED:
        return 'Отменен';
      default:
        return status;
    }
  };

  if (loading) {
    return (
      <div className="p-6">
        <div className="animate-pulse">
          <div className="h-8 bg-gray-200 rounded w-1/4 mb-4"></div>
          <div className="h-4 bg-gray-200 rounded w-1/2 mb-8"></div>
          <div className="space-y-4">
            <div className="h-4 bg-gray-200 rounded"></div>
            <div className="h-4 bg-gray-200 rounded w-3/4"></div>
            <div className="h-4 bg-gray-200 rounded w-1/2"></div>
          </div>
        </div>
      </div>
    );
  }

  if (error || !rfc) {
    return (
      <div className="p-6">
        <button
          onClick={() => navigate(-1)}
          className="flex items-center text-gray-600 hover:text-gray-900 mb-4"
        >
          <ArrowLeft size={20} className="mr-2" />
          Назад
        </button>
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <div className="flex">
            <AlertCircle className="h-5 w-5 text-red-400" />
            <div className="ml-3">
              <h3 className="text-sm font-medium text-red-800">
                Ошибка загрузки RFC
              </h3>
              <div className="mt-2 text-sm text-red-700">
                <p>{error || 'RFC не найден'}</p>
              </div>
              <div className="mt-4">
                <button
                  onClick={loadRfc}
                  className="text-sm font-medium text-red-800 hover:text-red-700"
                >
                  Попробовать снова
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="p-6 max-w-4xl mx-auto">
      {/* Header */}
      <div className="mb-6">
        <button
          onClick={() => navigate(-1)}
          className="flex items-center text-gray-600 hover:text-gray-900 mb-4"
        >
          <ArrowLeft size={20} className="mr-2" />
          Назад
        </button>
        
        <div className="flex items-start justify-between">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">{rfc.title}</h1>
            <div className="flex items-center space-x-4 text-sm text-gray-600">
              <div className="flex items-center">
                <User size={16} className="mr-1" />
                {rfc.author.firstName} {rfc.author.lastName}
              </div>
              <div className="flex items-center">
                <Calendar size={16} className="mr-1" />
                {new Date(rfc.createdAt).toLocaleDateString('ru-RU')}
              </div>
              <div className="flex items-center">
                <Clock size={16} className="mr-1" />
                ID: {rfc.id}
              </div>
            </div>
          </div>
          
          <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(rfc.status)}`}>
            {getStatusLabel(rfc.status)}
          </span>
        </div>
      </div>

      {/* Content */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main content */}
        <div className="lg:col-span-2 space-y-6">
          {/* Description */}
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Описание</h2>
            <div className="prose max-w-none text-gray-700">
              {rfc.description ? (
                <p className="whitespace-pre-wrap">{rfc.description}</p>
              ) : (
                <p className="text-gray-500 italic">Описание не указано</p>
              )}
            </div>
          </div>

          {/* Action buttons */}
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Действия</h2>
            <div className="flex flex-wrap gap-3">
              {/* Status change buttons */}
              {canChangeStatus(RfcStatus.APPROVED) && rfc.status === RfcStatus.WAITING && (
                <button
                  onClick={() => handleStatusChange(RfcStatus.APPROVED)}
                  disabled={actionLoading}
                  className="flex items-center px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50"
                >
                  <CheckCircle size={16} className="mr-2" />
                  Одобрить
                </button>
              )}
              
              {canChangeStatus(RfcStatus.DECLINED) && (rfc.status === RfcStatus.WAITING || rfc.status === RfcStatus.WAITING_FOR_CAB) && (
                <button
                  onClick={() => handleStatusChange(RfcStatus.DECLINED)}
                  disabled={actionLoading}
                  className="flex items-center px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 disabled:opacity-50"
                >
                  <XCircle size={16} className="mr-2" />
                  Отклонить
                </button>
              )}
              
              {canChangeStatus(RfcStatus.DONE) && rfc.status === RfcStatus.WAITING_FOR_CAB && (
                <button
                  onClick={() => handleStatusChange(RfcStatus.DONE)}
                  disabled={actionLoading}
                  className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
                >
                  <CheckCircle size={16} className="mr-2" />
                  Завершить
                </button>
              )}
              
              {canChangeStatus(RfcStatus.CANCELLED) && (
                <button
                  onClick={() => handleStatusChange(RfcStatus.CANCELLED)}
                  disabled={actionLoading}
                  className="flex items-center px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700 disabled:opacity-50"
                >
                  <XCircle size={16} className="mr-2" />
                  Отменить
                </button>
              )}

              {/* Executor confirmation buttons */}
              {rfc.status === RfcStatus.APPROVED && rfc.executors && rfc.executors.map((executor) => (
                canConfirmExecutorReadiness(executor.id) && (
                  <button
                    key={executor.id}
                    onClick={() => handleConfirmReadiness(executor.id)}
                    disabled={actionLoading}
                    className="flex items-center px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 disabled:opacity-50"
                  >
                    <CheckCircle size={16} className="mr-2" />
                    Подтвердить готовность ({executor.firstName})
                  </button>
                )
              ))}
            </div>
          </div>
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          {/* Details */}
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Детали</h2>
            <div className="space-y-3">
              <div>
                <dt className="text-sm font-medium text-gray-500">Приоритет</dt>
                <dd className="text-sm text-gray-900 mt-1">{rfc.priority}</dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Система</dt>
                <dd className="text-sm text-gray-900 mt-1">{rfc.system?.name || 'Не указана'}</dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Создано</dt>
                <dd className="text-sm text-gray-900 mt-1">
                  {new Date(rfc.createdAt).toLocaleString('ru-RU')}
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Обновлено</dt>
                <dd className="text-sm text-gray-900 mt-1">
                  {new Date(rfc.updatedAt).toLocaleString('ru-RU')}
                </dd>
              </div>
            </div>
          </div>

          {/* Executors */}
          {rfc.executors && rfc.executors.length > 0 && (
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-lg font-semibold text-gray-900 mb-4">Исполнители</h2>
              <div className="space-y-2">
                {rfc.executors.map((executor) => (
                  <div key={executor.id} className="flex items-center space-x-3">
                    <div className="flex-1">
                      <p className="text-sm font-medium text-gray-900">
                        {executor.firstName} {executor.lastName}
                      </p>
                      <p className="text-sm text-gray-500">{executor.email}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Reviewers */}
          {rfc.reviewers && rfc.reviewers.length > 0 && (
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-lg font-semibold text-gray-900 mb-4">Рецензенты</h2>
              <div className="space-y-2">
                {rfc.reviewers.map((reviewer) => (
                  <div key={reviewer.id} className="flex items-center space-x-3">
                    <div className="flex-1">
                      <p className="text-sm font-medium text-gray-900">
                        {reviewer.firstName} {reviewer.lastName}
                      </p>
                      <p className="text-sm text-gray-500">{reviewer.email}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
