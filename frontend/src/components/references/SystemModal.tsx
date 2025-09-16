import { useState, useEffect } from 'react';
import { useMutation } from '@tanstack/react-query';
import { referenceApi } from '@/api/referenceApi';
import { System, Team, CreateSystemRequest, UpdateSystemRequest } from '@/types/api';

interface SystemModalProps {
  system: System | null;
  teams: Team[];
  onClose: () => void;
  onSuccess: () => void;
}

export function SystemModal({ system, teams, onClose, onSuccess }: SystemModalProps) {
  const [formData, setFormData] = useState({
    name: '',
    type: '',
    description: '',
    responsibleTeamId: ''
  });

  const isEditing = !!system;

  useEffect(() => {
    if (system) {
      setFormData({
        name: system.name,
        type: system.type || '',
        description: system.description || '',
        responsibleTeamId: system.responsibleTeam?.id || ''
      });
    }
  }, [system]);

  // Create mutation
  const createMutation = useMutation({
    mutationFn: (data: CreateSystemRequest) => referenceApi.systems.create(data),
    onSuccess: () => {
      onSuccess();
    }
  });

  // Update mutation
  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateSystemRequest }) =>
      referenceApi.systems.update(id, data),
    onSuccess: () => {
      onSuccess();
    }
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.name.trim() || !formData.responsibleTeamId) {
      return;
    }

    if (isEditing && system) {
      const updateData: UpdateSystemRequest = {
        id: system.id,
        name: formData.name.trim(),
        type: formData.type.trim() || undefined,
        description: formData.description.trim() || undefined,
        responsibleTeamId: formData.responsibleTeamId
      };
      updateMutation.mutate({ id: system.id, data: updateData });
    } else {
      const createData: CreateSystemRequest = {
        name: formData.name.trim(),
        type: formData.type.trim() || undefined,
        description: formData.description.trim() || undefined,
        responsibleTeamId: formData.responsibleTeamId
      };
      createMutation.mutate(createData);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const isLoading = createMutation.isPending || updateMutation.isPending;
  const error = createMutation.error || updateMutation.error;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-lg max-w-md w-full max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          <div className="flex justify-between items-center mb-4">
            <h3 className="text-lg font-semibold text-gray-900">
              {isEditing ? 'Редактировать систему' : 'Добавить систему'}
            </h3>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600"
              disabled={isLoading}
            >
              ✕
            </button>
          </div>

          {error && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-md">
              <p className="text-sm text-red-600">
                Произошла ошибка при сохранении системы
              </p>
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="space-y-4">
              {/* Name */}
              <div>
                <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-1">
                  Название *
                </label>
                <input
                  type="text"
                  id="name"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  required
                  disabled={isLoading}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="Название системы"
                />
              </div>

              {/* Type */}
              <div>
                <label htmlFor="type" className="block text-sm font-medium text-gray-700 mb-1">
                  Тип
                </label>
                <input
                  type="text"
                  id="type"
                  name="type"
                  value={formData.type}
                  onChange={handleChange}
                  disabled={isLoading}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="Тип системы"
                />
              </div>

              {/* Responsible Team */}
              <div>
                <label htmlFor="responsibleTeamId" className="block text-sm font-medium text-gray-700 mb-1">
                  Ответственная команда *
                </label>
                <select
                  id="responsibleTeamId"
                  name="responsibleTeamId"
                  value={formData.responsibleTeamId}
                  onChange={handleChange}
                  required
                  disabled={isLoading}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="">Выберите команду</option>
                  {teams.map((team) => (
                    <option key={team.id} value={team.id}>
                      {team.name}
                    </option>
                  ))}
                </select>
              </div>

              {/* Description */}
              <div>
                <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-1">
                  Описание
                </label>
                <textarea
                  id="description"
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  rows={3}
                  disabled={isLoading}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                  placeholder="Описание системы"
                />
              </div>
            </div>

            {/* Actions */}
            <div className="flex justify-end space-x-3 mt-6">
              <button
                type="button"
                onClick={onClose}
                disabled={isLoading}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-md transition-colors disabled:opacity-50"
              >
                Отмена
              </button>
              <button
                type="submit"
                disabled={isLoading || !formData.name.trim() || !formData.responsibleTeamId}
                className="px-4 py-2 text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 rounded-md transition-colors disabled:opacity-50"
              >
                {isLoading ? 'Сохранение...' : (isEditing ? 'Сохранить' : 'Создать')}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
