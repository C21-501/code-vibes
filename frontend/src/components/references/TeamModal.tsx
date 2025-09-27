import { useState, useEffect } from 'react';
import { useMutation } from '@tanstack/react-query';
import { referenceApi } from '@/api/referenceApi';
import type { Team, UserWithRole, CreateTeamRequest, UpdateTeamRequest } from '@/types/api';

interface TeamModalProps {
  team: Team | null;
  users: UserWithRole[];
  onClose: () => void;
  onSuccess: () => void;
}

export function TeamModal({ team, users, onClose, onSuccess }: TeamModalProps) {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    leaderId: ''
  });

  const isEditing = !!team;

  useEffect(() => {
    if (team) {
      setFormData({
        name: team.name,
        description: team.description || '',
        leaderId: team.leader?.id || ''
      });
    }
  }, [team]);

  // Create mutation
  const createMutation = useMutation({
    mutationFn: (data: CreateTeamRequest) => referenceApi.teams.create(data),
    onSuccess: () => {
      onSuccess();
    }
  });

  // Update mutation
  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateTeamRequest }) =>
      referenceApi.teams.update(id, data),
    onSuccess: () => {
      onSuccess();
    }
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.name.trim() || !formData.leaderId) {
      return;
    }

    if (isEditing && team) {
      const updateData: UpdateTeamRequest = {
        id: team.id,
        name: formData.name.trim(),
        description: formData.description.trim() || undefined,
        leaderId: formData.leaderId
      };
      updateMutation.mutate({ id: team.id, data: updateData });
    } else {
      const createData: CreateTeamRequest = {
        name: formData.name.trim(),
        description: formData.description.trim() || undefined,
        leaderId: formData.leaderId
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
              {isEditing ? 'Редактировать команду' : 'Добавить команду'}
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
                Произошла ошибка при сохранении команды
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
                  placeholder="Название команды"
                />
              </div>

              {/* Leader */}
              <div>
                <label htmlFor="leaderId" className="block text-sm font-medium text-gray-700 mb-1">
                  Руководитель *
                </label>
                <select
                  id="leaderId"
                  name="leaderId"
                  value={formData.leaderId}
                  onChange={handleChange}
                  required
                  disabled={isLoading}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="">Выберите руководителя</option>
                  {users.map((user) => (
                    <option key={user.id} value={user.id}>
                      {user.fullName} ({user.username})
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
                  placeholder="Описание команды"
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
                disabled={isLoading || !formData.name.trim() || !formData.leaderId}
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
