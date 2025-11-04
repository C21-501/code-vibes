import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Toast from '../../../shared/components/Toast';
import './LoginForm.css';

/**
 * LoginForm Component
 * 
 * Implements user authentication form based on OpenAPI specification
 * API Endpoint: POST /user/login
 * 
 * Request: LoginRequest { username: string, password: string }
 * Response: LoginResponse { 
 *   accessToken: string, 
 *   refreshToken?: string, 
 *   expiresIn: number, 
 *   refreshExpiresIn?: number, 
 *   tokenType: string 
 * }
 */
function LoginForm() {
  const navigate = useNavigate();
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
   * username: required, minLength: 1, maxLength: 50
   * password: required, minLength: 1, maxLength: 100
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
   * Handle form submission
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

      // Make API call to POST /user/login
      const response = await fetch('/user/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginRequest)
      });

      // Handle different response statuses according to OpenAPI spec
      if (response.status === 200) {
        // Success: Parse LoginResponse
        const loginResponse = await response.json();
        
        // Store tokens in localStorage
        localStorage.setItem('accessToken', loginResponse.accessToken);
        localStorage.setItem('tokenType', loginResponse.tokenType);
        localStorage.setItem('expiresIn', loginResponse.expiresIn.toString());
        
        // Calculate token expiration timestamp
        const expirationTime = new Date().getTime() + (loginResponse.expiresIn * 1000);
        localStorage.setItem('tokenExpiration', expirationTime.toString());
        
        // Store refresh token if provided
        if (loginResponse.refreshToken) {
          localStorage.setItem('refreshToken', loginResponse.refreshToken);
        }
        
        // Store refresh token expiration if provided
        if (loginResponse.refreshExpiresIn) {
          const refreshExpirationTime = new Date().getTime() + (loginResponse.refreshExpiresIn * 1000);
          localStorage.setItem('refreshTokenExpiration', refreshExpirationTime.toString());
        }

        // Show success notification
        showToast('success', 'Успешный вход', 'Вы успешно авторизованы в системе!');
        
        // Clear form
        setFormData({ username: '', password: '' });
        
        // Redirect to users page after short delay
        setTimeout(() => {
          navigate('/users');
        }, 1000); // Delay to show success message

      } else if (response.status === 400) {
        // Bad Request: Validation errors
        const errorResponse = await response.json();
        const errorMessage = errorResponse.errors?.map(e => e.message).join(', ') 
          || 'Ошибка валидации данных';
        
        setErrors(prev => ({
          ...prev,
          general: errorMessage
        }));
        showToast('error', 'Ошибка валидации', errorMessage);

      } else if (response.status === 401) {
        // Unauthorized: Invalid credentials
        const errorResponse = await response.json();
        const errorMessage = errorResponse.errors?.[0]?.message 
          || 'Неверное имя пользователя или пароль';
        
        setErrors({
          username: 'Проверьте правильность данных',
          password: 'Проверьте правильность данных',
          general: errorMessage
        });
        showToast('error', 'Ошибка аутентификации', errorMessage);

      } else if (response.status === 500) {
        // Internal Server Error
        showToast('error', 'Ошибка сервера', 'Произошла внутренняя ошибка сервера. Попробуйте позже.');
        
      } else {
        // Other errors
        const errorResponse = await response.json();
        const errorMessage = errorResponse.errors?.[0]?.message 
          || 'Произошла ошибка при входе в систему';
        showToast('error', 'Ошибка', errorMessage);
      }

    } catch (error) {
      console.error('Login error:', error);
      showToast('error', 'Ошибка подключения', 'Не удалось подключиться к серверу. Проверьте ваше интернет-соединение.');
      
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
          <div className="login-logo">🔐</div>
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
                <span className="input-icon">👤</span>
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
                <span className="input-icon">🔒</span>
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

