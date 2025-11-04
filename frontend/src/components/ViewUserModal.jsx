/**
 * ViewUserModal Component
 * Modal for viewing user details (read-only)
 */
import './Modal.css';

export default function ViewUserModal({ user, isOpen, onClose }) {
  if (!isOpen || !user) return null;

  const getRoleLabel = (role) => {
    const labels = {
      'REQUESTER': 'Инициатор',
      'EXECUTOR': 'Исполнитель',
      'CAB_MANAGER': 'CAB Менеджер',
      'ADMIN': 'Администратор'
    };
    return labels[role] || role;
  };

  const getRoleClass = (role) => {
    return `role-badge role-${role.toLowerCase().replace('_', '-')}`;
  };

  const escapeHtml = (text) => {
    const map = {
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
  };

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  return (
    <div className="modal active" onClick={handleBackdropClick}>
      <div className="modal-content">
        <div className="modal-header">
          <h2>{user.firstName} {user.lastName}</h2>
          <button className="close" onClick={onClose}>&times;</button>
        </div>
        <div className="modal-body">
          <div className="detail-section">
            <h3>Основная информация</h3>
            <div className="detail-row">
              <div className="detail-label">ID пользователя:</div>
              <div className="detail-value"><strong>#{user.id}</strong></div>
            </div>
            <div className="detail-row">
              <div className="detail-label">Username:</div>
              <div className="detail-value"><strong>@{escapeHtml(user.username)}</strong></div>
            </div>
            <div className="detail-row">
              <div className="detail-label">Имя:</div>
              <div className="detail-value">{escapeHtml(user.firstName)}</div>
            </div>
            <div className="detail-row">
              <div className="detail-label">Фамилия:</div>
              <div className="detail-value">{escapeHtml(user.lastName)}</div>
            </div>
            <div className="detail-row">
              <div className="detail-label">Роль:</div>
              <div className="detail-value">
                <span className={getRoleClass(user.role)}>
                  {getRoleLabel(user.role)}
                </span>
              </div>
            </div>
          </div>
        </div>
        <div className="modal-footer">
          <button className="btn btn-primary" onClick={onClose}>
            Закрыть
          </button>
        </div>
      </div>
    </div>
  );
}

