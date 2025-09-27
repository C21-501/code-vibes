# Keycloak Integration Setup

## Переменные окружения

Создайте файл `.env.local` в корне frontend папки со следующими переменными:

```bash
# Keycloak Configuration
VITE_KEYCLOAK_URL=http://localhost:8080
VITE_KEYCLOAK_REALM=cab-realm
VITE_KEYCLOAK_CLIENT_ID=cab-frontend

# API Configuration
VITE_API_BASE_URL=http://localhost:8081
```

## Компоненты аутентификации

### AuthProvider
- Инициализирует Keycloak при загрузке приложения
- Предоставляет контекст аутентификации
- Автоматически обновляет токены

### useAuth Hook
Предоставляет:
- `isAuthenticated` - статус аутентификации
- `isLoading` - состояние загрузки
- `token` - текущий токен доступа
- `user` - профиль пользователя
- `login()` - метод входа
- `logout()` - метод выхода
- `keycloakInstance` - экземпляр Keycloak

### ProtectedRoute
Компонент для защиты маршрутов:
- Автоматически перенаправляет неаутентифицированных пользователей
- Показывает экран входа или кастомный fallback

### Axios Interceptor
- Автоматически добавляет Bearer token в заголовки запросов к `/api/**`
- Обновляет токены при истечении
- Обрабатывает ошибки 401

## Использование

```tsx
import { useAuth, ProtectedRoute } from './auth';
import { apiClient } from './api';

function MyComponent() {
  const { isAuthenticated, user, login, logout } = useAuth();
  
  const handleApiCall = async () => {
    const response = await apiClient.get('/api/data');
    // Токен будет добавлен автоматически
  };
  
  return (
    <ProtectedRoute>
      <div>Защищенный контент</div>
    </ProtectedRoute>
  );
}
```
