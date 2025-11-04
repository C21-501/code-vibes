/**
 * UserFormModal Component
 * Modal for creating and editing users
 */
import { useState, useEffect } from 'react';
import './Modal.css';

export default function UserFormModal({ user, isOpen, onClose, onSave }) {
  const isEditMode = !!user;
  
  const [formData, setFormData] = useState({
    username: '',
    firstName: '',
    lastName: '',
    role: '',
    password: ''
  });
  
  const [showPassword, setShowPassword] = useState(false);
  const [errors, setErrors] = useState({});

  // Initialize form data when user prop changes
  useEffect(() => {
    if (user) {
      setFormData({
        username: user.username || '',
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        role: user.role || '',
        password: ''
      });
    } else {
      setFormData({
        username: '',
        firstName: '',
        lastName: '',
        role: '',
        password: ''
      });
    }
    setErrors({});
  }, [user, isOpen]);

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
    
    // Username validation (only for create mode, minLength: 3, maxLength: 50, pattern: ^[a-zA-Z0-9_-]+$)
    if (!isEditMode) {
      if (!formData.username.trim()) {
        newErrors.username = 'Username обязателен';
      } else if (formData.username.length < 3) {
        newErrors.username = 'Username должен быть не менее 3 символов';
      } else if (formData.username.length > 50) {
        newErrors.username = 'Username должен быть не более 50 символов';
      } else if (!/^[a-zA-Z0-9_-]+$/.test(formData.username)) {
        newErrors.username = 'Username может содержать только буквы, цифры, подчеркивание и дефис';
      }
    }
    
    // FirstName validation (minLength: 1, maxLength: 100)
    if (!formData.firstName.trim()) {
      newErrors.firstName = 'Имя обязательно';
    } else if (formData.firstName.length > 100) {
      newErrors.firstName = 'Имя должно быть не более 100 символов';
    }
    
    // LastName validation (minLength: 1, maxLength: 100)
    if (!formData.lastName.trim()) {
      newErrors.lastName = 'Фамилия обязательна';
    } else if (formData.lastName.length > 100) {
      newErrors.lastName = 'Фамилия должна быть не более 100 символов';
    }
    
    // Role validation
    if (!formData.role) {
      newErrors.role = 'Роль обязательна';
    }
    
    // Password validation (only for create mode)
    if (!isEditMode) {
      if (!formData.password) {
        newErrors.password = 'Пароль обязателен';
      } else if (formData.password.length < 8) {
        newErrors.password = 'Пароль должен быть не менее 8 символов';
      } else if (formData.password.length > 100) {
        newErrors.password = 'Пароль должен быть не более 100 символов';
      }
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    // Prepare data according to API spec
    const userData = {
      firstName: formData.firstName.trim(),
      lastName: formData.lastName.trim(),
      role: formData.role
    };
    
    // Add username and password only for create mode
    if (!isEditMode) {
      userData.username = formData.username.trim();
      userData.password = formData.password;
    }
    
    onSave(userData);
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
          <h2>{isEditMode ? 'Редактировать пользователя' : 'Создать пользователя'}</h2>
          <button className="close" onClick={onClose}>&times;</button>
        </div>
        <div className="modal-body">
          <form id="userForm" onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="username">Username *</label>
              <input
                type="text"
                id="username"
                name="username"
                value={formData.username}
                onChange={handleChange}
                placeholder="Введите username (только буквы, цифры, _, -)"
                className={errors.username ? 'error' : ''}
                disabled={isEditMode}
                readOnly={isEditMode}
              />
              {errors.username && <div className="error-message">{errors.username}</div>}
              <small>
                {isEditMode 
                  ? 'Username нельзя изменить после создания' 
                  : 'От 3 до 50 символов. Только буквы, цифры, подчеркивание и дефис'}
              </small>
            </div>

            <div className="form-group">
              <label htmlFor="firstName">Имя *</label>
              <input
                type="text"
                id="firstName"
                name="firstName"
                value={formData.firstName}
                onChange={handleChange}
                placeholder="Введите имя"
                className={errors.firstName ? 'error' : ''}
              />
              {errors.firstName && <div className="error-message">{errors.firstName}</div>}
              <small>От 1 до 100 символов</small>
            </div>

            <div className="form-group">
              <label htmlFor="lastName">Фамилия *</label>
              <input
                type="text"
                id="lastName"
                name="lastName"
                value={formData.lastName}
                onChange={handleChange}
                placeholder="Введите фамилию"
                className={errors.lastName ? 'error' : ''}
              />
              {errors.lastName && <div className="error-message">{errors.lastName}</div>}
              <small>От 1 до 100 символов</small>
            </div>

            <div className="form-group">
              <label htmlFor="role">Роль *</label>
              <select
                id="role"
                name="role"
                value={formData.role}
                onChange={handleChange}
                className={errors.role ? 'error' : ''}
              >
                <option value="">Выберите роль</option>
                <option value="REQUESTER">Инициатор</option>
                <option value="EXECUTOR">Исполнитель</option>
                <option value="CAB_MANAGER">CAB Менеджер</option>
                <option value="ADMIN">Администратор</option>
              </select>
              {errors.role && <div className="error-message">{errors.role}</div>}
              <small>Выберите роль пользователя в системе</small>
            </div>

            {!isEditMode && (
              <div className="form-group">
                <label htmlFor="password">Пароль *</label>
                <div className="password-toggle">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    id="password"
                    name="password"
                    value={formData.password}
                    onChange={handleChange}
                    placeholder="Введите пароль"
                    className={errors.password ? 'error' : ''}
                  />
                  <button
                    type="button"
                    className="password-toggle-btn"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? '🙈' : '👁️'}
                  </button>
                </div>
                {errors.password && <div className="error-message">{errors.password}</div>}
                <small>От 8 до 100 символов (обязательно при создании)</small>
              </div>
            )}
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

