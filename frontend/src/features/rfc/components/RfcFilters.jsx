import React, { useState } from 'react';
import { RFC_STATUS, URGENCY } from '../utils/rfcUtils';
import './RfcFilters.css';

const RfcFilters = ({
  filters,
  onStatusChange,
  onUrgencyChange,
  onRequesterChange,
  onMyRfcChange,
  onApplyFilters,
  currentUser
}) => {
  const [localFilters, setLocalFilters] = useState(filters);

  const handleStatusChange = (e) => {
    const newFilters = { ...localFilters, status: e.target.value };
    setLocalFilters(newFilters);
    onStatusChange(e.target.value);
  };

  const handleUrgencyChange = (e) => {
    const newFilters = { ...localFilters, urgency: e.target.value };
    setLocalFilters(newFilters);
    onUrgencyChange(e.target.value);
  };

  const handleRequesterChange = (e) => {
    const newFilters = { ...localFilters, requesterId: e.target.value };
    setLocalFilters(newFilters);
    onRequesterChange(e.target.value);
  };

  const handleMyRfcChange = (e) => {
    onMyRfcChange(e.target.checked);
  };

  const handleReset = () => {
    const resetFilters = { status: '', urgency: '', requesterId: '' };
    setLocalFilters(resetFilters);
    onApplyFilters(resetFilters);
  };

  return (
    <div className="rfc-filters">
      <div className="filters-grid">
        <div className="filter-group">
          <label htmlFor="status-filter">Статус</label>
          <select
            id="status-filter"
            value={localFilters.status}
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
            value={localFilters.urgency}
            onChange={handleUrgencyChange}
          >
            <option value="">Все</option>
            <option value={URGENCY.EMERGENCY}>Безотлагательное</option>
            <option value={URGENCY.URGENT}>Срочное</option>
            <option value={URGENCY.PLANNED}>Плановое</option>
          </select>
        </div>

        <div className="filter-group">
          <label htmlFor="requester-filter">ID создателя</label>
          <input
            type="number"
            id="requester-filter"
            value={localFilters.requesterId}
            onChange={handleRequesterChange}
            placeholder="Введите ID"
          />
        </div>

        <div className="filter-group">
          <label>&nbsp;</label>
          <div className="my-rfc-filter">
            <input
              type="checkbox"
              id="my-rfc-filter"
              checked={localFilters.requesterId === currentUser?.id}
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
};

export default RfcFilters;