/**
 * UserTable Component
 * Displays users in a table with action buttons
 */
import './UserTable.css';

export default function UserTable({ users, onView, onEdit, onDelete }) {
  // Role mapping based on UserRole enum from OpenAPI spec
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

  if (users.length === 0) {
    return (
      <div className="empty-state">
        <div className="empty-state-icon">👤</div>
        <h3>Пользователи не найдены</h3>
        <p>Попробуйте изменить параметры поиска или создайте нового пользователя</p>
      </div>
    );
  }

  return (
    <table className="user-table">
      <thead>
        <tr>
          <th style={{ width: '80px' }}>ID</th>
          <th>Username</th>
          <th>Имя</th>
          <th>Фамилия</th>
          <th>Роль</th>
          <th style={{ width: '180px' }}>Действия</th>
        </tr>
      </thead>
      <tbody>
        {users.map(user => (
          <tr key={user.id}>
            <td><strong>#{user.id}</strong></td>
            <td><strong>@{escapeHtml(user.username)}</strong></td>
            <td>{escapeHtml(user.firstName)}</td>
            <td>{escapeHtml(user.lastName)}</td>
            <td>
              <span className={getRoleClass(user.role)}>
                {getRoleLabel(user.role)}
              </span>
            </td>
            <td>
              <div className="action-buttons">
                <button 
                  className="btn-view" 
                  onClick={() => onView(user)}
                  title="Просмотр"
                >
                  👁️ Просмотр
                </button>
                <button 
                  className="btn-edit" 
                  onClick={() => onEdit(user)}
                  title="Изменить"
                >
                  ✏️ Изменить
                </button>
                <button 
                  className="btn-delete" 
                  onClick={() => onDelete(user)}
                  title="Удалить"
                >
                  🗑️ Удалить
                </button>
              </div>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

