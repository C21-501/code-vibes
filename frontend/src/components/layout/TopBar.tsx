import React from 'react';
import { Bell, User, LogOut, Settings, Plus } from 'lucide-react';

interface TopBarProps {
  className?: string;
}

export const TopBar: React.FC<TopBarProps> = ({ className = '' }) => {
  // Mock user data for development
  const user = {
    firstName: 'Иван',
    lastName: 'Петров',
    role: 'Исполнитель'
  };

  const fullName = `${user.firstName} ${user.lastName}`;
  const initials = `${user.firstName[0]}${user.lastName[0]}`;

  const headerStyle = {
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    backdropFilter: 'blur(8px)',
    borderBottom: '1px solid #e5e7eb',
    padding: '16px 24px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between'
  };

  const userInfoStyle = {
    display: 'flex',
    alignItems: 'center',
    gap: '16px'
  };

  const userRoleStyle = {
    fontSize: '14px',
    color: '#6b7280',
    fontWeight: '500'
  };

  const avatarStyle = {
    width: '40px',
    height: '40px',
    borderRadius: '50%',
    backgroundColor: '#3498db',
    color: 'white',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontWeight: 'bold',
    fontSize: '14px'
  };

  const userNameStyle = {
    fontSize: '16px',
    fontWeight: '500',
    color: '#111827'
  };

  const buttonStyle = {
    display: 'inline-flex',
    alignItems: 'center',
    gap: '8px',
    padding: '10px 16px',
    backgroundColor: '#3498db',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '500',
    transition: 'background-color 0.3s ease'
  };

  return (
    <header style={headerStyle}>
      {/* Left side - can be used for breadcrumbs or page title */}
      <div style={{ flex: 1 }} />

      {/* Right side - user info and actions */}
      <div style={userInfoStyle}>
        <div style={userRoleStyle}>{user.role}</div>
        <div style={avatarStyle}>{initials}</div>
        <div style={userNameStyle}>{fullName}</div>
        <button
          style={buttonStyle}
          onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#2980b9'}
          onMouseLeave={(e) => e.currentTarget.style.backgroundColor = '#3498db'}
        >
          <Plus size={16} />
          Создать RFC
        </button>
      </div>
    </header>
  );
}
