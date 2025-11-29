import React, { useState, useEffect } from 'react';
import './CreateRfcModal.css';
import { getAllSystems } from '../../systems/api/systemApi';
import { getAllSystemSubsystems } from '../../systems/api/subsystemApi';
import { getUsers } from '../../users/api/userApi';
import SingleUserSearchSelect from './SingleUserSearchSelect';

const CreateRfcModal = ({ isOpen, onClose, onSubmit }) => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    implementationDate: '',
    urgency: 'PLANNED',
    affectedSystems: []
  });

  const [errors, setErrors] = useState({});
  const [systems, setSystems] = useState([]);
  const [subsystems, setSubsystems] = useState({}); // { systemId: [subsystems] }
  const [allUsers, setAllUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [systemsLoading, setSystemsLoading] = useState(false);
  const [systemsError, setSystemsError] = useState(null);

  // Сброс формы при открытии/закрытии
  useEffect(() => {
    if (isOpen) {
      setFormData({
        title: '',
        description: '',
        implementationDate: '',
        urgency: 'PLANNED',
        affectedSystems: []
      });
      setErrors({});
      setSystemsError(null);
      loadSystemsAndUsers();
    }
  }, [isOpen]);

  // Загрузка всех систем и пользователей
  const loadSystemsAndUsers = async () => {
    setSystemsLoading(true);
    setSystemsError(null);
    try {
      const [systemsData, usersData] = await Promise.all([
        getAllSystems(),
        getUsers({ page: 0, size: 100 })
      ]);
      setSystems(systemsData);
      setAllUsers(usersData.content || []);
    } catch (error) {
      console.error('Failed to load systems:', error);
      setSystemsError('Не удалось загрузить список систем. Пожалуйста, попробуйте позже.');
    } finally {
      setSystemsLoading(false);
    }
  };

  // Загрузка подсистем для системы
  const loadSubsystems = async (systemId) => {
    if (subsystems[systemId]) return; // Уже загружены

    try {
      const subsystemsData = await getAllSystemSubsystems(systemId);
      setSubsystems(prev => ({
        ...prev,
        [systemId]: subsystemsData
      }));
    } catch (error) {
      console.error(`Failed to load subsystems for system ${systemId}:`, error);
    }
  };

  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault();

    // Валидация
    const newErrors = {};
    if (!formData.title.trim()) newErrors.title = 'Название обязательно';
    if (!formData.implementationDate) newErrors.implementationDate = 'Дата реализации обязательна';
    if (!formData.urgency) newErrors.urgency = 'Срочность обязательна';

    // Проверка выбранных систем и подсистем
    if (formData.affectedSystems.length === 0) {
      newErrors.affectedSystems = 'Необходимо выбрать хотя бы одну систему';
    } else {
      formData.affectedSystems.forEach((system, index) => {
        if (!system.systemId) {
          newErrors[`system_${index}`] = 'Система обязательна';
        }
        if (!system.affectedSubsystems || system.affectedSubsystems.length === 0) {
          newErrors[`subsystems_${index}`] = 'Необходимо выбрать хотя бы одну подсистему';
        } else {
          system.affectedSubsystems.forEach((subsystem, subIndex) => {
            if (!subsystem.subsystemId) {
              newErrors[`subsystem_${index}_${subIndex}`] = 'Подсистема обязательна';
            }
            if (!subsystem.executor) {
              newErrors[`executor_${index}_${subIndex}`] = 'Исполнитель обязателен';
            }
          });
        }
      });
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    // Преобразование данных для API
    const apiData = {
      title: formData.title,
      description: formData.description || null,
      implementationDate: formData.implementationDate + ':00.000Z',
      urgency: formData.urgency,
      affectedSystems: formData.affectedSystems.map(system => ({
        systemId: parseInt(system.systemId),
        affectedSubsystems: system.affectedSubsystems.map(subsystem => ({
          subsystemId: parseInt(subsystem.subsystemId),
          executorId: subsystem.executor ? parseInt(subsystem.executor.id) : null
        }))
      })),
      attachmentIds: []
    };

    onSubmit(apiData);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    // Очистка ошибки при изменении поля
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  // Добавление новой системы
  const addSystem = () => {
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

  // Удаление системы
  const removeSystem = (index) => {
    setFormData(prev => ({
      ...prev,
      affectedSystems: prev.affectedSystems.filter((_, i) => i !== index)
    }));
  };

  // Изменение выбранной системы
  const handleSystemChange = (index, systemId) => {
    setFormData(prev => {
      const newAffectedSystems = [...prev.affectedSystems];
      newAffectedSystems[index] = {
        ...newAffectedSystems[index],
        systemId,
        affectedSubsystems: [] // Сбрасываем подсистемы при смене системы
      };
      return { ...prev, affectedSystems: newAffectedSystems };
    });

    // Загружаем подсистемы для выбранной системы
    if (systemId) {
      loadSubsystems(systemId);
    }

    // Очистка ошибок
    if (errors[`system_${index}`]) {
      setErrors(prev => ({ ...prev, [`system_${index}`]: '' }));
    }
    if (errors[`subsystems_${index}`]) {
      setErrors(prev => ({ ...prev, [`subsystems_${index}`]: '' }));
    }
  };

  // Добавление подсистемы к системе
  const addSubsystem = (systemIndex) => {
    setFormData(prev => {
      const newAffectedSystems = [...prev.affectedSystems];
      newAffectedSystems[systemIndex] = {
        ...newAffectedSystems[systemIndex],
        affectedSubsystems: [
          ...(newAffectedSystems[systemIndex].affectedSubsystems || []),
          { subsystemId: '', executor: null }
        ]
      };
      return { ...prev, affectedSystems: newAffectedSystems };
    });
  };

  // Удаление подсистемы
  const removeSubsystem = (systemIndex, subsystemIndex) => {
    setFormData(prev => {
      const newAffectedSystems = [...prev.affectedSystems];
      newAffectedSystems[systemIndex] = {
        ...newAffectedSystems[systemIndex],
        affectedSubsystems: newAffectedSystems[systemIndex].affectedSubsystems.filter((_, i) => i !== subsystemIndex)
      };
      return { ...prev, affectedSystems: newAffectedSystems };
    });
  };

  // Изменение подсистемы
  const handleSubsystemChange = (systemIndex, subsystemIndex, subsystemId) => {
    setFormData(prev => {
      const newAffectedSystems = [...prev.affectedSystems];
      newAffectedSystems[systemIndex].affectedSubsystems[subsystemIndex] = {
        ...newAffectedSystems[systemIndex].affectedSubsystems[subsystemIndex],
        subsystemId
      };
      return { ...prev, affectedSystems: newAffectedSystems };
    });

    // Очистка ошибок
    if (errors[`subsystem_${systemIndex}_${subsystemIndex}`]) {
      setErrors(prev => ({ ...prev, [`subsystem_${systemIndex}_${subsystemIndex}`]: '' }));
    }
  };

  // Изменение исполнителя
  const handleExecutorChange = (systemIndex, subsystemIndex, executor) => {
    setFormData(prev => {
      const newAffectedSystems = [...prev.affectedSystems];
      newAffectedSystems[systemIndex].affectedSubsystems[subsystemIndex] = {
        ...newAffectedSystems[systemIndex].affectedSubsystems[subsystemIndex],
        executor
      };
      return { ...prev, affectedSystems: newAffectedSystems };
    });

    // Очистка ошибок
    if (errors[`executor_${systemIndex}_${subsystemIndex}`]) {
      setErrors(prev => ({ ...prev, [`executor_${systemIndex}_${subsystemIndex}`]: '' }));
    }
  };

  return (
    <div className={`modal ${isOpen ? 'active' : ''}`}>
      <div className="modal-content create-rfc-modal">
        <div className="modal-header">
          <h2>Создать новый RFC</h2>
          <button className="close" onClick={onClose}>
            ×
          </button>
        </div>
        <div className="modal-body">
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Название RFC *</label>
              <input
                type="text"
                name="title"
                value={formData.title}
                onChange={handleChange}
                placeholder="Введите название RFC"
                className={errors.title ? 'error' : ''}
                required
              />
              {errors.title && <div className="error-message">{errors.title}</div>}
            </div>

            <div className="form-group">
              <label>Описание</label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                placeholder="Опишите предлагаемые изменения"
                rows="4"
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Дата реализации *</label>
                <input
                  type="datetime-local"
                  name="implementationDate"
                  value={formData.implementationDate}
                  onChange={handleChange}
                  className={errors.implementationDate ? 'error' : ''}
                  required
                />
                {errors.implementationDate && <div className="error-message">{errors.implementationDate}</div>}
              </div>

              <div className="form-group">
                <label>Срочность *</label>
                <select
                  name="urgency"
                  value={formData.urgency}
                  onChange={handleChange}
                  className={errors.urgency ? 'error' : ''}
                  required
                >
                  <option value="PLANNED">Плановое</option>
                  <option value="URGENT">Срочное</option>
                  <option value="EMERGENCY">Безотлагательное</option>
                </select>
                {errors.urgency && <div className="error-message">{errors.urgency}</div>}
              </div>
            </div>

            <div className="form-section">
              <div className="form-section-header">
                <h3>Затронутые системы</h3>
                <button type="button" onClick={addSystem} className="btn btn-secondary btn-sm">
                  + Добавить систему
                </button>
              </div>

              {systemsError && (
                <div className="error-message" style={{ marginBottom: '15px' }}>
                  {systemsError}
                  <button
                    type="button"
                    onClick={loadSystemsAndUsers}
                    className="btn btn-secondary btn-sm"
                    style={{ marginLeft: '10px' }}
                  >
                    Повторить
                  </button>
                </div>
              )}

              {errors.affectedSystems && (
                <div className="error-message" style={{ marginBottom: '15px' }}>
                  {errors.affectedSystems}
                </div>
              )}

              {formData.affectedSystems.map((system, systemIndex) => (
                <div key={systemIndex} className="system-block">
                  <div className="system-header">
                    <h4>Система #{systemIndex + 1}</h4>
                    {formData.affectedSystems.length > 1 && (
                      <button
                        type="button"
                        onClick={() => removeSystem(systemIndex)}
                        className="btn btn-danger btn-sm"
                      >
                        Удалить
                      </button>
                    )}
                  </div>

                  <div className="form-group">
                    <label>Система *</label>
                    <select
                      value={system.systemId || ''}
                      onChange={(e) => handleSystemChange(systemIndex, e.target.value)}
                      className={errors[`system_${systemIndex}`] ? 'error' : ''}
                      required
                      disabled={systemsLoading}
                    >
                      <option value="">{systemsLoading ? 'Загрузка систем...' : 'Выберите систему'}</option>
                      {systems.map(sys => (
                        <option key={sys.id} value={sys.id}>
                          {sys.name} (ID: {sys.id})
                        </option>
                      ))}
                    </select>
                    {errors[`system_${systemIndex}`] && (
                      <div className="error-message">{errors[`system_${systemIndex}`]}</div>
                    )}
                  </div>

                  {system.systemId && (
                    <div className="subsystems-block">
                      <div className="subsystems-header">
                        <label>Подсистемы *</label>
                        <button
                          type="button"
                          onClick={() => addSubsystem(systemIndex)}
                          className="btn btn-secondary btn-sm"
                        >
                          + Добавить подсистему
                        </button>
                      </div>

                      {errors[`subsystems_${systemIndex}`] && (
                        <div className="error-message">{errors[`subsystems_${systemIndex}`]}</div>
                      )}

                      {system.affectedSubsystems.map((subsystem, subsystemIndex) => (
                        <div key={subsystemIndex} className="subsystem-row">
                          <div className="form-row">
                            <div className="form-group">
                              <label>Подсистема *</label>
                              <select
                                value={subsystem.subsystemId || ''}
                                onChange={(e) => handleSubsystemChange(systemIndex, subsystemIndex, e.target.value)}
                                className={errors[`subsystem_${systemIndex}_${subsystemIndex}`] ? 'error' : ''}
                                required
                              >
                                <option value="">Выберите подсистему</option>
                                {subsystems[system.systemId]?.map(sub => (
                                  <option key={sub.id} value={sub.id}>
                                    {sub.name} (ID: {sub.id})
                                  </option>
                                ))}
                              </select>
                              {errors[`subsystem_${systemIndex}_${subsystemIndex}`] && (
                                <div className="error-message">
                                  {errors[`subsystem_${systemIndex}_${subsystemIndex}`]}
                                </div>
                              )}
                            </div>

                            <div className="form-group">
                              <label>Исполнитель *</label>
                              <SingleUserSearchSelect
                                value={subsystem.executor}
                                onChange={(user) => handleExecutorChange(systemIndex, subsystemIndex, user)}
                                error={errors[`executor_${systemIndex}_${subsystemIndex}`]}
                                placeholder="Поиск исполнителя по имени..."
                                disabled={!subsystem.subsystemId}
                              />
                            </div>

                            <div className="form-group" style={{ flex: '0 0 auto', alignSelf: 'flex-end' }}>
                              <button
                                type="button"
                                onClick={() => removeSubsystem(systemIndex, subsystemIndex)}
                                className="btn btn-danger btn-sm"
                                disabled={system.affectedSubsystems.length === 1}
                              >
                                Удалить
                              </button>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              ))}
            </div>

            <div className="modal-footer">
              <button type="submit" className="btn btn-primary" disabled={loading || systemsLoading}>
                {loading ? 'Загрузка...' : 'Создать RFC'}
              </button>
              <button type="button" onClick={onClose} className="btn btn-secondary">
                Отмена
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default CreateRfcModal;