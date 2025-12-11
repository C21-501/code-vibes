/**
 * SystemFormModal Component
 * Modal for creating and editing systems
 */
import { useState, useEffect } from 'react';
import '../../../shared/components/Modal.css';
import './SystemFormModal.css';

export default function SystemFormModal({ system, isOpen, onClose, onSave }) {
  const isEditMode = !!system;
  
  const [formData, setFormData] = useState({
    name: '',
    description: ''
  });
  
  const [errors, setErrors] = useState({});

  // Initialize form data when system prop changes
  useEffect(() => {
    if (system) {
      setFormData({
        name: system.name || '',
        description: system.description || ''
      });
    } else {
      setFormData({
        name: '',
        description: ''
      });
    }
    setErrors({});
  }, [system, isOpen]);

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

  const validateForm = () => {
    const newErrors = {};
    
    // Name validation (minLength: 1, maxLength: 255)
    if (!formData.name.trim()) {
      newErrors.name = 'Название системы обязательно';
    } else if (formData.name.length > 255) {
      newErrors.name = 'Название должно быть не более 255 символов';
    }
    
    // Description validation (maxLength: 1000)
    if (formData.description && formData.description.length > 1000) {
      newErrors.description = 'Описание должно быть не более 1000 символов';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    // Prepare data according to API spec (SystemRequest)
    const systemData = {
      name: formData.name.trim(),
      description: formData.description.trim() || null
    };
    
    onSave(systemData);
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
          <h2>{isEditMode ? 'Редактировать систему' : 'Создать систему'}</h2>
          <button className="close" onClick={onClose}>&times;</button>
        </div>
        <div className="modal-body">
          <form id="systemForm" onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="name">Название системы *</label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                placeholder="Введите название системы"
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
                placeholder="Опишите назначение системы (необязательно)"
                className={errors.description ? 'error' : ''}
                rows="4"
              />
              {errors.description && <div className="error-message">{errors.description}</div>}
              <small>До 1000 символов. Осталось: {1000 - formData.description.length}</small>
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

