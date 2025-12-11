import React, { useState, useEffect } from 'react';
import { useAuth } from '../../auth/context/AuthContext';
import { attachmentApi } from '../../../shared/api/attachmentApi';
import {
  canApproveRfc,
  canUnapproveRfc,
  canConfirmSubsystems,
  canUpdateExecution,
  getConfirmableSubsystems,
  getExecutableSubsystems,
  getStatusLabel,
  getStatusClass,
  getUrgencyLabel,
  getUrgencyClass,
  formatDate
} from '../utils/rfcUtils';
import Toast from '../../../shared/components/Toast';
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
  const [downloading, setDownloading] = useState(null);
  const [toast, setToast] = useState({ show: false, type: '', title: '', message: '' });

  // Сбрасываем комментарий при открытии модального окна
  useEffect(() => {
    if (isOpen) {
      setComment('');
    }
  }, [isOpen]);

  // Функция для скачивания файла
  const handleDownloadAttachment = async (attachmentId, filename) => {
    setDownloading(attachmentId);
    try {
      await attachmentApi.downloadAttachment(attachmentId, filename);
    } catch (error) {
      console.error('Download error:', error);
      showToast('error', 'Ошибка', 'Не удалось скачать файл');
    } finally {
      setDownloading(null);
    }
  };

  // Функция для форматирования размера файла
  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const showToast = (type, title, message) => {
    setToast({ show: true, type, title, message });
    setTimeout(() => setToast({ ...toast, show: false }), 5000);
  };

  if (!isOpen) return null;

  // Отладочная информация
  console.log('=== RfcModal Debug Info ===');
  console.log('User:', user);
  console.log('RFC:', rfc);
  console.log('RFC Status:', rfc?.status);
  console.log('RFC Approvals:', rfc?.approvals);
  console.log('RFC Affected Systems:', rfc?.affectedSystems);
  console.log('RFC actions:', rfc?.actions);
  console.log('RFC Attachments:', rfc?.attachments);

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

          {/* Секция вложений */}
          {rfc?.attachments && rfc.attachments.length > 0 && (
            <div className="detail-section">
              <h3>Вложения</h3>
              <div className="attachments-list">
                {rfc.attachments.map(attachment => (
                  <div key={attachment.id} className="attachment-item">
                    <div className="attachment-info">
                      <span className="attachment-icon">📎</span>
                      <div className="attachment-details">
                        <span className="attachment-name">{attachment.originalFilename}</span>
                        <span className="attachment-meta">
                          {formatFileSize(attachment.fileSize)} •
                          {new Date(attachment.createDatetime).toLocaleDateString('ru-RU')}
                        </span>
                      </div>
                    </div>
                    <button
                      onClick={() => handleDownloadAttachment(attachment.id, attachment.originalFilename)}
                      disabled={downloading === attachment.id}
                      className="btn-download"
                    >
                      {downloading === attachment.id ? '⏳' : '⬇️'} Скачать
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}

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
                <label htmlFor="actionComment">
                  Комментарий к действию (необязательно):
                  <span style={{color: '#666', fontSize: '12px', marginLeft: '5px'}}>
                    - если не указан, будет использован комментарий по умолчанию
                  </span>
                </label>
                <textarea
                  id="actionComment"
                  value={comment}
                  onChange={(e) => setComment(e.target.value)}
                  placeholder="Введите комментарий к действию (необязательно)..."
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
                      >
                        Согласовать RFC
                      </button>
                    )}
                    {canUnapprove && (
                      <button
                        className="btn btn-warning"
                        onClick={handleUnapprove}
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
                        >
                          Подтвердить
                        </button>
                        <button
                          className="btn btn-danger"
                          onClick={() => handleConfirm(subsystem.subsystemId, 'REJECTED')}
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
                          >
                            Начать выполнение
                          </button>
                        )}
                        {subsystem.executionStatus === 'IN_PROGRESS' && (
                          <button
                            className="btn btn-success"
                            onClick={() => handleUpdateExecution(subsystem.subsystemId, 'DONE')}
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

      <Toast
        show={toast.show}
        type={toast.type}
        title={toast.title}
        message={toast.message}
        onClose={() => setToast({ ...toast, show: false })}
      />
    </div>
  );
};

export default RfcModal;