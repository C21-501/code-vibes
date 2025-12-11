/**
 * SubsystemFormModal Component
 * Modal for creating and editing subsystems
 */
import { useState, useEffect } from 'react';
import TeamSearchSelect from './TeamSearchSelect';
import '../../../shared/components/Modal.css';
import './SubsystemFormModal.css';

export default function SubsystemFormModal({ subsystem, systemId, isOpen, onClose, onSave }) {
  const isEditMode = !!subsystem;
  
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    systemId: systemId || '',
    teamId: ''
  });
  
  const [errors, setErrors] = useState({});

  // Initialize form data when subsystem prop changes
  useEffect(() => {
    if (subsystem) {
      setFormData({
        name: subsystem.name || '',
        description: subsystem.description || '',
        systemId: subsystem.systemId || systemId,
        teamId: subsystem.teamId || ''
      });
    } else {
      setFormData({
        name: '',
        description: '',
        systemId: systemId || '',
        teamId: ''
      });
    }
    setErrors({});
  }, [subsystem, systemId, isOpen]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear error for this field
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const handleTeamChange = (teamId) => {
    setFormData(prev => ({
      ...prev,
      teamId: teamId
    }));
    // Clear error for teamId field
    if (errors.teamId) {
      setErrors(prev => ({
        ...prev,
        teamId: ''
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    // Name validation (minLength: 1, maxLength: 255)
    if (!formData.name.trim()) {
      newErrors.name = 'Название подсистемы обязательно';
    } else if (formData.name.length > 255) {
      newErrors.name = 'Название должно быть не более 255 символов';
    }
    
    // Description validation (maxLength: 1000)
    if (formData.description && formData.description.length > 1000) {
      newErrors.description = 'Описание должно быть не более 1000 символов';
    }
    
    // TeamId validation (required)
    if (!formData.teamId) {
      newErrors.teamId = 'Необходимо выбрать команду';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    // Prepare data according to API spec (SubsystemRequest)
    const subsystemData = {
      name: formData.name.trim(),
      description: formData.description.trim() || null,
      systemId: parseInt(formData.systemId, 10),
      teamId: parseInt(formData.teamId, 10)
    };
    
    onSave(subsystemData);
  };

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  return (
    <div className="modal active" onClick={handleBackdropClick}>
      <div className="modal-content">
        <div className="modal-header">
          <h2>{isEditMode ? 'Редактировать подсистему' : 'Создать подсистему'}</h2>
          <button className="close" onClick={onClose}>&times;</button>
        </div>
        <div className="modal-body">
          <form id="subsystemForm" onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="name">Название подсистемы *</label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                placeholder="Введите название подсистемы"
                className={errors.name ? 'error' : ''}
              />
              {errors.name && <div className="error-message">{errors.name}</div>}
              <small>От 1 до 255 символов. Осталось: {255 - formData.name.length}</small>
            </div>

            <div className="form-group">
              <label htmlFor="description">Описание</label>
              <textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleChange}
                placeholder="Опишите назначение подсистемы (необязательно)"
                className={errors.description ? 'error' : ''}
                rows="4"
              />
              {errors.description && <div className="error-message">{errors.description}</div>}
              <small>До 1000 символов. Осталось: {1000 - formData.description.length}</small>
            </div>

            <div className="form-group">
              <label htmlFor="teamId">Ответственная команда *</label>
              <TeamSearchSelect
                selectedTeamId={formData.teamId}
                onChange={handleTeamChange}
                error={errors.teamId}
              />
            </div>
          </form>
        </div>
        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose}>
            Отмена
          </button>
          <button className="btn btn-primary" onClick={handleSubmit}>
            Сохранить
          </button>
        </div>
      </div>
    </div>
  );
}

