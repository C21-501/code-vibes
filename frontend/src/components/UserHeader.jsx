/**
 * UserHeader Component
 * Displays current user information in the header
 */
import './UserHeader.css';

export default function UserHeader({ user }) {
  if (!user) return null;

  // Get user initials from first and last name
  const initials = `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`;
  const fullName = `${user.firstName} ${user.lastName}`;

  // Role mapping based on UserRole enum from OpenAPI spec
  const roleMap = {
    'REQUESTER': { class: 'role-requester', text: 'Инициатор' },
    'EXECUTOR': { class: 'role-executor', text: 'Исполнитель' },
    'CAB_MANAGER': { class: 'role-cab-manager', text: 'Менеджер CAB' },
    'ADMIN': { class: 'role-admin', text: 'Администратор' }
  };

  const roleInfo = roleMap[user.role] || roleMap['REQUESTER'];

  return (
    <div className="user-info">
      <div className="user-avatar">{initials}</div>
      <div className="user-details">
        <div className="user-name">{fullName}</div>
        <div className="user-role">
          <span className={`role-badge ${roleInfo.class}`}>
            {roleInfo.text}
          </span>
        </div>
      </div>
    </div>
  );
}

