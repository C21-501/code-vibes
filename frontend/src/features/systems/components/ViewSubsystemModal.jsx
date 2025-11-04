/**
 * ViewSubsystemModal Component
 * Modal for viewing subsystem details
 */
import { useState, useEffect } from 'react';
import { getTeamById } from '../../teams/api/teamApi';
import '../../../shared/components/Modal.css';
import './ViewSubsystemModal.css';

export default function ViewSubsystemModal({ subsystem, system, isOpen, onClose }) {
  const [team, setTeam] = useState(null);
  const [loadingTeam, setLoadingTeam] = useState(false);

  useEffect(() => {
    if (isOpen && subsystem && subsystem.teamId) {
      fetchTeam(subsystem.teamId);
    }
  }, [isOpen, subsystem]);

  const fetchTeam = async (teamId) => {
    try {
      setLoadingTeam(true);
      const teamData = await getTeamById(teamId);
      setTeam(teamData);
    } catch (error) {
      console.error('Failed to fetch team:', error);
      setTeam(null);
    } finally {
      setLoadingTeam(false);
    }
  };

  if (!isOpen || !subsystem) return null;

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  return (
    <div className="modal active" onClick={handleBackdropClick}>
      <div className="modal-content view-subsystem-modal">
        <div className="modal-header">
          <h2>{subsystem.name}</h2>
          <button className="close" onClick={onClose}>&times;</button>
        </div>
        <div className="modal-body">
          <div className="detail-section">
            <h3>Основная информация</h3>
            <div className="detail-row">
              <div className="detail-label">ID подсистемы:</div>
              <div className="detail-value"><strong>#{subsystem.id}</strong></div>
            </div>
            <div className="detail-row">
              <div className="detail-label">Название:</div>
              <div className="detail-value"><strong>{subsystem.name}</strong></div>
            </div>
            <div className="detail-row">
              <div className="detail-label">Описание:</div>
              <div className="detail-value">
                {subsystem.description ? (
                  subsystem.description
                ) : (
                  <em style={{ color: '#999' }}>Нет описания</em>
                )}
              </div>
            </div>
          </div>

          <div className="detail-section">
            <h3>Связанные сущности</h3>
            <div className="detail-row">
              <div className="detail-label">Система:</div>
              <div className="detail-value">
                {system ? (
                  <div>
                    <strong>{system.name}</strong>
                    {system.description && (
                      <div style={{ marginTop: '5px', fontSize: '14px', color: '#7f8c8d' }}>
                        {system.description}
                      </div>
                    )}
                  </div>
                ) : (
                  <span>ID: #{subsystem.systemId}</span>
                )}
              </div>
            </div>
            <div className="detail-row">
              <div className="detail-label">Ответственная команда:</div>
              <div className="detail-value">
                {loadingTeam ? (
                  <em style={{ color: '#999' }}>Загрузка...</em>
                ) : team ? (
                  <div>
                    <strong>{team.name}</strong>
                    {team.description && (
                      <div style={{ marginTop: '5px', fontSize: '14px', color: '#7f8c8d' }}>
                        {team.description}
                      </div>
                    )}
                  </div>
                ) : (
                  <span>ID: #{subsystem.teamId}</span>
                )}
              </div>
            </div>
          </div>
        </div>
        <div className="modal-footer">
          <button className="btn btn-primary" onClick={onClose}>Закрыть</button>
        </div>
      </div>
    </div>
  );
}

