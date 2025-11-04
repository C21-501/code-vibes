/**
 * SystemTable Component
 * Displays systems in a table format with action buttons and expandable subsystems
 */
import { useState, useEffect } from 'react';
import { getSystemSubsystems } from '../api/subsystemApi';
import { getTeamById } from '../../teams/api/teamApi';
import './SystemTable.css';

export default function SystemTable({ 
  systems, 
  onView, 
  onEdit, 
  onDelete,
  onViewSubsystem,
  onEditSubsystem,
  onDeleteSubsystem,
  onAddSubsystem,
  subsystemRefreshTrigger
}) {
  // State for expanded systems and their subsystems
  const [expandedSystems, setExpandedSystems] = useState({});

  // Effect to refresh all expanded subsystems when subsystemRefreshTrigger changes
  useEffect(() => {
    if (subsystemRefreshTrigger > 0) {
      // Refresh all currently expanded systems
      Object.keys(expandedSystems).forEach(systemId => {
        if (expandedSystems[systemId]?.expanded) {
          refreshSubsystems(parseInt(systemId, 10));
        }
      });
    }
  }, [subsystemRefreshTrigger]);

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

  const handleToggleExpand = async (system) => {
    const systemId = system.id;
    const currentState = expandedSystems[systemId];

    // If already expanded, just collapse
    if (currentState?.expanded) {
      setExpandedSystems(prev => ({
        ...prev,
        [systemId]: { ...prev[systemId], expanded: false }
      }));
      return;
    }

    // Always fetch subsystems on expand (no caching)
    setExpandedSystems(prev => ({
      ...prev,
      [systemId]: { expanded: false, loading: true, subsystems: null, teams: {} }
    }));

    try {
      const subsystems = await getSystemSubsystems(systemId);
      
      // Fetch team names for all unique teamIds
      const uniqueTeamIds = [...new Set(subsystems.map(sub => sub.teamId))];
      const teamsData = {};
      
      await Promise.all(
        uniqueTeamIds.map(async (teamId) => {
          try {
            const team = await getTeamById(teamId);
            teamsData[teamId] = team;
          } catch (error) {
            console.error(`Failed to fetch team ${teamId}:`, error);
            teamsData[teamId] = { id: teamId, name: `Team #${teamId}` };
          }
        })
      );
      
      setExpandedSystems(prev => ({
        ...prev,
        [systemId]: { expanded: true, loading: false, subsystems, teams: teamsData }
      }));
    } catch (error) {
      console.error('Failed to fetch subsystems:', error);
      setExpandedSystems(prev => ({
        ...prev,
        [systemId]: { expanded: false, loading: false, subsystems: null, teams: {}, error: error.message }
      }));
    }
  };

  const refreshSubsystems = async (systemId) => {
    // Only refresh if currently expanded
    if (!expandedSystems[systemId]?.expanded) {
      return;
    }
    
    try {
      const subsystems = await getSystemSubsystems(systemId);
      
      // Fetch team names for all unique teamIds
      const uniqueTeamIds = [...new Set(subsystems.map(sub => sub.teamId))];
      const teamsData = {};
      
      await Promise.all(
        uniqueTeamIds.map(async (teamId) => {
          try {
            const team = await getTeamById(teamId);
            teamsData[teamId] = team;
          } catch (error) {
            console.error(`Failed to fetch team ${teamId}:`, error);
            teamsData[teamId] = { id: teamId, name: `Team #${teamId}` };
          }
        })
      );
      
      setExpandedSystems(prev => ({
        ...prev,
        [systemId]: { 
          ...prev[systemId],
          subsystems,
          teams: teamsData,
          error: null
        }
      }));
    } catch (error) {
      console.error('Failed to refresh subsystems:', error);
    }
  };

  const handleSubsystemAction = async (action, systemId, subsystem = null) => {
    await action(systemId, subsystem);
    // Note: Subsystems refresh is now handled by SystemManagement via subsystemRefreshTrigger
  };

  return (
    <table className="system-table">
      <thead>
        <tr>
          <th style={{ width: '50px' }}></th>
          <th style={{ width: '80px' }}>ID</th>
          <th>Система</th>
          <th>Описание</th>
          <th style={{ width: '150px' }}>Подсистемы</th>
          <th style={{ width: '250px' }}>Действия</th>
        </tr>
      </thead>
      <tbody>
        {systems.map(system => {
          const systemState = expandedSystems[system.id];
          const isExpanded = systemState?.expanded || false;
          const isLoading = systemState?.loading || false;
          const subsystems = systemState?.subsystems || [];
          const teams = systemState?.teams || {};
          const error = systemState?.error;

          return (
            <>
              <tr key={system.id} className="system-row">
                <td>
                  <button
                    className="expand-button"
                    onClick={() => handleToggleExpand(system)}
                    title={isExpanded ? 'Свернуть подсистемы' : 'Развернуть подсистемы'}
                  >
                    {isLoading ? '⏳' : isExpanded ? '▼' : '▶'}
                  </button>
                </td>
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
                    {subsystems.length > 0 ? (
                      <span className="badge badge-subsystems">
                        {getSubsystemsLabel(subsystems.length)}
                      </span>
                    ) : system.subsystems && system.subsystems.length > 0 ? (
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
                    <button 
                      className="btn-add-subsystem" 
                      onClick={() => handleSubsystemAction(onAddSubsystem, system.id)}
                    >
                      ➕ Подсистему
                    </button>
                  </div>
                </td>
              </tr>
              {isExpanded && (
                <tr key={`${system.id}-subsystems`} className="subsystems-row">
                  <td colSpan="6">
                    <div className="subsystems-container">
                      {error ? (
                        <div className="subsystems-error">
                          ❌ Ошибка загрузки подсистем: {error}
                        </div>
                      ) : subsystems.length > 0 ? (
                        <table className="subsystems-table">
                          <thead>
                            <tr>
                              <th style={{ width: '80px' }}>ID</th>
                              <th>Подсистема</th>
                              <th>Описание</th>
                              <th style={{ width: '120px' }}>Команда</th>
                              <th style={{ width: '200px' }}>Действия</th>
                            </tr>
                          </thead>
                          <tbody>
                            {subsystems.map(subsystem => {
                              const team = teams[subsystem.teamId];
                              return (
                                <tr key={subsystem.id}>
                                  <td><strong>#{subsystem.id}</strong></td>
                                  <td><strong>{subsystem.name}</strong></td>
                                  <td>
                                    {subsystem.description ? (
                                      subsystem.description
                                    ) : (
                                      <em style={{ color: '#999' }}>Нет описания</em>
                                    )}
                                  </td>
                                  <td>
                                    <span className="team-badge">
                                      {team ? team.name : `ID: #${subsystem.teamId}`}
                                    </span>
                                  </td>
                                  <td>
                                    <div className="action-buttons">
                                      <button 
                                        className="btn-view" 
                                        onClick={() => onViewSubsystem(system.id, subsystem, system)}
                                      >
                                        👁️ Просмотр
                                      </button>
                                      <button 
                                        className="btn-edit" 
                                        onClick={() => handleSubsystemAction(onEditSubsystem, system.id, subsystem)}
                                      >
                                        ✏️ Изменить
                                      </button>
                                      <button 
                                        className="btn-delete" 
                                        onClick={() => handleSubsystemAction(onDeleteSubsystem, system.id, subsystem)}
                                      >
                                        🗑️ Удалить
                                      </button>
                                    </div>
                                  </td>
                                </tr>
                              );
                            })}
                          </tbody>
                        </table>
                      ) : (
                        <div className="subsystems-empty">
                          <em>У этой системы пока нет подсистем</em>
                        </div>
                      )}
                    </div>
                  </td>
                </tr>
              )}
            </>
          );
        })}
      </tbody>
    </table>
  );
}

