/**
 * SystemTable Component
 * Displays systems in a table format with action buttons
 */
import './SystemTable.css';

export default function SystemTable({ systems, onView, onEdit, onDelete }) {
  if (!systems || systems.length === 0) {
    return (
      <div className="empty-state">
        <div className="empty-state-icon">🖥️</div>
        <h3>Системы не найдены</h3>
        <p>Попробуйте изменить параметры поиска или создайте новую систему</p>
      </div>
    );
  }

  const getSubsystemsLabel = (count) => {
    if (count === 0) return 'Нет подсистем';
    if (count === 1) return '1 подсистема';
    if (count >= 2 && count <= 4) return `${count} подсистемы`;
    return `${count} подсистем`;
  };

  return (
    <table className="system-table">
      <thead>
        <tr>
          <th style={{ width: '80px' }}>ID</th>
          <th>Название</th>
          <th>Описание</th>
          <th style={{ width: '150px' }}>Подсистемы</th>
          <th style={{ width: '180px' }}>Действия</th>
        </tr>
      </thead>
      <tbody>
        {systems.map(system => (
          <tr key={system.id}>
            <td><strong>#{system.id}</strong></td>
            <td><strong>{system.name}</strong></td>
            <td>
              {system.description ? (
                system.description
              ) : (
                <em style={{ color: '#999' }}>Нет описания</em>
              )}
            </td>
            <td>
              <div className="subsystems-badge">
                {system.subsystems && system.subsystems.length > 0 ? (
                  <span className="badge badge-subsystems">
                    {getSubsystemsLabel(system.subsystems.length)}
                  </span>
                ) : (
                  <em style={{ color: '#999' }}>Нет подсистем</em>
                )}
              </div>
            </td>
            <td>
              <div className="action-buttons">
                <button className="btn-view" onClick={() => onView(system)}>
                  👁️ Просмотр
                </button>
                <button className="btn-edit" onClick={() => onEdit(system)}>
                  ✏️ Изменить
                </button>
                <button className="btn-delete" onClick={() => onDelete(system)}>
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

