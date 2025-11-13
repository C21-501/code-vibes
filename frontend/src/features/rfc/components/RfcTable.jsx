import React from 'react';
import {
  getStatusLabel,
  getUrgencyLabel,
  getStatusClass,
  getUrgencyClass,
  formatDate,
  canPerformAction,
  RFC_ACTION
} from '../utils/rfcUtils';
import './RfcTable.css';

const RfcTable = ({
  rfcs,
  currentUser,
  onViewRfc,
  onEditRfc,
  onDeleteRfc,
  onStatusAction
}) => {
  const formatUserName = (user) => {
    if (!user) return '';
    return `${user.lastName} ${user.firstName?.charAt(0)}.`;
  };

  const getSystemsCount = (rfc) => {
    return rfc.affectedSystems?.length || 0;
  };

  const getSubsystemsCount = (rfc) => {
    return rfc.affectedSystems?.reduce((total, system) =>
      total + (system.affectedSubsystems?.length || 0), 0) || 0;
  };

  const getAttachmentsCount = (rfc) => {
    return rfc.attachments?.length || 0;
  };

  return (
    <div className="rfc-table-container">
      <table className="rfc-table">
        <thead>
          <tr>
            <th style={{ width: '80px' }}>ID</th>
            <th>Название</th>
            <th style={{ width: '140px' }}>Статус</th>
            <th style={{ width: '130px' }}>Срочность</th>
            <th style={{ width: '150px' }}>Дата исполнения</th>
            <th style={{ width: '120px' }}>Создатель</th>
            <th style={{ width: '150px' }}>Дата создания</th>
            <th style={{ width: '150px' }}>Действия</th>
          </tr>
        </thead>
        <tbody>
          {rfcs.length === 0 ? (
            <tr>
              <td colSpan="8" className="empty-state">
                <div className="empty-state-content">
                  <div className="empty-state-icon">📋</div>
                  <p>RFC не найдены</p>
                  <small>Попробуйте изменить параметры фильтрации</small>
                </div>
              </td>
            </tr>
          ) : (
            rfcs.map((rfc) => (
              <tr key={rfc.id} className="rfc-row">
                <td>
                  <span className="rfc-id">RFC-{String(rfc.id).padStart(3, '0')}</span>
                </td>
                <td>
                  <div className="rfc-title" onClick={() => onViewRfc(rfc.id)}>
                    <div className="title-text">{rfc.title}</div>
                    <div className="rfc-meta">
                      <span title="Системы">🖥️ {getSystemsCount(rfc)}</span>
                      <span title="Подсистемы">📦 {getSubsystemsCount(rfc)}</span>
                      <span title="Вложения">📎 {getAttachmentsCount(rfc)}</span>
                    </div>
                  </div>
                </td>
                <td>
                  <span className={`status-badge ${getStatusClass(rfc.status)}`}>
                    {getStatusLabel(rfc.status)}
                  </span>
                </td>
                <td>
                  <span className={`urgency-badge ${getUrgencyClass(rfc.urgency)}`}>
                    {getUrgencyLabel(rfc.urgency)}
                  </span>
                </td>
                <td>{formatDate(rfc.implementationDate)}</td>
                <td>{formatUserName(rfc.requester)}</td>
                <td>{formatDate(rfc.createDatetime)}</td>
                <td>
                  <div className="action-buttons">
                    <button
                      className="btn-view"
                      onClick={() => onViewRfc(rfc.id)}
                    >
                      👁️ Просмотр
                    </button>

                    {/* Кнопка редактирования отображается только если есть права */}
                    {canPerformAction(currentUser, rfc, RFC_ACTION.UPDATE) && (
                      <button
                        className="btn-edit"
                        onClick={() => onEditRfc(rfc.id)}
                      >
                        ✏️ Редактировать
                      </button>
                    )}

                    {/* Кнопка удаления отображается только если есть права */}
                    {rfc.actions?.includes('DELETE') && (
                      <button
                        className="btn-delete"
                        onClick={() => onDeleteRfc(rfc.id)}
                        title="Удалить RFC"
                      >
                        🗑️ Удалить
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};

export default RfcTable;