/**
 * ViewSystemModal Component
 * Modal for viewing system details and subsystems
 */
import '../../../shared/components/Modal.css';
import './ViewSystemModal.css';

export default function ViewSystemModal({ system, isOpen, onClose }) {
  if (!isOpen || !system) return null;

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  return (
    <div className="modal active" onClick={handleBackdropClick}>
      <div className="modal-content view-system-modal">
        <div className="modal-header">
          <h2>{system.name}</h2>
          <button className="close" onClick={onClose}>&times;</button>
        </div>
        <div className="modal-body">
          <div className="detail-section">
            <h3>Основная информация</h3>
            <div className="detail-row">
              <div className="detail-label">ID системы:</div>
              <div className="detail-value"><strong>#{system.id}</strong></div>
            </div>
            <div className="detail-row">
              <div className="detail-label">Название:</div>
              <div className="detail-value"><strong>{system.name}</strong></div>
            </div>
            <div className="detail-row">
              <div className="detail-label">Описание:</div>
              <div className="detail-value">
                {system.description ? (
                  system.description
                ) : (
                  <em style={{ color: '#999' }}>Нет описания</em>
                )}
              </div>
            </div>
          </div>

          <div className="detail-section">
            <h3>Подсистемы ({system.subsystems?.length || 0})</h3>
            {system.subsystems && system.subsystems.length > 0 ? (
              <div className="subsystems-list">
                {system.subsystems.map(subsystem => (
                  <div key={subsystem.id} className="subsystem-card">
                    <div className="subsystem-header">
                      <div className="subsystem-title">
                        <strong>{subsystem.name}</strong>
                        <span className="subsystem-id">ID: #{subsystem.id}</span>
                      </div>
                    </div>
                    {subsystem.description && (
                      <div className="subsystem-description">
                        {subsystem.description}
                      </div>
                    )}
                    <div className="subsystem-footer">
                      <span className="subsystem-info">
                        <span className="info-label">Система:</span> #{subsystem.systemId}
                      </span>
                      <span className="subsystem-info">
                        <span className="info-label">Команда:</span> #{subsystem.teamId}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <p style={{ color: '#999', fontStyle: 'italic' }}>
                Нет подсистем в этой системе
              </p>
            )}
            {system.subsystems && system.subsystems.length > 0 && (
              <div className="subsystems-note">
                <small>
                  ℹ️ Для управления подсистемами используйте раздел "Подсистемы" (в разработке)
                </small>
              </div>
            )}
          </div>
        </div>
        <div className="modal-footer">
          <button className="btn btn-primary" onClick={onClose}>Закрыть</button>
        </div>
      </div>
    </div>
  );
}

