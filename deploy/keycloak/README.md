# Keycloak Configuration для CAB приложения

Эта директория содержит конфигурационные файлы для автоматической настройки Keycloak в рамках CAB приложения.

## Конфигурация Realm

### Основные настройки

- **Название Realm**: `cab-realm`
- **Display Name**: `CAB Application Realm`
- **Статус**: enabled
- **SSL**: required external
- **Защита от брутфорса**: включена

### Временные настройки токенов

- **Access Token Lifespan**: 5 минут (300 секунд)
- **SSO Session Max Lifespan**: 10 часов (36000 секунд)
- **SSO Session Idle Timeout**: 30 минут (1800 секунд)

## Ролевая модель системы

### Realm Roles

- **REQUESTER** - Пользователи, которые могут заказывать такси
- **EXECUTOR** - Водители такси
- **CAB_MANAGER** - Менеджеры такси-службы
- **ADMIN** - Администраторы системы

## Клиенты (Clients)

### Frontend Client (`cab-frontend`)

- **Тип**: Public Client
- **Назначение**: Веб-приложение
- **Redirect URIs**:
    - `http://localhost:5173/*`
    - `http://localhost:3000/*`
- **Web Origins**:
    - `http://localhost:5173`
    - `http://localhost:3000`
- **Protocol Mappers**:
    - Realm roles → `realm_access.roles`
    - Username → `preferred_username`

### Backend Client (`cab-backend`)

- **Тип**: Confidential Client
- **Secret**: `backend-secret-123`
- **Назначение**: Backend API сервис
- **Service Accounts**: включены
- **Direct Access Grants**: включены
- **Protocol Mappers**:
    - Realm roles → `realm_access.roles`

## Тестовые пользователи

### REQUESTER (3 пользователя)

- **requester_1** / **requester_1** - requester1@example.com
- **requester_2** / **requester_2** - requester2@example.com
- **requester_3** / **requester_3** - requester3@example.com

### EXECUTOR (3 пользователя)

- **executor_1** / **executor_1** - executor1@example.com
- **executor_2** / **executor_2** - executor2@example.com
- **executor_3** / **executor_3** - executor3@example.com

### CAB_MANAGER (3 пользователя)

- **cab_manager_1** / **cab_manager_1** - cabmanager1@example.com
- **cab_manager_2** / **cab_manager_2** - cabmanager2@example.com
- **cab_manager_3** / **cab_manager_3** - cabmanager3@example.com

### ADMIN (1 пользователь)

- **admin** / **admin** - admin@example.com

## Автоматическая настройка

Конфигурация автоматически импортируется при запуске контейнера через переменную окружения:

```yaml
KC_IMPORT: /opt/keycloak/data/import/realm-config.json
```

# Использование

## Доступ к Keycloak Admin Console

- URL: http://localhost:8081
- Логин: admin
- Пароль: admin

# Настройки для разработки

В `docker-compose.yml` для Keycloak настроены следующие переменные окружения:

- `KEYCLOAK_ADMIN=admin` - имя администратора
- `KEYCLOAK_ADMIN_PASSWORD=admin` - пароль администратора
- `KC_HTTP_ENABLED=true` - включение HTTP для dev-среды
- `KC_IMPORT` - автоматический импорт конфигурации

# Интеграция с приложением

## Frontend Integration

- **Client ID**: `cab-frontend`

## Backend Integration

- **Client ID**: `cab-backend`
- **Client Secret**: `backend-secret-123`
  ирование** - настроить backup стратегию