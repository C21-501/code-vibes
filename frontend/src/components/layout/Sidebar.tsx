import React from 'react';
import { Link, useLocation } from 'react-router-dom';

interface NavItem {
  id: string;
  label: string;
  icon: string;
  path: string;
}

const navItems: NavItem[] = [
  { id: 'dashboard', label: 'Дашборд', icon: 'fa-th-large', path: '/dashboard' },
  { id: 'my-rfcs', label: 'Мои RFC', icon: 'fa-list', path: '/my-rfcs' },
  { id: 'all-rfcs', label: 'Все RFC', icon: 'fa-layer-group', path: '/all-rfcs' },
  { id: 'references', label: 'Справочники', icon: 'fa-book', path: '/references' },
  { id: 'settings', label: 'Настройки', icon: 'fa-cog', path: '/settings' },
];

export const Sidebar: React.FC = () => {
  const location = useLocation();

  return (
    <div className="sidebar">
      {/* Header */}
      <div className="sidebar-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <i className="fas fa-tasks fa-2x"></i>
          <span>CAB System</span>
        </div>
      </div>

      {/* Navigation */}
      <nav className="sidebar-nav">
        <ul className="sidebar-menu">
          {navItems.map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <li key={item.id} className={isActive ? 'active' : ''}>
                <Link to={item.path}>
                  <i className={`fas ${item.icon}`}></i>
                  <span>{item.label}</span>
                </Link>
              </li>
            );
          })}
        </ul>
      </nav>
    </div>
  );
};