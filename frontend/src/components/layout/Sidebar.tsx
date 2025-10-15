import React, { useState } from 'react';
import { 
  Home, 
  FileText, 
  Files, 
  Kanban, 
  BookOpen,
  ChevronLeft,
  ChevronRight,
  Layers
} from 'lucide-react';
import { Link, useLocation } from 'react-router-dom';

interface NavItem {
  id: string;
  label: string;
  icon: React.ComponentType<{ size?: number; className?: string }>;
  path: string;
}

const navItems: NavItem[] = [
  { id: 'dashboard', label: 'Дашборд', icon: Home, path: '/dashboard' },
  { id: 'my-rfcs', label: 'Мои RFC', icon: FileText, path: '/my-rfcs' },
  { id: 'all-rfcs', label: 'Все RFC', icon: Files, path: '/all-rfcs' },
  { id: 'kanban', label: 'Канбан-доска', icon: Kanban, path: '/kanban' },
  { id: 'references', label: 'Справочники', icon: BookOpen, path: '/references' },
];

interface SidebarProps {
  className?: string;
}

export const Sidebar: React.FC<SidebarProps> = ({ className = '' }) => {
  const [isCollapsed, setIsCollapsed] = useState(false);
  const location = useLocation();

  const toggleCollapse = () => {
    setIsCollapsed(!isCollapsed);
  };

  const sidebarStyle = {
    position: 'fixed' as const,
    top: 0,
    left: 0,
    bottom: 0,
    zIndex: 40,
    width: isCollapsed ? '64px' : '256px',
    backgroundColor: '#2c3e50',
    color: 'white',
    borderRight: '1px solid #1f2a36',
    transition: 'all 0.3s ease',
    ...(className && {})
  };

  const headerStyle = {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: '12px 16px',
    borderBottom: '1px solid rgba(255,255,255,0.1)'
  };

  const navStyle = {
    padding: '12px',
    overflowY: 'auto' as const,
    height: 'calc(100vh - 56px)'
  };

  const menuItemStyle = (isActive: boolean) => ({
    display: 'flex',
    alignItems: 'center',
    padding: '8px 12px',
    marginBottom: '4px',
    borderRadius: '6px',
    backgroundColor: isActive ? 'rgba(255,255,255,0.1)' : 'transparent',
    color: isActive ? 'white' : 'rgba(255,255,255,0.8)',
    textDecoration: 'none',
    transition: 'all 0.2s ease',
    position: 'relative' as const,
    cursor: 'pointer'
  });

  const activeBarStyle = {
    position: 'absolute' as const,
    left: 0,
    top: 0,
    height: '100%',
    width: '4px',
    backgroundColor: '#3498db',
    borderRadius: '0 2px 2px 0'
  };

  return (
    <aside style={sidebarStyle}>
      {/* Header */}
      <div style={headerStyle}>
        {!isCollapsed && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Layers size={22} style={{ color: 'white' }} />
            <span style={{ fontSize: '16px', fontWeight: '600' }}>CAB System</span>
          </div>
        )}
        <button
          onClick={toggleCollapse}
          style={{
            padding: '8px',
            borderRadius: '6px',
            backgroundColor: 'transparent',
            border: 'none',
            color: 'rgba(255,255,255,0.8)',
            cursor: 'pointer',
            transition: 'background-color 0.2s ease'
          }}
          onMouseEnter={(e) => e.currentTarget.style.backgroundColor = 'rgba(255,255,255,0.1)'}
          onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
          title={isCollapsed ? 'Развернуть меню' : 'Свернуть меню'}
        >
          {isCollapsed ? (
            <ChevronRight size={20} />
          ) : (
            <ChevronLeft size={20} />
          )}
        </button>
      </div>

      {/* Navigation */}
      <nav style={navStyle}>
        <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
          {navItems.map((item) => {
            const isActive = location.pathname === item.path;
            const IconComponent = item.icon;
            return (
              <li key={item.id} style={{ marginBottom: '4px' }}>
                <Link
                  to={item.path}
                  style={menuItemStyle(isActive)}
                  title={isCollapsed ? item.label : ''}
                  onMouseEnter={(e) => {
                    if (!isActive) {
                      e.currentTarget.style.backgroundColor = 'rgba(255,255,255,0.05)';
                      e.currentTarget.style.color = 'white';
                    }
                  }}
                  onMouseLeave={(e) => {
                    if (!isActive) {
                      e.currentTarget.style.backgroundColor = 'transparent';
                      e.currentTarget.style.color = 'rgba(255,255,255,0.8)';
                    }
                  }}
                >
                  {isActive && <span style={activeBarStyle} />}
                  <IconComponent 
                    size={20} 
                    style={{ 
                      color: isActive ? 'white' : 'rgba(255,255,255,0.8)',
                      marginRight: !isCollapsed ? '12px' : '0'
                    }} 
                  />
                  {!isCollapsed && (
                    <span style={{ fontSize: '14px', fontWeight: '500' }}>
                      {item.label}
                    </span>
                  )}
                </Link>
              </li>
            );
          })}
        </ul>
      </nav>
    </aside>
  );
}
