import React, { useState, useEffect } from 'react';
import './CreateRfcModal.css';

const CreateRfcModal = ({ isOpen, onClose, onSubmit }) => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    implementationDate: '',
    urgency: 'PLANNED',
    affectedSystems: [{
      systemId: '',
      affectedSubsystems: [{
        subsystemId: '',
        executorId: ''
      }]
    }]
  });

  const [errors, setErrors] = useState({});

  // Сброс формы при открытии/закрытии
  useEffect(() => {
    if (isOpen) {
      setFormData({
        title: '',
        description: '',
        implementationDate: '',
        urgency: 'PLANNED',
        affectedSystems: [{
          systemId: '',
          affectedSubsystems: [{
            subsystemId: '',
            executorId: ''
          }]
        }]
      });
      setErrors({});
    }
  }, [isOpen]);

  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault();

    // Валидация
    const newErrors = {};
    if (!formData.title.trim()) newErrors.title = 'Название обязательно';
    if (!formData.implementationDate) newErrors.implementationDate = 'Дата реализации обязательна';
    if (!formData.urgency) newErrors.urgency = 'Срочность обязательна';
    if (!formData.affectedSystems[0]?.systemId) newErrors.systemId = 'Система обязательна';
    if (!formData.affectedSystems[0]?.affectedSubsystems[0]?.subsystemId) newErrors.subsystemId = 'Подсистема обязательна';
    if (!formData.affectedSystems[0]?.affectedSubsystems[0]?.executorId) newErrors.executorId = 'Исполнитель обязателен';

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    // Преобразование данных для API
    const apiData = {
      title: formData.title,
      description: formData.description || null,
      implementationDate: formData.implementationDate + ':00.000Z', // Формат для бэкенда
      urgency: formData.urgency,
      affectedSystems: formData.affectedSystems.map(system => ({
        systemId: parseInt(system.systemId),
        affectedSubsystems: system.affectedSubsystems.map(subsystem => ({
          subsystemId: parseInt(subsystem.subsystemId),
          executorId: parseInt(subsystem.executorId)
        }))
      })),
      attachmentIds: [] // Пока пустой массив, можно добавить загрузку файлов позже
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

  const handleSystemChange = (e) => {
    const value = e.target.value;
    setFormData(prev => ({
      ...prev,
      affectedSystems: [{
        ...prev.affectedSystems[0],
        systemId: value
      }]
    }));

    if (errors.systemId) {
      setErrors(prev => ({
        ...prev,
        systemId: ''
      }));
    }
  };

  const handleSubsystemChange = (e) => {
    const value = e.target.value;
    setFormData(prev => ({
      ...prev,
      affectedSystems: [{
        ...prev.affectedSystems[0],
        affectedSubsystems: [{
          ...prev.affectedSystems[0].affectedSubsystems[0],
          subsystemId: value
        }]
      }]
    }));

    if (errors.subsystemId) {
      setErrors(prev => ({
        ...prev,
        subsystemId: ''
      }));
    }
  };

  const handleExecutorChange = (e) => {
    const value = e.target.value;
    setFormData(prev => ({
      ...prev,
      affectedSystems: [{
        ...prev.affectedSystems[0],
        affectedSubsystems: [{
          ...prev.affectedSystems[0].affectedSubsystems[0],
          executorId: value
        }]
      }]
    }));

    if (errors.executorId) {
      setErrors(prev => ({
        ...prev,
        executorId: ''
      }));
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content create-rfc-modal">
        <button className="modal-close" onClick={onClose}>
          ×
        </button>
        <h2>Создать новый RFC</h2>
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
            <h3>Затронутые системы</h3>

            <div className="form-group">
              <label>ID системы *</label>
              <input
                type="number"
                value={formData.affectedSystems[0]?.systemId || ''}
                onChange={handleSystemChange}
                placeholder="Введите ID системы"
                className={errors.systemId ? 'error' : ''}
                required
              />
              {errors.systemId && <div className="error-message">{errors.systemId}</div>}
              <small>Пример: 1, 2, 3</small>
            </div>

            <div className="form-group">
              <label>ID подсистемы *</label>
              <input
                type="number"
                value={formData.affectedSystems[0]?.affectedSubsystems[0]?.subsystemId || ''}
                onChange={handleSubsystemChange}
                placeholder="Введите ID подсистемы"
                className={errors.subsystemId ? 'error' : ''}
                required
              />
              {errors.subsystemId && <div className="error-message">{errors.subsystemId}</div>}
              <small>Пример: 1, 2, 3</small>
            </div>

            <div className="form-group">
              <label>ID исполнителя *</label>
              <input
                type="number"
                value={formData.affectedSystems[0]?.affectedSubsystems[0]?.executorId || ''}
                onChange={handleExecutorChange}
                placeholder="Введите ID исполнителя"
                className={errors.executorId ? 'error' : ''}
                required
              />
              {errors.executorId && <div className="error-message">{errors.executorId}</div>}
              <small>Пример: 1, 2, 3</small>
            </div>
          </div>

          <div className="form-actions">
            <button type="submit" className="btn-primary">
              Создать RFC
            </button>
            <button type="button" onClick={onClose} className="btn-secondary">
              Отмена
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateRfcModal;