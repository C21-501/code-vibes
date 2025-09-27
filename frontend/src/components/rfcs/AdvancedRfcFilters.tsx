import React, { useState, useEffect } from 'react';
import { Search, Filter, X, ChevronDown } from 'lucide-react';
import type { RfcFilters, Team, System } from '../../types/api';
import { RfcStatus, Priority } from '../../types/api';
import { rfcApi } from '../../api/rfcApi';

interface AdvancedRfcFiltersProps {
  filters: RfcFilters;
  onFiltersChange: (filters: RfcFilters) => void;
  onClearFilters: () => void;
}

interface ExtendedRfcFilters extends RfcFilters {
  statuses?: RfcStatus[];
  priorities?: Priority[];
  teamIds?: string[];
  search?: string;
}

const statusOptions: Array<{ value: RfcStatus; label: string }> = [
  { value: 'REQUESTED_NEW', label: 'Новый запрос' },
  { value: 'WAITING', label: 'Ожидание' },
  { value: 'APPROVED', label: 'Одобрен' },
  { value: 'DECLINED', label: 'Отклонен' },
  { value: 'DONE', label: 'Выполнен' },
  { value: 'CANCELLED', label: 'Отменен' }
];

const priorityOptions: Array<{ value: Priority; label: string }> = [
  { value: 'LOW', label: 'Низкий' },
  { value: 'MEDIUM', label: 'Средний' },
  { value: 'HIGH', label: 'Высокий' },
  { value: 'CRITICAL', label: 'Критический' }
];

export const AdvancedRfcFilters: React.FC<AdvancedRfcFiltersProps> = ({
  filters,
  onFiltersChange,
  onClearFilters
}) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const [teams, setTeams] = useState<Team[]>([]);
  const [systems, setSystems] = useState<System[]>([]);
  const [loading, setLoading] = useState(false);
  
  // Local state for advanced filters
  const [localFilters, setLocalFilters] = useState<ExtendedRfcFilters>({
    ...filters,
    statuses: filters.status ? [filters.status] : [],
    priorities: filters.priority ? [filters.priority] : [],
    teamIds: [],
    search: ''
  });

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      try {
        const [teamsData, systemsData] = await Promise.all([
          rfcApi.getTeams(),
          rfcApi.getSystems()
        ]);
        setTeams(teamsData);
        setSystems(systemsData);
      } catch (error) {
        console.error('Error loading filter data:', error);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  const hasActiveFilters = Object.values(localFilters).some(value => {
    if (Array.isArray(value)) {
      return value.length > 0;
    }
    return value !== undefined && value !== null && value !== '';
  });

  const handleFilterChange = (key: keyof ExtendedRfcFilters, value: any) => {
    const newFilters = {
      ...localFilters,
      [key]: value
    };
    setLocalFilters(newFilters);
    
    // Convert advanced filters back to simple filters for API
    const apiFilters: RfcFilters = {
      status: newFilters.statuses && newFilters.statuses.length === 1 ? newFilters.statuses[0] : undefined,
      priority: newFilters.priorities && newFilters.priorities.length === 1 ? newFilters.priorities[0] : undefined,
      systemId: newFilters.systemId,
      dateFrom: newFilters.dateFrom,
      dateTo: newFilters.dateTo
    };
    
    onFiltersChange(apiFilters);
  };

  const handleMultiSelectChange = (key: 'statuses' | 'priorities' | 'teamIds', value: string) => {
    const currentValues = localFilters[key] || [];
    const newValues = currentValues.includes(value as any)
      ? currentValues.filter(v => v !== value)
      : [...currentValues, value as any];
    
    handleFilterChange(key, newValues);
  };

  const handleClearFilters = () => {
    setLocalFilters({
      statuses: [],
      priorities: [],
      teamIds: [],
      search: ''
    });
    onClearFilters();
  };

  const MultiSelectDropdown: React.FC<{
    label: string;
    options: Array<{ value: string; label: string }>;
    selectedValues: string[];
    onChange: (value: string) => void;
  }> = ({ label, options, selectedValues, onChange }) => {
    const [isOpen, setIsOpen] = useState(false);

    return (
      <div className="relative">
        <label className="block text-sm font-medium text-gray-700 mb-1">
          {label}
        </label>
        <button
          type="button"
          onClick={() => setIsOpen(!isOpen)}
          className="w-full px-3 py-2 text-left border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm bg-white"
        >
          <div className="flex items-center justify-between">
            <span className="truncate">
              {selectedValues.length === 0
                ? `Все ${label.toLowerCase()}`
                : selectedValues.length === 1
                ? options.find(o => o.value === selectedValues[0])?.label
                : `Выбрано: ${selectedValues.length}`
              }
            </span>
            <ChevronDown size={16} className={`transition-transform ${isOpen ? 'rotate-180' : ''}`} />
          </div>
        </button>
        
        {isOpen && (
          <>
            <div className="fixed inset-0 z-10" onClick={() => setIsOpen(false)} />
            <div className="absolute z-20 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-lg max-h-60 overflow-auto">
              {options.map((option) => (
                <label
                  key={option.value}
                  className="flex items-center px-3 py-2 hover:bg-gray-50 cursor-pointer"
                >
                  <input
                    type="checkbox"
                    checked={selectedValues.includes(option.value)}
                    onChange={() => onChange(option.value)}
                    className="mr-2 h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                  />
                  <span className="text-sm text-gray-900">{option.label}</span>
                </label>
              ))}
            </div>
          </>
        )}
      </div>
    );
  };

  return (
    <div className="bg-white rounded-lg shadow p-4 mb-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <div className="flex items-center space-x-2">
            <Filter size={20} className="text-gray-500" />
            <span className="font-medium text-gray-900">Фильтры</span>
            {hasActiveFilters && (
              <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                Активны
              </span>
            )}
          </div>

          <button
            onClick={() => setIsExpanded(!isExpanded)}
            className="text-sm text-blue-600 hover:text-blue-700 font-medium"
          >
            {isExpanded ? 'Скрыть' : 'Показать'}
          </button>
        </div>

        {hasActiveFilters && (
          <button
            onClick={handleClearFilters}
            className="flex items-center space-x-1 text-sm text-gray-500 hover:text-gray-700"
          >
            <X size={16} />
            <span>Очистить</span>
          </button>
        )}
      </div>

      {isExpanded && (
        <div className="mt-4 space-y-4">
          {/* Поиск */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Поиск по названию
            </label>
            <div className="relative">
              <Search size={16} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
              <input
                type="text"
                value={localFilters.search || ''}
                onChange={(e) => handleFilterChange('search', e.target.value)}
                placeholder="Введите название RFC..."
                className="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {/* Статусы (множественный выбор) */}
            <MultiSelectDropdown
              label="Статусы"
              options={statusOptions}
              selectedValues={localFilters.statuses || []}
              onChange={(value) => handleMultiSelectChange('statuses', value)}
            />

            {/* Приоритеты (множественный выбор) */}
            <MultiSelectDropdown
              label="Приоритеты"
              options={priorityOptions}
              selectedValues={localFilters.priorities || []}
              onChange={(value) => handleMultiSelectChange('priorities', value)}
            />

            {/* Команды (множественный выбор) */}
            {!loading && teams.length > 0 && (
              <MultiSelectDropdown
                label="Команды"
                options={teams.map(team => ({ value: team.id, label: team.name }))}
                selectedValues={localFilters.teamIds || []}
                onChange={(value) => handleMultiSelectChange('teamIds', value)}
              />
            )}

            {/* Система */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Система
              </label>
              <select
                value={localFilters.systemId || ''}
                onChange={(e) => handleFilterChange('systemId', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
              >
                <option value="">Все системы</option>
                {systems.map((system) => (
                  <option key={system.id} value={system.id}>
                    {system.name}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Дата от */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Дата от
              </label>
              <input
                type="date"
                value={localFilters.dateFrom || ''}
                onChange={(e) => handleFilterChange('dateFrom', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
              />
            </div>

            {/* Дата до */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Дата до
              </label>
              <input
                type="date"
                value={localFilters.dateTo || ''}
                onChange={(e) => handleFilterChange('dateTo', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
              />
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
