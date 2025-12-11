# Аутентификация в RFC Management System

## Keycloak

Система использует Keycloak для аутентификации пользователей.

### Конфигурация

- **Keycloak URL**: http://localhost:8081
- **Realm**: `cab-realm`
- **Backend Client**: `cab-backend`
- **Frontend Client**: `cab-frontend`

### Тестовые пользователи

#### 1. Пользователь приложения

- **Username**: `admin`
- **Password**: `admin`
- **Email**: `admin@example.com`
- **Роль**: `ADMIN`
- **Realm**: `cab-realm`

Этот пользователь используется для входа в приложение через форму логина.

#### 2. Администратор Keycloak

- **Username**: `admin`
- **Password**: `admin`
- **Realm**: `master`
- **URL**: http://localhost:8081/admin

Этот пользователь используется только для администрирования Keycloak.

### Frontend Login

Откройте http://localhost:5173 и войдите используя:
- Username: `admin`
- Password: `admin`

### Роли приложения

- **REQUESTER** - Пользователи, которые могут заказывать такси
- **EXECUTOR** - Водители такси
- **CAB_MANAGER** - Менеджеры такси-службы
- **ADMIN** - Администраторы системы

### Добавление новых пользователей

Для добавления новых пользователей:

1. Войдите в Keycloak Admin: http://localhost:8081/admin
2. Выберите realm `cab-realm`
3. Перейдите в Users → Add user
4. Заполните данные пользователя
5. Установите пароль в вкладке Credentials
6. Назначьте роль в вкладке Role mappings

### Troubleshooting

#### 401 Unauthorized при логине

Проверьте:
1. Backend успешно запустился и подключился к Keycloak
2. Используется correct realm (`cab-realm`, а не `master`)
3. URL Keycloak правильный: `http://keycloak:8080` (внутри Docker)
4. Пользователь существует в realm `cab-realm`

#### 502 Bad Gateway

Backend еще не полностью запустился. Подождите 1-2 минуты после запуска контейнеров.

### Конфигурация Backend

Backend настроен через `application-docker.yaml`:

```yaml
keycloak:
  auth-server-url: http://keycloak:8080
  realm: cab-realm
  resource: cab-backend
  credentials:
    secret: backend-secret-123
```

### Конфигурация Docker Compose

Переменные окружения для backend:

```yaml
KEYCLOAK_AUTH_SERVER_URL: http://keycloak:8080
KEYCLOAK_REALM: cab-realm
KEYCLOAK_RESOURCE: cab-backend
KEYCLOAK_CREDENTIALS_SECRET: backend-secret-123
```

