import React, { useState, useEffect } from 'react';
import { X, FileText, Users, Calendar } from 'lucide-react';
import { rfcApi } from '../../api/rfcApi';
import type { CreateRfcRequest, System, Team, User } from '../../types/api';
import { Priority } from '../../types/api';

interface CreateRfcModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess?: () => void;
}

interface FormData {
  title: string;
  description: string;
  systemId: string;
  executorIds: string[];
  reviewerIds: string[];
  updatedAt: string;
  priority: Priority | '';
}

export const CreateRfcModal: React.FC<CreateRfcModalProps> = ({ 
  isOpen, 
  onClose, 
  onSuccess 
}) => {
  const [formData, setFormData] = useState<FormData>({
    title: '',
    description: '',
    systemId: '',
    executorIds: [],
    reviewerIds: [],
    updatedAt: '',
    priority: ''
  });
  
  const [systems, setSystems] = useState<System[]>([]);
  const [teams, setTeams] = useState<Team[]>([]);
  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(false);

  // Загрузка данных при открытии модального окна
  useEffect(() => {
    if (isOpen) {
      loadData();
    }
  }, [isOpen]);

  const loadData = async () => {
    setLoadingData(true);
    try {
      const [systemsData, teamsData] = await Promise.all([
        rfcApi.getSystems(),
        rfcApi.getTeams()
      ]);
      setSystems(systemsData);
      setTeams(teamsData);
    } catch (error) {
      console.error('Error loading data:', error);
    } finally {
      setLoadingData(false);
    }
  };

  const resetForm = () => {
    setFormData({
      title: '',
      description: '',
      systemId: '',
      executorIds: [],
      reviewerIds: [],
      updatedAt: '',
      priority: ''
    });
  };

  const handleClose = () => {
    resetForm();
    onClose();
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.priority) {
      alert('Пожалуйста, выберите приоритет');
      return;
    }

    setLoading(true);
    try {
      const rfcData: CreateRfcRequest = {
        title: formData.title,
        description: formData.description,
        systemId: formData.systemId,
        executorIds: formData.executorIds,
        reviewerIds: formData.reviewerIds,
        updatedAt: formData.updatedAt,
        priority: formData.priority as Priority
      };

      await rfcApi.createRfc(rfcData);
      onSuccess?.();
      handleClose();
    } catch (error) {
      console.error('Error creating RFC:', error);
      alert('Ошибка при создании RFC. Попробуйте снова.');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (field: keyof FormData, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleMultiSelectChange = (field: 'executorIds' | 'reviewerIds', userId: string) => {
    setFormData(prev => ({
      ...prev,
      [field]: prev[field].includes(userId)
        ? prev[field].filter(id => id !== userId)
        : [...prev[field], userId]
    }));
  };

  const getUsersFromTeams = (): User[] => {
    const users: User[] = [];
    const userIds = new Set<string>();
    
    teams.forEach(team => {
      team.members.forEach(user => {
        if (!userIds.has(user.id)) {
          userIds.add(user.id);
          users.push(user);
        }
      });
    });
    
    return users;
  };

  const allUsers = getUsersFromTeams();

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      {/* Backdrop */}
      <div 
        className="fixed inset-0 bg-black bg-opacity-50 transition-opacity"
        onClick={handleClose}
      />
      
      {/* Modal */}
      <div className="flex min-h-full items-center justify-center p-4">
        <div className="relative bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-hidden">
          {/* Header */}
          <div className="flex items-center justify-between p-6 border-b border-gray-200">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                <FileText size={20} className="text-blue-600" />
              </div>
              <h3 className="text-lg font-semibold text-gray-900">
                Создать новый RFC
              </h3>
            </div>
            <button
              onClick={handleClose}
              className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
            >
              <X size={20} className="text-gray-500" />
            </button>
          </div>

          {/* Content */}
          <div className="overflow-y-auto max-h-[calc(90vh-140px)]">
            <form onSubmit={handleSubmit} className="p-6">
              {loadingData ? (
                <div className="text-center py-8">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
                  <p className="mt-2 text-gray-500">Загрузка данных...</p>
                </div>
              ) : (
                <div className="space-y-6">
                  {/* Основная информация */}
                  <div className="space-y-4">
                    <h4 className="text-md font-medium text-gray-900 flex items-center">
                      <FileText size={18} className="mr-2" />
                      Основная информация
                    </h4>
                    
                    <div>
                      <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-1">
                        Название RFC *
                      </label>
                      <input
                        type="text"
                        id="title"
                        value={formData.title}
                        onChange={(e) => handleInputChange('title', e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        placeholder="Введите название RFC"
                        required
                      />
                    </div>

                    <div>
                      <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-1">
                        Описание *
                      </label>
                      <textarea
                        id="description"
                        rows={4}
                        value={formData.description}
                        onChange={(e) => handleInputChange('description', e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                        placeholder="Опишите суть предложения"
                        required
                      />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label htmlFor="priority" className="block text-sm font-medium text-gray-700 mb-1">
                          Приоритет *
                        </label>
                        <select
                          id="priority"
                          value={formData.priority}
                          onChange={(e) => handleInputChange('priority', e.target.value)}
                          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          required
                        >
                          <option value="">Выберите приоритет</option>
                          <option value="LOW">Низкий</option>
                          <option value="MEDIUM">Средний</option>
                          <option value="HIGH">Высокий</option>
                          <option value="CRITICAL">Критический</option>
                        </select>
                      </div>

                      <div>
                        <label htmlFor="system" className="block text-sm font-medium text-gray-700 mb-1">
                          Подсистема *
                        </label>
                        <select
                          id="system"
                          value={formData.systemId}
                          onChange={(e) => handleInputChange('systemId', e.target.value)}
                          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          required
                        >
                          <option value="">Выберите подсистему</option>
                          {systems.map((system) => (
                            <option key={system.id} value={system.id}>
                              {system.name}
                            </option>
                          ))}
                        </select>
                      </div>
                    </div>

                    <div>
                      <label htmlFor="updatedAt" className="block text-sm font-medium text-gray-700 mb-1 flex items-center">
                        <Calendar size={16} className="mr-1" />
                        Дата обновления *
                      </label>
                      <input
                        type="date"
                        id="updatedAt"
                        value={formData.updatedAt}
                        onChange={(e) => handleInputChange('updatedAt', e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        required
                      />
                    </div>
                  </div>

                  {/* Участники */}
                  <div className="space-y-4">
                    <h4 className="text-md font-medium text-gray-900 flex items-center">
                      <Users size={18} className="mr-2" />
                      Участники
                    </h4>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Исполнители
                      </label>
                      <div className="border border-gray-300 rounded-lg p-3 max-h-32 overflow-y-auto">
                        {allUsers.length > 0 ? (
                          allUsers.map((user) => (
                            <label key={user.id} className="flex items-center space-x-2 py-1">
                              <input
                                type="checkbox"
                                checked={formData.executorIds.includes(user.id)}
                                onChange={() => handleMultiSelectChange('executorIds', user.id)}
                                className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                              />
                              <span className="text-sm text-gray-700">{user.fullName}</span>
                            </label>
                          ))
                        ) : (
                          <p className="text-sm text-gray-500">Пользователи не найдены</p>
                        )}
                      </div>
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Согласующие
                      </label>
                      <div className="border border-gray-300 rounded-lg p-3 max-h-32 overflow-y-auto">
                        {allUsers.length > 0 ? (
                          allUsers.map((user) => (
                            <label key={user.id} className="flex items-center space-x-2 py-1">
                              <input
                                type="checkbox"
                                checked={formData.reviewerIds.includes(user.id)}
                                onChange={() => handleMultiSelectChange('reviewerIds', user.id)}
                                className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                              />
                              <span className="text-sm text-gray-700">{user.fullName}</span>
                            </label>
                          ))
                        ) : (
                          <p className="text-sm text-gray-500">Пользователи не найдены</p>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              )}

              {/* Actions */}
              <div className="flex justify-end space-x-3 mt-6 pt-6 border-t border-gray-200">
                <button
                  type="button"
                  onClick={handleClose}
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                  disabled={loading}
                >
                  Отмена
                </button>
                <button
                  type="submit"
                  disabled={loading || loadingData}
                  className="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {loading ? (
                    <span className="flex items-center">
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                      Создание...
                    </span>
                  ) : (
                    'Создать RFC'
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};
