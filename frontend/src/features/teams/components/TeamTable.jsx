/**
 * TeamTable Component
 * Displays teams in a table format with action buttons
 */
import './TeamTable.css';

export default function TeamTable({ teams, onView, onEdit, onDelete, isAdmin }) {
  if (!teams || teams.length === 0) {
    return (
      <div className="empty-state">
        <div className="empty-state-icon">👥</div>
        <h3>Команды не найдены</h3>
        <p>Попробуйте изменить параметры поиска или создайте новую команду</p>
      </div>
    );
  }

  const getInitials = (firstName, lastName) => {
    return (firstName.charAt(0) + lastName.charAt(0)).toUpperCase();
  };

  return (
    <table className="team-table">
      <thead>
        <tr>
          <th style={{ width: '80px' }}>ID</th>
          <th>Название</th>
          <th>Описание</th>
          <th>Участники</th>
          <th style={{ width: '180px' }}>Действия</th>
        </tr>
      </thead>
      <tbody>
        {teams.map(team => (
          <tr key={team.id}>
            <td><strong>#{team.id}</strong></td>
            <td><strong>{team.name}</strong></td>
            <td>
              {team.description ? (
                team.description
              ) : (
                <em style={{ color: '#999' }}>Нет описания</em>
              )}
            </td>
            <td>
              <div className="team-members">
                {team.members && team.members.length > 0 ? (
                  team.members.map(member => (
                    <div key={member.id} className="member-badge">
                      <div className="member-avatar">
                        {getInitials(member.firstName, member.lastName)}
                      </div>
                      {member.firstName} {member.lastName}
                    </div>
                  ))
                ) : (
                  <em style={{ color: '#999' }}>Нет участников</em>
                )}
              </div>
            </td>
            <td>
              <div className="action-buttons">
                <button className="btn-view" onClick={() => onView(team)}>
                  👁️ Просмотр
                </button>
                {isAdmin && (
                  <>
                    <button className="btn-edit" onClick={() => onEdit(team)}>
                      ✏️ Изменить
                    </button>
                    <button className="btn-delete" onClick={() => onDelete(team)}>
                      🗑️ Удалить
                    </button>
                  </>
                )}
              </div>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

