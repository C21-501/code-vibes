/**
 * Sidebar Component
 * Navigation sidebar with menu items and logout button

import { useNavigate, Link, useLocation } from 'react-router-dom';
import './Sidebar.css';

export default function Sidebar({ currentPage = 'users' }) {
  const navigate = useNavigate();
  const location = useLocation();
  
  const handleLogout = () => {
    if (confirm('Вы уверены, что хотите выйти из системы?')) {
      console.log('Logging out...');
      
      // Clear all authentication data from localStorage
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('tokenType');
      localStorage.removeItem('expiresIn');
      localStorage.removeItem('tokenExpiration');
      localStorage.removeItem('refreshTokenExpiration');
      
      // Redirect to login page using React Router
      navigate('/login', { replace: true });
    }
  };

  return (
    <aside className="sidebar">
      <div className="logo">RFC System</div>
      <nav>
        <ul className="nav-menu">
          <li className="nav-item">
            <span className="nav-link disabled">
              📋 Список RFC
            </span>
          </li>
          <li className="nav-item">
            <Link 
              to="/teams" 
              className={`nav-link ${location.pathname === '/teams' ? 'active' : ''}`}
            >
              👥 Команды
            </Link>
          </li>
          <li className="nav-item">
            <Link 
              to="/systems" 
              className={`nav-link ${location.pathname === '/systems' ? 'active' : ''}`}
            >
              🖥️ Системы
            </Link>
          </li>
          <li className="nav-item">
            <Link 
              to="/users" 
              className={`nav-link ${location.pathname === '/users' ? 'active' : ''}`}
            >
              👤 Пользователи
            </Link>
          </li>
        </ul>
      </nav>
      <div className="sidebar-footer">
        <button className="btn-logout" onClick={handleLogout}>
          <span>🚪</span>
          <span>Выйти</span>
        </button>
      </div>
    </aside>
  );
}
*/

