/**
 * ViewSystemModal Component
 * Modal for viewing system details
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

        </div>
        <div className="modal-footer">
          <button className="btn btn-primary" onClick={onClose}>Закрыть</button>
        </div>
      </div>
    </div>
  );
}

