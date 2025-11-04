/**
 * Sidebar Component
 * Navigation sidebar with menu items and logout button
 */
import './Sidebar.css';

export default function Sidebar({ currentPage = 'users' }) {
  const handleLogout = () => {
    if (confirm('Вы уверены, что хотите выйти из системы?')) {
      console.log('Logging out...');
      // TODO: Implement actual logout logic
      // window.location.href = '/login';
    }
  };

  return (
    <aside className="sidebar">
      <div className="logo">RFC System</div>
      <nav>
        <ul className="nav-menu">
          <li className="nav-item">
            <a href="#rfc-list" className="nav-link">
              📋 Список RFC
            </a>
          </li>
          <li className="nav-item">
            <a href="#teams" className="nav-link">
              👥 Команды
            </a>
          </li>
          <li className="nav-item">
            <a href="#systems" className="nav-link">
              🖥️ Системы
            </a>
          </li>
          <li className="nav-item">
            <a href="#subsystems" className="nav-link">
              🔧 Подсистемы
            </a>
          </li>
          <li className="nav-item">
            <a 
              href="#users" 
              className={`nav-link ${currentPage === 'users' ? 'active' : ''}`}
            >
              👤 Пользователи
            </a>
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

