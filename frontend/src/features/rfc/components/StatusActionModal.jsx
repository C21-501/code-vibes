import React, { useState, useEffect } from 'react';
import {
  RFC_STATUS,
  getStatusLabel,
  getStatusClass
} from '../utils/rfcUtils';
import './StatusActionModal.css';

const StatusActionModal = ({
  isOpen,
  onClose,
  rfc,
  currentUser,
  onStatusUpdate
}) => {
  const [selectedAction, setSelectedAction] = useState(null);
  const [comment, setComment] = useState('');
  const [availableActions, setAvailableActions] = useState([]);

  useEffect(() => {
    if (rfc && currentUser) {
      // Здесь должна быть логика определения доступных действий
      // Временно оставляем пустой массив
      setAvailableActions([]);
      setSelectedAction(null);
      setComment('');
    }
  }, [rfc, currentUser]);

  if (!isOpen || !rfc) return null;

  const handleActionSelect = (action) => {
    setSelectedAction(action);
  };

  const handleSubmit = () => {
    if (!selectedAction) return;

    onStatusUpdate(rfc.id, selectedAction.value, comment);
    onClose();
  };

  const handleClose = () => {
    setSelectedAction(null);
    setComment('');
    onClose();
  };

  return (
    <div className={`modal ${isOpen ? 'active' : ''}`}>
      <div className="modal-content status-action-modal">
        <div className="modal-header">
          <h2>Изменение статуса RFC</h2>
          <button className="close" onClick={handleClose}>×</button>
        </div>

        <div className="modal-body">
          {/* Current Status Info */}
          <div className="modal-section">
            <div className="modal-section-title">Текущий статус</div>
            <div className="current-status-info">
              <div className="status-display">
                <span className={`status-badge ${getStatusClass(rfc.status)}`}>
                  {getStatusLabel(rfc.status)}
                </span>
              </div>
            </div>
          </div>

          {/* Available Actions */}
          <div className="modal-section">
            <div className="modal-section-title">Доступные действия</div>
            <div className="actions-list">
              {availableActions.length === 0 ? (
                <div className="no-actions">
                  Нет доступных действий для текущего статуса
                </div>
              ) : (
                availableActions.map((action) => (
                  <div
                    key={action.value}
                    className={`action-item ${selectedAction?.value === action.value ? 'selected' : ''}`}
                    onClick={() => handleActionSelect(action)}
                  >
                    <div className="action-radio">
                      <div className="radio-circle">
                        {selectedAction?.value === action.value && <div className="radio-dot" />}
                      </div>
                    </div>
                    <div className="action-content">
                      <div className="action-label">{action.label}</div>
                      <div className="action-description">{action.description}</div>
                      <div className="action-next-status">
                        Новый статус: <span className={`status-badge ${getStatusClass(action.nextStatus)}`}>
                          {getStatusLabel(action.nextStatus)}
                        </span>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>

          {/* Action Details */}
          {selectedAction && (
            <div className="modal-section">
              <div className="modal-section-title">Детали действия</div>

              {/* Selected Action Info */}
              <div className="selected-action-info">
                <div className="action-detail-row">
                  <div className="action-detail-label">Выбранное действие:</div>
                  <div className="action-detail-value">{selectedAction.label}</div>
                </div>
                <div className="action-detail-row">
                  <div className="action-detail-label">Новый статус:</div>
                  <div className="action-detail-value">
                    <span className={`status-badge ${getStatusClass(selectedAction.nextStatus)}`}>
                      {getStatusLabel(selectedAction.nextStatus)}
                    </span>
                  </div>
                </div>
                <div className="action-detail-row">
                  <div className="action-detail-label">Описание:</div>
                  <div className="action-detail-value">{selectedAction.description}</div>
                </div>
              </div>

              {/* Comment Input */}
              <div className="form-group">
                <label htmlFor="statusComment">
                  Комментарий
                  <span className="required">*</span>
                </label>
                <textarea
                  id="statusComment"
                  value={comment}
                  onChange={(e) => setComment(e.target.value)}
                  placeholder="Опишите причину изменения статуса..."
                  rows="4"
                  maxLength="1000"
                  className={!comment ? 'error' : ''}
                />
                <div className="field-counter">
                  <span>{comment.length}</span> / 1000
                </div>
                {!comment && (
                  <div className="error-message">Комментарий обязателен для заполнения</div>
                )}
              </div>
            </div>
          )}
        </div>

        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={handleClose}>
            Отмена
          </button>
          <button
            className="btn btn-primary"
            onClick={handleSubmit}
            disabled={!selectedAction || !comment.trim()}
          >
            Подтвердить изменение
          </button>
        </div>
      </div>
    </div>
  );
};

export default StatusActionModal;