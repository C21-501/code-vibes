# Keycloak Authentication

Keycloak используется для аутентификации и авторизации в системе. Все защищенные эндпоинты требуют JWT токен.

## Получение JWT токена

### Тестовый метод авторизации (будет удален в production)

Для тестирования доступен временный эндпоинт:

**Endpoint**: `POST /api/auth/login`

**Request**:

```json
{
  "username": "username",
  "password": "password"
}
```

**Response**

```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJQbU...",
  "refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJQbU...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "token_type": "Bearer"
}
```

# Использование токена в Swagger

1. **Откройте Swagger UI**: http://localhost:8080/swagger-ui.html
2. **Получите токен** через `/api/auth/login`
3. **Нажмите кнопку "Authorize"** (замочек)
4. **В поле "Value" введите**: `YOUR_ACCESS_TOKEN`
5. **Нажмите "Authorize"** и затем **"Close"**

Теперь все защищенные эндпоинты будут доступны с правами авторизованного пользователя.

# Тестовые пользователи

Доступные тестовые пользователи (логин/пароль):

## REQUESTER (заказчики такси)

- `requester_1` / `requester_1`
- `requester_2` / `requester_2`
- `requester_3` / `requester_3`

## EXECUTOR (водители такси)

- `executor_1` / `executor_1`
- `executor_2` / `executor_2`
- `executor_3` / `executor_3`

## CAB_MANAGER (менеджеры)

- `cab_manager_1` / `cab_manager_1`
- `cab_manager_2` / `cab_manager_2`
- `cab_manager_3` / `cab_manager_3`

## ADMIN (администратор)

- `admin` / `admin`

# Управление пользователями

Пользователей и их роли можно настраивать в Keycloak Admin Console:

- **URL**: http://localhost:8081
- **Логин**: `admin`
- **Пароль**: `admin`

В админке можно:

- Создавать новых пользователей
- Назначать роли (REQUESTER, EXECUTOR, CAB_MANAGER, ADMIN)
- Сбрасывать пароли
- Управлять правами доступа

# Ролевая модель

Доступ к эндпоинтам ограничен ролями:

- **REQUESTER** - доступ к заказам такси
- **EXECUTOR** - доступ к заявкам на выполнение заказов
- **CAB_MANAGER** - управление системой
- **ADMIN** - полный доступ

# Важно

- Тестовый эндпоинт `/api/auth/login` будет удален в production среде
- В production фронтенд будет использовать OAuth2 flow напрямую с Keycloak
- Токены действительны 5 минут, refresh token - 30 минут
- Для продления токена используйте refresh token механизм

# Troubleshooting

Если токен не работает:

1. Проверьте, что токен не истек (lifespan: 5 минут)
2. Убедитесь, что в заголовке указан `Bearer ` перед токеном
3. Проверьте, что пользователь существует и активен в Keycloak
4. Убедитесь, что Keycloak доступен по адресу http://localhost:8081

