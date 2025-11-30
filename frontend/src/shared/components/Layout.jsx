// src/shared/components/Layout.jsx
import React, { useState, useEffect } from 'react';
import { useAuth } from '../../features/auth/context/AuthContext';
import { useNavigate, useLocation } from 'react-router-dom';
import { isAdmin } from '../../utils/jwtUtils';
import './Layout.css';

const Layout = ({ children }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [userIsAdmin, setUserIsAdmin] = useState(false);

  // Check admin status on mount
  useEffect(() => {
    setUserIsAdmin(isAdmin());
  }, []);

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
          {userIsAdmin && (
            <button
              className={`nav-link ${isActive('/audit-rfc') ? 'active' : ''}`}
              onClick={() => handleNavigation('/audit-rfc')}
            >
              🔍 Аудит RFC
            </button>
          )}
        </nav>

        <div className="sidebar-footer">
          <button className="btn-logout" onClick={handleLogout}>
            🚪 Выйти
          </button>
        </div>
      </aside>

      {/* Main Content */}
      <main className="main-content">
        {/* Page Content - сразу контент страницы без header */}
        <div className="page-content">
          {children}
        </div>
      </main>
    </div>
  );
};

export default Layout;