import React, { useState, useEffect } from 'react';
import { useAuth } from '../../auth/context/AuthContext';
import { getSystems } from '../../systems/api/systemApi';
import { getSystemSubsystems } from '../../systems/api/subsystemApi'; // Исправленный импорт
import { getTeams } from '../../teams/api/teamApi';
import { attachmentApi } from '../../../shared/api/attachmentApi';
import LoadingSpinner from '../../../shared/components/LoadingSpinner';
import Toast from '../../../shared/components/Toast';
import './CreateRfcModal.css';

const EditRfcModal = ({ isOpen, onClose, onSubmit, rfc }) => {
  const { user } = useAuth();
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    implementationDate: '',
    urgency: 'PLANNED',
    affectedSystems: [],
    attachmentIds: []
  });
  const [systems, setSystems] = useState([]);
  const [subsystems, setSubsystems] = useState({}); // { systemId: [subsystems] }
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [toast, setToast] = useState({ show: false, type: '', title: '', message: '' });

  // Заполняем форму данными RFC при открытии
  useEffect(() => {
    if (rfc && isOpen) {
      setFormData({
        title: rfc.title || '',
        description: rfc.description || '',
        implementationDate: rfc.implementationDate ?
          new Date(rfc.implementationDate).toISOString().slice(0, 16) : '',
        urgency: rfc.urgency || 'PLANNED',
        affectedSystems: rfc.affectedSystems || [],
        attachmentIds: rfc.attachments ? rfc.attachments.map(a => a.id) : []
      });

      // Загружаем подсистемы для уже выбранных систем
      if (rfc.affectedSystems) {
        rfc.affectedSystems.forEach(system => {
          if (system.systemId) {
            loadSubsystemsForSystem(system.systemId);
          }
        });
      }
    }
  }, [rfc, isOpen]);

  // Загружаем системы и команды
  useEffect(() => {
    if (isOpen) {
      loadSystemsAndTeams();
    }
  }, [isOpen]);

  const loadSystemsAndTeams = async () => {
    setLoading(true);
    try {
      const [systemsResponse, teamsResponse] = await Promise.all([
        getSystems({ page: 0, size: 100 }),
        getTeams({ page: 0, size: 100 })
      ]);

      setSystems(systemsResponse.content || []);
      setTeams(teamsResponse.content || []);
    } catch (error) {
      console.error('Error loading data:', error);
      setError('Не удалось загрузить данные');
    } finally {
      setLoading(false);
    }
  };

  const loadSubsystemsForSystem = async (systemId) => {
    if (!systemId) return;

    try {
      // Используем существующую функцию getSystemSubsystems
      const subsystemsResponse = await getSystemSubsystems(systemId);
      setSubsystems(prev => ({
        ...prev,
        [systemId]: subsystemsResponse || []
      }));
    } catch (error) {
      console.error('Error loading subsystems for system:', systemId, error);
      // Если API не реализовано, используем пустой массив
      setSubsystems(prev => ({
        ...prev,
        [systemId]: []
      }));
    }
  };

  const showToast = (type, title, message) => {
    setToast({ show: true, type, title, message });
    setTimeout(() => setToast({ ...toast, show: false }), 5000);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSystemChange = async (systemIndex, field, value) => {
    if (field === 'systemId') {
      // Загружаем подсистемы при выборе системы
      await loadSubsystemsForSystem(value);
    }

    setFormData(prev => ({
      ...prev,
      affectedSystems: prev.affectedSystems.map((system, index) =>
        index === systemIndex ? { ...system, [field]: value } : system
      )
    }));
  };

  const handleAddSystem = () => {
    setFormData(prev => ({
      ...prev,
      affectedSystems: [
        ...prev.affectedSystems,
        {
          systemId: '',
          affectedSubsystems: []
        }
      ]
    }));
  };

  const handleRemoveSystem = (systemIndex) => {
    setFormData(prev => ({
      ...prev,
      affectedSystems: prev.affectedSystems.filter((_, index) => index !== systemIndex)
    }));
  };

  const handleAddSubsystem = (systemIndex) => {
    setFormData(prev => ({
      ...prev,
      affectedSystems: prev.affectedSystems.map((system, index) =>
        index === systemIndex
          ? {
              ...system,
              affectedSubsystems: [
                ...system.affectedSubsystems,
                {
                  subsystemId: '',
                  executorId: ''
                }
              ]
            }
          : system
      )
    }));
  };

  const handleRemoveSubsystem = (systemIndex, subsystemIndex) => {
    setFormData(prev => ({
      ...prev,
      affectedSystems: prev.affectedSystems.map((system, index) =>
        index === systemIndex
          ? {
              ...system,
              affectedSubsystems: system.affectedSubsystems.filter((_, idx) => idx !== subsystemIndex)
            }
          : system
      )
    }));
  };

  const handleSubsystemChange = (systemIndex, subsystemIndex, field, value) => {
    setFormData(prev => ({
      ...prev,
      affectedSystems: prev.affectedSystems.map((system, sIndex) =>
        sIndex === systemIndex
          ? {
              ...system,
              affectedSubsystems: system.affectedSubsystems.map((subsystem, ssIndex) =>
                ssIndex === subsystemIndex ? { ...subsystem, [field]: value } : subsystem
              )
            }
          : system
      )
    }));
  };

  const handleAttachmentUpload = async (file) => {
    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await attachmentApi.uploadAttachment(formData);
      setFormData(prev => ({
        ...prev,
        attachmentIds: [...prev.attachmentIds, response.id]
      }));

      showToast('success', 'Успех', 'Файл успешно загружен');
      return response;
    } catch (error) {
      const errorMessage = error.response?.data?.errors?.[0]?.message || 'Не удалось загрузить файл';
      showToast('error', 'Ошибка', errorMessage);
      throw error;
    }
  };

  const handleRemoveAttachment = (attachmentId) => {
    setFormData(prev => ({
      ...prev,
      attachmentIds: prev.attachmentIds.filter(id => id !== attachmentId)
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);

    try {
      // Валидация формы
      if (!formData.title.trim()) {
        throw new Error('Название RFC обязательно для заполнения');
      }

      if (!formData.implementationDate) {
        throw new Error('Дата реализации обязательна для заполнения');
      }

      if (formData.affectedSystems.length === 0) {
        throw new Error('Добавьте хотя бы одну затронутую систему');
      }

      // Проверяем, что у всех систем есть подсистемы
      for (const system of formData.affectedSystems) {
        if (!system.systemId) {
          throw new Error('Для всех систем должен быть выбран идентификатор системы');
        }
        if (system.affectedSubsystems.length === 0) {
          throw new Error('Для каждой системы должна быть добавлена хотя бы одна подсистема');
        }
        for (const subsystem of system.affectedSubsystems) {
          if (!subsystem.subsystemId || !subsystem.executorId) {
            throw new Error('Для всех подсистем должны быть выбраны подсистема и исполнитель');
          }
        }
      }

      // Подготовка данных для отправки
      const rfcData = {
        title: formData.title.trim(),
        description: formData.description.trim(),
        implementationDate: new Date(formData.implementationDate).toISOString(),
        urgency: formData.urgency,
        affectedSystems: formData.affectedSystems.map(system => ({
          systemId: parseInt(system.systemId),
          affectedSubsystems: system.affectedSubsystems.map(subsystem => ({
            subsystemId: parseInt(subsystem.subsystemId),
            executorId: parseInt(subsystem.executorId)
          }))
        })),
        attachmentIds: formData.attachmentIds
      };

      await onSubmit(rfc.id, rfcData);
      onClose();
    } catch (error) {
      console.error('Error updating RFC:', error);
      showToast('error', 'Ошибка', error.message || 'Не удалось обновить RFC');
    } finally {
      setSubmitting(false);
    }
  };

  // Получаем подсистемы для конкретной системы
  const getSubsystemsForSystem = (systemId) => {
    return subsystems[systemId] || [];
  };

  // Получаем доступных исполнителей для подсистемы
  const getAvailableExecutors = (subsystemId) => {
    if (!subsystemId) return teams.flatMap(team => team.members || []);

    // Здесь можно добавить логику фильтрации исполнителей по команде подсистемы
    // Пока возвращаем всех пользователей из команд
    return teams.flatMap(team => team.members || []);
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0, 0, 0, 0.5)',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      zIndex: 1000
    }}>
      <div className="modal-content create-rfc-modal">
        <div className="modal-header">
          <h2>Редактировать RFC</h2>
          <button className="close-button" onClick={onClose}>×</button>
        </div>

        <form onSubmit={handleSubmit} className="rfc-form">
          {loading && (
            <div style={{
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              background: 'rgba(255, 255, 255, 0.8)',
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'center',
              alignItems: 'center',
              borderRadius: '5px',
              zIndex: 5
            }}>
              <LoadingSpinner size="medium" />
              <p>Загрузка данных...</p>
            </div>
          )}

          <div className="form-section">
            <div className="form-group">
              <label htmlFor="title">Название RFC *</label>
              <input
                type="text"
                id="title"
                name="title"
                value={formData.title}
                onChange={handleInputChange}
                required
                maxLength={255}
                placeholder="Введите название RFC"
                className={!formData.title.trim() ? 'error' : ''}
              />
              {!formData.title.trim() && <div className="error-message">Название обязательно</div>}
            </div>
          </div>

          <div className="form-section">
            <div className="form-group">
              <label htmlFor="description">Описание</label>
              <textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                rows={4}
                maxLength={5000}
                placeholder="Опишите предлагаемые изменения..."
              />
              <small>Максимум 5000 символов</small>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="implementationDate">Дата реализации *</label>
              <input
                type="datetime-local"
                id="implementationDate"
                name="implementationDate"
                value={formData.implementationDate}
                onChange={handleInputChange}
                required
                className={!formData.implementationDate ? 'error' : ''}
              />
              {!formData.implementationDate && <div className="error-message">Дата реализации обязательна</div>}
            </div>

            <div className="form-group">
              <label htmlFor="urgency">Срочность *</label>
              <select
                id="urgency"
                name="urgency"
                value={formData.urgency}
                onChange={handleInputChange}
                required
              >
                <option value="PLANNED">Плановая</option>
                <option value="URGENT">Срочная</option>
                <option value="EMERGENCY">Критическая</option>
              </select>
            </div>
          </div>

          {/* Секция затронутых систем */}
          <div className="form-section">
            <h3>Затронутые системы и подсистемы *</h3>
            <button type="button" onClick={handleAddSystem} className="btn-add">
              + Добавить систему
            </button>

            {formData.affectedSystems.map((system, systemIndex) => (
              <div key={systemIndex} className="system-group" style={{ marginBottom: '15px', padding: '10px', border: '1px solid #eee', borderRadius: '5px' }}>
                <div className="form-row">
                  <div className="form-group" style={{ flex: 1 }}>
                    <label>Система *</label>
                    <select
                      value={system.systemId}
                      onChange={(e) => handleSystemChange(systemIndex, 'systemId', e.target.value)}
                      required
                      className={!system.systemId ? 'error' : ''}
                    >
                      <option value="">Выберите систему</option>
                      {systems.map(sys => (
                        <option key={sys.id} value={sys.id}>
                          {sys.name}
                        </option>
                      ))}
                    </select>
                    {!system.systemId && <div className="error-message">Выберите систему</div>}
                  </div>
                  <button
                    type="button"
                    onClick={() => handleRemoveSystem(systemIndex)}
                    className="btn-remove"
                    style={{ background: '#e74c3c', color: 'white', border: 'none', padding: '5px 10px', borderRadius: '3px', cursor: 'pointer', marginTop: '25px' }}
                  >
                    ×
                  </button>
                </div>

                <div style={{ marginTop: '10px' }}>
                  <label>Подсистемы *</label>
                  {system.affectedSubsystems.map((subsystem, subsystemIndex) => (
                    <div key={subsystemIndex} className="form-row" style={{ marginBottom: '10px', alignItems: 'center' }}>
                      <div className="form-group" style={{ flex: 1 }}>
                        <select
                          value={subsystem.subsystemId}
                          onChange={(e) => handleSubsystemChange(systemIndex, subsystemIndex, 'subsystemId', e.target.value)}
                          required
                          className={!subsystem.subsystemId ? 'error' : ''}
                        >
                          <option value="">Выберите подсистему</option>
                          {getSubsystemsForSystem(system.systemId).map(subsys => (
                            <option key={subsys.id} value={subsys.id}>
                              {subsys.name}
                            </option>
                          ))}
                        </select>
                        {!subsystem.subsystemId && <div className="error-message">Выберите подсистему</div>}
                      </div>
                      <div className="form-group" style={{ flex: 1 }}>
                        <select
                          value={subsystem.executorId}
                          onChange={(e) => handleSubsystemChange(systemIndex, subsystemIndex, 'executorId', e.target.value)}
                          required
                          className={!subsystem.executorId ? 'error' : ''}
                        >
                          <option value="">Выберите исполнителя</option>
                          {getAvailableExecutors(subsystem.subsystemId).map(member => (
                            <option key={member.id} value={member.id}>
                              {member.firstName} {member.lastName}
                            </option>
                          ))}
                        </select>
                        {!subsystem.executorId && <div className="error-message">Выберите исполнителя</div>}
                      </div>
                      <button
                        type="button"
                        onClick={() => handleRemoveSubsystem(systemIndex, subsystemIndex)}
                        className="btn-remove"
                        style={{ background: '#e74c3c', color: 'white', border: 'none', padding: '5px 10px', borderRadius: '3px', cursor: 'pointer', marginTop: '0' }}
                      >
                        ×
                      </button>
                    </div>
                  ))}
                  <button
                    type="button"
                    onClick={() => handleAddSubsystem(systemIndex)}
                    className="btn-add-subsystem"
                    style={{ background: '#3498db', color: 'white', border: 'none', padding: '8px 12px', borderRadius: '3px', cursor: 'pointer', marginTop: '5px' }}
                  >
                    + Добавить подсистему
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* Секция вложений */}
          <div className="form-section">
            <h3>Вложения</h3>
            <div className="attachments-section">
              <input
                type="file"
                id="file-upload"
                onChange={(e) => {
                  if (e.target.files[0]) {
                    handleAttachmentUpload(e.target.files[0]);
                  }
                }}
                style={{ display: 'none' }}
              />
              <label htmlFor="file-upload" className="btn-upload" style={{
                display: 'inline-block',
                background: '#2ecc71',
                color: 'white',
                padding: '8px 12px',
                borderRadius: '3px',
                cursor: 'pointer',
                marginBottom: '10px'
              }}>
                📎 Загрузить файл
              </label>

              <div className="attachments-list">
                {formData.attachmentIds.map((id, index) => (
                  <div key={id} className="attachment-item" style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    padding: '5px 10px',
                    background: '#f9f9f9',
                    border: '1px solid #ddd',
                    borderRadius: '3px',
                    marginBottom: '5px'
                  }}>
                    <span>Вложение {index + 1} (ID: {id})</span>
                    <button
                      type="button"
                      onClick={() => handleRemoveAttachment(id)}
                      className="btn-remove"
                      style={{ background: '#e74c3c', color: 'white', border: 'none', padding: '2px 6px', borderRadius: '3px', cursor: 'pointer' }}
                    >
                      ×
                    </button>
                  </div>
                ))}
              </div>
            </div>
          </div>

          <div className="form-actions" style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '20px' }}>
            <button
              type="button"
              onClick={onClose}
              className="btn-secondary"
              disabled={submitting}
              style={{
                background: '#95a5a6',
                color: 'white',
                border: 'none',
                padding: '10px 20px',
                borderRadius: '3px',
                cursor: 'pointer'
              }}
            >
              Отмена
            </button>
            <button
              type="submit"
              className="btn-primary"
              disabled={submitting || loading}
              style={{
                background: '#3498db',
                color: 'white',
                border: 'none',
                padding: '10px 20px',
                borderRadius: '3px',
                cursor: 'pointer'
              }}
            >
              {submitting ? <LoadingSpinner size="small" /> : 'Сохранить изменения'}
            </button>
          </div>
        </form>

        <Toast
          show={toast.show}
          type={toast.type}
          title={toast.title}
          message={toast.message}
          onClose={() => setToast({ ...toast, show: false })}
        />
      </div>
    </div>
  );
};

export default EditRfcModal;