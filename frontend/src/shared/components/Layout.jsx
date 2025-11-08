// src/shared/components/Layout.jsx
import React from 'react';
import { useAuth } from '../../features/auth/context/AuthContext';
import { useNavigate, useLocation } from 'react-router-dom';
import './Layout.css';

const Layout = ({ children }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleNavigation = (path) => {
    navigate(path);
  };

  const isActive = (path) => {
    return location.pathname === path;
  };

  return (
    <div className="layout">
      {/* Sidebar */}
      <aside className="sidebar">
        <div className="logo">RFC System</div>
        <nav className="nav-menu">
          <button
            className={`nav-link ${isActive('/rfc') ? 'active' : ''}`}
            onClick={() => handleNavigation('/rfc')}
          >
            📋 Список RFC
          </button>
          <button
            className={`nav-link ${isActive('/teams') ? 'active' : ''}`}
            onClick={() => handleNavigation('/teams')}
          >
            👥 Команды
          </button>
          <button
            className={`nav-link ${isActive('/systems') ? 'active' : ''}`}
            onClick={() => handleNavigation('/systems')}
          >
            🖥️ Системы
          </button>
          <button
            className={`nav-link ${isActive('/users') ? 'active' : ''}`}
            onClick={() => handleNavigation('/users')}
          >
            👤 Пользователи
          </button>
        </nav>

        <div className="sidebar-footer">
          <button className="btn-logout" onClick={handleLogout}>
            🚪 Выйти
          </button>
        </div>
      </aside>

      {/* Main Content */}
      <main className="main-content">
        {/* Header */}
        <header className="header">
          <h1>RFC Management System</h1>
          <div className="user-info">
            <div className="user-avatar">
              {user?.firstName?.[0]}{user?.lastName?.[0]}
            </div>
            <div className="user-details">
              <div className="user-name">
                {user?.firstName} {user?.lastName}
              </div>
              <div className="user-role">
                <span className={`role-badge role-${user?.role?.toLowerCase()}`}>
                  {user?.role}
                </span>
              </div>
            </div>
          </div>
        </header>

        {/* Page Content */}
        <div className="page-content">
          {children}
        </div>
      </main>
    </div>
  );
};

export default Layout;