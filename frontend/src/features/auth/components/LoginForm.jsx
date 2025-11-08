import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../../../features/auth/api/authApi'; // Импортируем authApi
import Toast from '../../../shared/components/Toast';
import { getAndClearReturnUrl, clearAuthTokens } from '../../../utils/authContext';
import { isCurrentTokenExpired } from '../../../utils/jwtUtils';
import apiClient from '../../../utils/apiClient';
import './LoginForm.css';

function LoginForm() {
  const navigate = useNavigate();

  // Clean up expired/invalid tokens on mount
  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token && isCurrentTokenExpired()) {
      clearAuthTokens();
    }
  }, []);

  // Form state
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });

  // UI state
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [errors, setErrors] = useState({
    username: '',
    password: '',
    general: ''
  });

  // Toast notification state
  const [toast, setToast] = useState({
    show: false,
    type: '', // 'success' | 'error' | 'info'
    title: '',
    message: ''
  });

  /**
   * Handle input changes
   */
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    // Clear field error on input
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  /**
   * Validate form according to LoginRequest schema
   */
  const validateForm = () => {
    const newErrors = {
      username: '',
      password: '',
      general: ''
    };
    let isValid = true;

    // Validate username
    if (!formData.username || formData.username.trim().length === 0) {
      newErrors.username = 'Имя пользователя обязательно для заполнения';
      isValid = false;
    } else if (formData.username.length > 50) {
      newErrors.username = 'Имя пользователя не должно превышать 50 символов';
      isValid = false;
    }

    // Validate password
    if (!formData.password || formData.password.length === 0) {
      newErrors.password = 'Пароль обязателен для заполнения';
      isValid = false;
    } else if (formData.password.length > 100) {
      newErrors.password = 'Пароль не должен превышать 100 символов';
      isValid = false;
    }

    setErrors(newErrors);
    return isValid;
  };

  /**
   * Show toast notification
   */
  const showToast = (type, title, message) => {
    setToast({
      show: true,
      type,
      title,
      message
    });

    // Auto-hide after 5 seconds
    setTimeout(() => {
      setToast(prev => ({ ...prev, show: false }));
    }, 5000);
  };

  /**
   * Handle form submission using authApi
   */
  const handleSubmit = async (e) => {
    e.preventDefault();

    // Clear previous errors
    setErrors({ username: '', password: '', general: '' });

    // Validate form
    if (!validateForm()) {
      return;
    }

    // Set loading state
    setIsLoading(true);

    try {
      // Prepare request body according to LoginRequest schema
      const loginRequest = {
        username: formData.username.trim(),
        password: formData.password
      };

      // ✅ ИСПОЛЬЗУЕМ authApi ВМЕСТО ПРЯМОГО fetch
      const response = await authApi.login(loginRequest);

      // ✅ Токены уже сохранены в localStorage в authApi.login
      // Показываем успешное уведомление
      showToast('success', 'Успешный вход', 'Вы успешно авторизованы в системе!');

      // Очищаем форму
      setFormData({ username: '', password: '' });

      // Уведомляем API client об успешном входе
      await apiClient.onLoginSuccess();

      // Получаем сохраненный URL для возврата или используем /rfc по умолчанию
      const returnUrl = getAndClearReturnUrl() || '/rfc';

      // Редирект после короткой задержки для показа сообщения
      setTimeout(() => {
        navigate(returnUrl);
      }, 1000);

    } catch (error) {
      console.error('Login error:', error);

      // Обрабатываем различные типы ошибок
      let errorTitle = 'Ошибка входа';
      let errorMessage = 'Произошла ошибка при входе в систему';

      if (error.response) {
        // Сервер ответил с ошибкой
        const status = error.response.status;

        if (status === 400) {
          errorTitle = 'Ошибка валидации';
          errorMessage = 'Проверьте правильность введенных данных';
        } else if (status === 401) {
          errorTitle = 'Ошибка аутентификации';
          errorMessage = 'Неверное имя пользователя или пароль';
          setErrors({
            username: 'Проверьте правильность данных',
            password: 'Проверьте правильность данных',
            general: 'Неверное имя пользователя или пароль'
          });
        } else if (status === 500) {
          errorTitle = 'Ошибка сервера';
          errorMessage = 'Внутренняя ошибка сервера. Попробуйте позже.';
        }
      } else if (error.request) {
        // Запрос был сделан, но ответ не получен
        errorTitle = 'Ошибка подключения';
        errorMessage = 'Не удалось подключиться к серверу. Проверьте ваше интернет-соединение.';
      } else {
        // Что-то пошло не так при настройке запроса
        errorMessage = error.message || 'Произошла непредвиденная ошибка';
      }

      showToast('error', errorTitle, errorMessage);

    } finally {
      setIsLoading(false);
    }
  };

  /**
   * Toggle password visibility
   */
  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  return (
    <>
      <div className="login-container">
        <div className="login-header">
          <h1>RFC Management System</h1>
          <p>Войдите в систему для продолжения работы</p>
        </div>

        <div className="login-body">
          <form onSubmit={handleSubmit}>
            {/* Username field */}
            <div className="form-group">
              <label htmlFor="username">
                Имя пользователя <span className="required">*</span>
              </label>
              <div className="input-wrapper">
                <input
                  type="text"
                  id="username"
                  name="username"
                  value={formData.username}
                  onChange={handleChange}
                  className={errors.username ? 'error' : ''}
                  placeholder="Введите ваше имя пользователя"
                  autoComplete="username"
                  disabled={isLoading}
                  required
                  minLength={1}
                  maxLength={50}
                />
              </div>
              <small>От 1 до 50 символов</small>
              {errors.username && (
                <div className="error-message show">{errors.username}</div>
              )}
            </div>

            {/* Password field */}
            <div className="form-group">
              <label htmlFor="password">
                Пароль <span className="required">*</span>
              </label>
              <div className="input-wrapper password-toggle">
                <input
                  type={showPassword ? 'text' : 'password'}
                  id="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  className={errors.password ? 'error' : ''}
                  placeholder="Введите ваш пароль"
                  autoComplete="current-password"
                  disabled={isLoading}
                  required
                  minLength={1}
                  maxLength={100}
                />
                <button
                  type="button"
                  className="password-toggle-btn"
                  onClick={togglePasswordVisibility}
                  disabled={isLoading}
                >
                  {showPassword ? '🙈' : '👁️'}
                </button>
              </div>
              <small>От 1 до 100 символов</small>
              {errors.password && (
                <div className="error-message show">{errors.password}</div>
              )}
            </div>

            {/* General error message */}
            {errors.general && (
              <div className="error-message show general-error">
                {errors.general}
              </div>
            )}

            {/* Submit button */}
            <button
              type="submit"
              className={`btn-login ${isLoading ? 'loading' : ''}`}
              disabled={isLoading}
            >
              <span className="btn-text">Войти в систему</span>
              <div className="spinner"></div>
            </button>
          </form>
        </div>

        <div className="login-footer">
          RFC Management System v1.0<br />
          © 2025 Все права защищены
        </div>
      </div>

      {/* Toast Notification */}
      <Toast
        show={toast.show}
        type={toast.type}
        title={toast.title}
        message={toast.message}
        onClose={() => setToast(prev => ({ ...prev, show: false }))}
      />
    </>
  );
}

export default LoginForm;