/**
 * RfcAuditTable Component
 * Displays RFC history events in a table format
 */
import './RfcAuditTable.css';

export default function RfcAuditTable({ events }) {
  const formatDateTime = (isoString) => {
    if (!isoString) return '-';
    try {
      const date = new Date(isoString);
      return date.toLocaleString('ru-RU', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    } catch (e) {
      return isoString;
    }
  };

  const getEventTypeLabel = (eventType) => {
    const labels = {
      'RFC_FIELDS_CHANGED': 'Изменение полей',
      'RFC_ATTACHMENTS_CHANGED': 'Изменение файлов',
      'RFC_SUBSYSTEMS_CHANGED': 'Изменение подсистем',
      'SUBSYSTEM_STATUS_CHANGED': 'Изменение статуса подсистемы'
    };
    return labels[eventType] || eventType;
  };

  const renderEventDetails = (event) => {
    switch (event.eventType) {
      case 'RFC_FIELDS_CHANGED':
        return (
          <div className="event-details">
            <div className="event-operation">
              <strong>Операция:</strong> {event.operation === 'CREATE' ? 'Создание' : 'Обновление'}
            </div>
            {event.changes && Object.keys(event.changes).length > 0 && (
              <div className="event-changes">
                <strong>Изменения:</strong>
                <ul>
                  {Object.entries(event.changes).map(([field, change]) => (
                    <li key={field}>
                      <strong>{field}:</strong>{' '}
                      {change.oldValue !== null && change.oldValue !== undefined
                        ? `${change.oldValue} → ${change.newValue}`
                        : `→ ${change.newValue}`}
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        );

      case 'RFC_ATTACHMENTS_CHANGED':
        return (
          <div className="event-details">
            {event.attachmentsAdded && event.attachmentsAdded.length > 0 && (
              <div className="event-added">
                <strong>Добавлены файлы:</strong>
                <ul>
                  {event.attachmentsAdded.map((file) => (
                    <li key={file.id}>{file.originalFilename}</li>
                  ))}
                </ul>
              </div>
            )}
            {event.attachmentsRemoved && event.attachmentsRemoved.length > 0 && (
              <div className="event-removed">
                <strong>Удалены файлы:</strong>
                <ul>
                  {event.attachmentsRemoved.map((file) => (
                    <li key={file.id}>{file.originalFilename}</li>
                  ))}
                </ul>
              </div>
            )}
            {(!event.attachmentsAdded || event.attachmentsAdded.length === 0) &&
              (!event.attachmentsRemoved || event.attachmentsRemoved.length === 0) && (
                <span>Нет изменений</span>
              )}
          </div>
        );

      case 'RFC_SUBSYSTEMS_CHANGED':
        return (
          <div className="event-details">
            {event.subsystemsAdded && event.subsystemsAdded.length > 0 && (
              <div className="event-added">
                <strong>Добавлены подсистемы:</strong>
                <ul>
                  {event.subsystemsAdded.map((subsystem) => (
                    <li key={subsystem.id}>
                      {subsystem.subsystemName} ({subsystem.systemName})
                      {subsystem.executorName && ` - Исполнитель: ${subsystem.executorName}`}
                    </li>
                  ))}
                </ul>
              </div>
            )}
            {event.subsystemsRemoved && event.subsystemsRemoved.length > 0 && (
              <div className="event-removed">
                <strong>Удалены подсистемы:</strong>
                <ul>
                  {event.subsystemsRemoved.map((subsystem) => (
                    <li key={subsystem.id}>
                      {subsystem.subsystemName} ({subsystem.systemName})
                    </li>
                  ))}
                </ul>
              </div>
            )}
            {(!event.subsystemsAdded || event.subsystemsAdded.length === 0) &&
              (!event.subsystemsRemoved || event.subsystemsRemoved.length === 0) && (
                <span>Нет изменений</span>
              )}
          </div>
        );

      case 'SUBSYSTEM_STATUS_CHANGED':
        return (
          <div className="event-details">
            <div className="event-subsystem">
              <strong>Подсистема:</strong> {event.subsystem?.subsystemName} ({event.subsystem?.systemName})
            </div>
            <div className="event-status">
              <strong>Тип статуса:</strong>{' '}
              {event.statusType === 'CONFIRMATION' ? 'Подтверждение' : 'Выполнение'}
            </div>
            <div className="event-status-change">
              <strong>Изменение:</strong>{' '}
              {event.oldStatus !== null && event.oldStatus !== undefined
                ? `${event.oldStatus} → ${event.newStatus}`
                : `→ ${event.newStatus}`}
            </div>
          </div>
        );

      default:
        return <span>Неизвестный тип события</span>;
    }
  };

  if (events.length === 0) {
    return (
      <div className="empty-state">
        <div className="empty-state-icon">📋</div>
        <h3>История изменений не найдена</h3>
        <p>Введите ID RFC и нажмите "Найти" для просмотра истории изменений</p>
      </div>
    );
  }

  return (
    <table className="rfc-audit-table">
      <thead>
        <tr>
          <th style={{ width: '180px' }}>Время</th>
          <th style={{ width: '200px' }}>Тип события</th>
          <th>Детали</th>
          <th style={{ width: '150px' }}>Изменил</th>
        </tr>
      </thead>
      <tbody>
        {events.map((event, index) => (
          <tr key={index}>
            <td>{formatDateTime(event.timestamp)}</td>
            <td>
              <span className="event-type-badge">{getEventTypeLabel(event.eventType)}</span>
            </td>
            <td>{renderEventDetails(event)}</td>
            <td>
              {event.changedBy ? (
                <div className="changed-by">
                  <div className="changed-by-name">{event.changedBy.name}</div>
                  <div className="changed-by-id">ID: {event.changedBy.id}</div>
                </div>
              ) : (
                '-'
              )}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

