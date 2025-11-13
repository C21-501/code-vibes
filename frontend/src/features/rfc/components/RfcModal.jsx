import React, { useState } from 'react';
import { useAuth } from '../../auth/context/AuthContext';
import {
  canApproveRfc,
  canUnapproveRfc,
  canConfirmSubsystems,
  canUpdateExecution,
  getConfirmableSubsystems,
  getExecutableSubsystems,
  getStatusLabel,
  getStatusClass
} from '../utils/rfcUtils';
import './RfcModal.css';

const RfcModal = ({
  isOpen,
  onClose,
  children,
  rfc,
  onApprove,
  onUnapprove,
  onConfirm,
  onUpdateExecution
}) => {
  const { user } = useAuth();
  const [comment, setComment] = useState('');

  if (!isOpen) return null;

  // Отладочная информация
  console.log('=== RfcModal Debug Info ===');
  console.log('User:', user);
  console.log('RFC:', rfc);
  console.log('RFC Status:', rfc?.status);
  console.log('RFC Approvals:', rfc?.approvals);
  console.log('RFC Affected Systems:', rfc?.affectedSystems);
  console.log('RFC actions:', rfc?.actions);

  const canApprove = canApproveRfc(user, rfc);
  const canUnapprove = canUnapproveRfc(user, rfc);
  const canConfirm = canConfirmSubsystems(user, rfc);
  const canUpdateExec = canUpdateExecution(user, rfc);
  const confirmableSubsystems = getConfirmableSubsystems(user, rfc);
  const executableSubsystems = getExecutableSubsystems(user, rfc);

  console.log('canApprove:', canApprove);
  console.log('canUnapprove:', canUnapprove);
  console.log('canConfirm:', canConfirm);
  console.log('canUpdateExec:', canUpdateExec);
  console.log('confirmableSubsystems:', confirmableSubsystems);
  console.log('executableSubsystems:', executableSubsystems);
  console.log('======================');

  const handleApprove = () => {
    if (onApprove && rfc) {
      onApprove(rfc.id, comment);
      setComment('');
    }
  };

  const handleUnapprove = () => {
    if (onUnapprove && rfc) {
      onUnapprove(rfc.id, comment);
      setComment('');
    }
  };

  const handleConfirm = (subsystemId, status) => {
    console.log('Handling confirm for subsystemId:', subsystemId, 'status:', status);
    if (onConfirm && rfc) {
      onConfirm(rfc.id, subsystemId, status, comment);
      setComment('');
    }
  };

  const handleUpdateExecution = (subsystemId, status) => {
    console.log('Handling execution update for subsystemId:', subsystemId, 'status:', status);
    if (onUpdateExecution && rfc) {
      onUpdateExecution(rfc.id, subsystemId, status, comment);
      setComment('');
    }
  };

  return (
    <div className={`modal ${isOpen ? 'active' : ''}`}>
      <div className="modal-content rfc-detail-modal">
        <div className="modal-header">
          <h2>Детали RFC #{rfc?.id}</h2>
          <button className="close" onClick={onClose}>
            ×
          </button>
        </div>
        <div className="modal-body">
          {children}

          {/* Блок действий */}
          {(canApprove || canUnapprove || canConfirm || canUpdateExec) && (
            <div className="rfc-actions-panel">
              <h3>Доступные действия</h3>

              {/* Отладочная информация в UI */}
              <div className="debug-info" style={{fontSize: '12px', color: '#666', marginBottom: '15px', padding: '10px', backgroundColor: '#f5f5f5', borderRadius: '4px'}}>
                <strong>Отладка:</strong> canApprove: {canApprove ? 'true' : 'false'},
                canUnapprove: {canUnapprove ? 'true' : 'false'},
                canConfirm: {canConfirm ? 'true' : 'false'},
                canUpdateExec: {canUpdateExec ? 'true' : 'false'},
                confirmableSubsystems: {confirmableSubsystems.length},
                executableSubsystems: {executableSubsystems.length}
              </div>

              {/* Комментарий для всех действий */}
              <div className="action-comment">
                <label htmlFor="actionComment">Комментарий к действию:</label>
                <textarea
                  id="actionComment"
                  value={comment}
                  onChange={(e) => setComment(e.target.value)}
                  placeholder="Введите комментарий к действию..."
                  rows="3"
                  maxLength="1000"
                />
                <div className="field-counter">
                  <span>{comment.length}</span> / 1000
                </div>
              </div>

              {/* Кнопки согласования - разделяем логику для approve и unapprove */}
              {(canApprove || canUnapprove) && (
                <div className="action-group">
                  <h4>Согласование RFC</h4>
                  <div className="action-buttons-group">
                    {canApprove && (
                      <button
                        className="btn btn-primary"
                        onClick={handleApprove}
                        disabled={!comment.trim()}
                      >
                        Согласовать RFC
                      </button>
                    )}
                    {canUnapprove && (
                      <button
                        className="btn btn-warning"
                        onClick={handleUnapprove}
                        disabled={!comment.trim()}
                      >
                        Отменить согласование
                      </button>
                    )}
                  </div>
                </div>
              )}

              {/* Кнопки подтверждения подсистем */}
              {canConfirm && confirmableSubsystems.length > 0 && (
                <div className="action-group">
                  <h4>Подтверждение подсистем</h4>
                  {confirmableSubsystems.map(subsystem => (
                    <div key={subsystem.affectedSubsystemId} className="subsystem-action">
                      <div className="subsystem-info">
                        <span className="system-name">{subsystem.systemName}</span>
                        <span className="subsystem-name">{subsystem.subsystemName}</span>
                        <span className={`status-badge ${getStatusClass(subsystem.confirmationStatus)}`}>
                          {getStatusLabel(subsystem.confirmationStatus)}
                        </span>
                      </div>
                      <div className="action-buttons">
                        <button
                          className="btn btn-success"
                          onClick={() => handleConfirm(subsystem.subsystemId, 'CONFIRMED')}
                          disabled={!comment.trim()}
                        >
                          Подтвердить
                        </button>
                        <button
                          className="btn btn-danger"
                          onClick={() => handleConfirm(subsystem.subsystemId, 'REJECTED')}
                          disabled={!comment.trim()}
                        >
                          Отклонить
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}

              {/* Кнопки обновления статуса выполнения */}
              {canUpdateExec && executableSubsystems.length > 0 && (
                <div className="action-group">
                  <h4>Статус выполнения</h4>
                  {executableSubsystems.map(subsystem => (
                    <div key={subsystem.affectedSubsystemId} className="subsystem-action">
                      <div className="subsystem-info">
                        <span className="system-name">{subsystem.systemName}</span>
                        <span className="subsystem-name">{subsystem.subsystemName}</span>
                        <span className={`status-badge ${getStatusClass(subsystem.executionStatus)}`}>
                          {getStatusLabel(subsystem.executionStatus)}
                        </span>
                      </div>
                      <div className="action-buttons">
                        {subsystem.executionStatus === 'PENDING' && (
                          <button
                            className="btn btn-info"
                            onClick={() => handleUpdateExecution(subsystem.subsystemId, 'IN_PROGRESS')}
                            disabled={!comment.trim()}
                          >
                            Начать выполнение
                          </button>
                        )}
                        {subsystem.executionStatus === 'IN_PROGRESS' && (
                          <button
                            className="btn btn-success"
                            onClick={() => handleUpdateExecution(subsystem.subsystemId, 'DONE')}
                            disabled={!comment.trim()}
                          >
                            Завершить выполнение
                          </button>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default RfcModal;