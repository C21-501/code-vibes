import React, { useState, useEffect, useRef, useCallback } from 'react';
import { RFC_STATUS, URGENCY } from '../utils/rfcUtils';
import './RfcFilters.css';

const RfcFilters = React.memo(({
  filters,
  onStatusChange,
  onUrgencyChange,
  onRequesterChange,
  onTitleChange,
  onMyRfcChange,
  currentUser
}) => {
  const [titleInput, setTitleInput] = useState(filters.title || '');
  const debounceTimeout = useRef(null);
  const prevFiltersRef = useRef(filters);

  // Синхронизация поля названия с пропсами только при реальном изменении
  useEffect(() => {
    // Проверяем, изменился ли фильтр title в пропсах
    if (filters.title !== prevFiltersRef.current.title) {
      setTitleInput(filters.title || '');
      prevFiltersRef.current = filters;
    }
  }, [filters]);

  // Debounce для поиска по названию
  useEffect(() => {
    // Очищаем предыдущий таймаут
    if (debounceTimeout.current) {
      clearTimeout(debounceTimeout.current);
    }

    // Если поиск пустой, применяем сразу
    if (!titleInput || titleInput.trim().length === 0) {
      onTitleChange('');
      return;
    }

    // Если 1-2 символа, ждем
    if (titleInput.trim().length < 3) {
      return;
    }

    // Устанавливаем таймаут для поиска с 3+ символами
    debounceTimeout.current = setTimeout(() => {
      onTitleChange(titleInput.trim());
    }, 500);

    // Очистка
    return () => {
      if (debounceTimeout.current) {
        clearTimeout(debounceTimeout.current);
      }
    };
  }, [titleInput, onTitleChange]);

  const handleStatusChange = useCallback((e) => {
    onStatusChange(e.target.value);
  }, [onStatusChange]);

  const handleUrgencyChange = useCallback((e) => {
    onUrgencyChange(e.target.value);
  }, [onUrgencyChange]);

  const handleRequesterChange = useCallback((e) => {
    const value = e.target.value;
    onRequesterChange(value === '' ? '' : value);
  }, [onRequesterChange]);

  const handleTitleInputChange = useCallback((e) => {
    setTitleInput(e.target.value);
  }, []);

  const handleMyRfcChange = useCallback((e) => {
    onMyRfcChange(e.target.checked);
  }, [onMyRfcChange]);

  const handleReset = useCallback(() => {
    onStatusChange('');
    onUrgencyChange('');
    onRequesterChange('');
    onTitleChange('');
    setTitleInput('');
    if (currentUser) {
      onMyRfcChange(false);
    }
  }, [onStatusChange, onUrgencyChange, onRequesterChange, onTitleChange, onMyRfcChange, currentUser]);

  return (
    <div className="rfc-filters">
      <div className="filters-grid">
        <div className="filter-group">
          <label htmlFor="status-filter">Статус</label>
          <select
            id="status-filter"
            value={filters.status}
            onChange={handleStatusChange}
          >
            <option value="">Все статусы</option>
            <option value={RFC_STATUS.NEW}>Новый</option>
            <option value={RFC_STATUS.UNDER_REVIEW}>На рассмотрении</option>
            <option value={RFC_STATUS.APPROVED}>Одобрен</option>
            <option value={RFC_STATUS.IMPLEMENTED}>Внедрен</option>
            <option value={RFC_STATUS.REJECTED}>Отклонен</option>
          </select>
        </div>

        <div className="filter-group">
          <label htmlFor="urgency-filter">Срочность</label>
          <select
            id="urgency-filter"
            value={filters.urgency}
            onChange={handleUrgencyChange}
          >
            <option value="">Все</option>
            <option value={URGENCY.EMERGENCY}>Безотлагательное</option>
            <option value={URGENCY.URGENT}>Срочное</option>
            <option value={URGENCY.PLANNED}>Плановое</option>
          </select>
        </div>

        {/* ПОИСК ПО НАЗВАНИЮ (перенесен на третье место) */}
        <div className="filter-group">
          <label htmlFor="title-filter">Поиск по названию</label>
          <input
            type="text"
            id="title-filter"
            value={titleInput}
            onChange={handleTitleInputChange}
            placeholder="Введите минимум 3 символа..."
          />
          {titleInput && titleInput.trim().length > 0 && titleInput.trim().length < 3 && (
            <small style={{ color: '#f39c12', marginTop: '5px', display: 'block' }}>
              Введите ещё {3 - titleInput.trim().length} символ(а) для поиска
            </small>
          )}
        </div>

        {/* ID СОЗДАТЕЛЯ (перенесен на четвертое место) */}
        <div className="filter-group">
          <label htmlFor="requester-filter">ID создателя</label>
          <input
            type="number"
            id="requester-filter"
            value={filters.requesterId || ''}
            onChange={handleRequesterChange}
            placeholder="Введите ID"
            min="0"
          />
        </div>

        <div className="filter-group">
          <label>&nbsp;</label>
          <div className="my-rfc-filter">
            <input
              type="checkbox"
              id="my-rfc-filter"
              checked={filters.requesterId === currentUser?.id}
              onChange={handleMyRfcChange}
              className="filter-checkbox"
            />
            <label htmlFor="my-rfc-filter" className="checkbox-label">
              Только мои RFC
            </label>
          </div>
        </div>

        <div className="filter-group">
          <button
            className="btn-secondary"
            onClick={handleReset}
          >
            Сбросить
          </button>
        </div>
      </div>
    </div>
  );
});

RfcFilters.displayName = 'RfcFilters';

export default RfcFilters;