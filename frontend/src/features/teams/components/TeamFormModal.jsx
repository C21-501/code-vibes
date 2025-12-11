/**
 * TeamFormModal Component
 * Modal for creating and editing teams
 */
import { useState, useEffect } from 'react';
import UserSearchSelect from './UserSearchSelect';
import '../../../shared/components/Modal.css';
import './TeamFormModal.css';

export default function TeamFormModal({ team, isOpen, onClose, onSave }) {
  const isEditMode = !!team;
  
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    selectedMembers: []
  });
  
  const [errors, setErrors] = useState({});

  // Initialize form data when team prop changes
  useEffect(() => {
    if (team) {
      setFormData({
        name: team.name || '',
        description: team.description || '',
        selectedMembers: team.members || []
      });
    } else {
      setFormData({
        name: '',
        description: '',
        selectedMembers: []
      });
    }
    setErrors({});
  }, [team, isOpen]);

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

  const handleMembersChange = (newMembers) => {
    setFormData(prev => ({
      ...prev,
      selectedMembers: newMembers
    }));
    // Clear memberIds error
    if (errors.memberIds) {
      setErrors(prev => ({
        ...prev,
        memberIds: ''
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    // Name validation (minLength: 1, maxLength: 255)
    if (!formData.name.trim()) {
      newErrors.name = 'Название команды обязательно';
    } else if (formData.name.length > 255) {
      newErrors.name = 'Название должно быть не более 255 символов';
    }
    
    // Description validation (maxLength: 1000)
    if (formData.description && formData.description.length > 1000) {
      newErrors.description = 'Описание должно быть не более 1000 символов';
    }
    
    // MemberIds validation (minItems: 1)
    if (!formData.selectedMembers || formData.selectedMembers.length === 0) {
      newErrors.memberIds = 'Необходимо выбрать хотя бы одного участника';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    // Prepare data according to API spec (TeamRequest)
    const teamData = {
      name: formData.name.trim(),
      description: formData.description.trim() || null,
      memberIds: formData.selectedMembers.map(m => m.id)
    };
    
    onSave(teamData);
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
          <h2>{isEditMode ? 'Редактировать команду' : 'Создать команду'}</h2>
          <button className="close" onClick={onClose}>&times;</button>
        </div>
        <div className="modal-body">
          <form id="teamForm" onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="name">Название команды *</label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                placeholder="Введите название команды"
                className={errors.name ? 'error' : ''}
              />
              {errors.name && <div className="error-message">{errors.name}</div>}
              <small>От 1 до 255 символов</small>
            </div>

            <div className="form-group">
              <label htmlFor="description">Описание</label>
              <textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleChange}
                placeholder="Опишите назначение команды (необязательно)"
                className={errors.description ? 'error' : ''}
              />
              {errors.description && <div className="error-message">{errors.description}</div>}
              <small>До 1000 символов</small>
            </div>

            <div className="form-group">
              <label>Участники команды *</label>
              <UserSearchSelect
                selectedMembers={formData.selectedMembers}
                onChange={handleMembersChange}
                error={errors.memberIds}
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

