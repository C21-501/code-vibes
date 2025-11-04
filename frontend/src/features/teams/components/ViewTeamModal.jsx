/**
 * ViewTeamModal Component
 * Modal for viewing team details and members
 */
import '../../../shared/components/Modal.css';
import './ViewTeamModal.css';

export default function ViewTeamModal({ team, isOpen, onClose }) {
  if (!isOpen || !team) return null;

  const getInitials = (firstName, lastName) => {
    return (firstName.charAt(0) + lastName.charAt(0)).toUpperCase();
  };

  const getRoleLabel = (role) => {
    const labels = {
      'REQUESTER': 'Инициатор',
      'EXECUTOR': 'Исполнитель',
      'CAB_MANAGER': 'CAB Менеджер',
      'ADMIN': 'Администратор'
    };
    return labels[role] || role;
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
          <h2>{team.name}</h2>
          <button className="close" onClick={onClose}>&times;</button>
        </div>
        <div className="modal-body">
          <div className="detail-section">
            <h3>Основная информация</h3>
            <div className="detail-row">
              <div className="detail-label">ID команды:</div>
              <div className="detail-value"><strong>#{team.id}</strong></div>
            </div>
            <div className="detail-row">
              <div className="detail-label">Название:</div>
              <div className="detail-value"><strong>{team.name}</strong></div>
            </div>
            <div className="detail-row">
              <div className="detail-label">Описание:</div>
              <div className="detail-value">
                {team.description ? (
                  team.description
                ) : (
                  <em style={{ color: '#999' }}>Нет описания</em>
                )}
              </div>
            </div>
          </div>

          <div className="detail-section">
            <h3>Участники команды ({team.members?.length || 0})</h3>
            {team.members && team.members.length > 0 ? (
              <div className="member-list">
                {team.members.map(member => (
                  <div key={member.id} className="member-card">
                    <div className="member-avatar">
                      {getInitials(member.firstName, member.lastName)}
                    </div>
                    <div className="member-info">
                      <div className="name">{member.firstName} {member.lastName}</div>
                      <div className="username">@{member.username} • {getRoleLabel(member.role)}</div>
                    </div>
                    <span className={`role-badge role-${member.role.toLowerCase().replace('_', '-')}`}>
                      {member.role}
                    </span>
                  </div>
                ))}
              </div>
            ) : (
              <p style={{ color: '#999', fontStyle: 'italic' }}>Нет участников в команде</p>
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

