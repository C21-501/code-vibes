# Keycloak Configuration для CAB приложения

Эта директория содержит конфигурационные файлы для автоматической настройки Keycloak в рамках CAB приложения.

## Файлы

### `cab-realm.json`
Конфигурационный файл Realm для Keycloak, содержащий:

- **Название Realm**: `cab-realm`
- **Клиент**: `cab-frontend` (public client для фронтенд приложения)
- **Роли**:
  - `requester` - пользователи, которые могут заказывать такси
  - `executor` - водители такси  
  - `cab_manager` - менеджеры такси-службы
  - `admin` - администраторы системы

- **Тестовые пользователи**:
  - `test_requester` / `password123` - роль `requester`
  - `test_executor` / `password123` - роль `executor`
  - `test_manager` / `password123` - роль `cab_manager`
  - `test_admin` / `password123` - роль `admin`

### `import-realm.sh`
Скрипт для автоматического импорта Realm в Keycloak. Скрипт:
- Ожидает полного запуска Keycloak
- Проверяет, существует ли уже Realm
- Импортирует конфигурацию из `cab-realm.json` если Realm не существует

## Использование

1. **Запуск через docker-compose**:
   ```bash
   docker-compose up keycloak
   ```

2. **Ручной импорт Realm** (если автоматический импорт не сработал):
   ```bash
   ./deploy/keycloak/import-realm.sh
   ```

3. **Доступ к Keycloak Admin Console**:
   - URL: http://localhost:8081
   - Логин: `admin`
   - Пароль: `admin`

## Настройки для разработки

В `docker-compose.yml` для Keycloak настроены следующие переменные окружения:
- `KEYCLOAK_ADMIN=admin` - имя администратора
- `KEYCLOAK_ADMIN_PASSWORD=admin` - пароль администратора  
- `KC_HTTP_ENABLED=true` - включение HTTP для dev-среды

## Интеграция с приложением

### Frontend (cab-frontend client)
- **Client ID**: `cab-frontend`
- **Client Type**: Public
- **Valid Redirect URIs**: 
  - `http://localhost:5173/*` (Vite dev server)
  - `http://localhost:3000/*` (альтернативный порт)
- **Web Origins**: 
  - `http://localhost:5173`
  - `http://localhost:3000`

### Backend (будет добавлен позже)
Планируется создание confidential client для бэкенд-сервиса с настройками:
- Service Account включен
- Direct Access Grants включен
- Интеграция с Spring Security

## Следующие шаги

1. **Настройка фронтенда**: Интеграция с Keycloak JS adapter
2. **Настройка бэкенда**: Создание confidential client и настройка Spring Security
3. **Настройка ролей**: Детальная настройка доступа по ролям в приложении
4. **Production конфигурация**: Настройка SSL и production параметров безопасности

## Troubleshooting

### Keycloak не запускается
- Проверьте, что порт 8081 свободен
- Убедитесь, что Docker имеет достаточно памяти

### Импорт Realm не работает
- Проверьте логи: `docker-compose logs keycloak`
- Запустите импорт вручную: `./deploy/keycloak/import-realm.sh`
- Убедитесь, что файл `cab-realm.json` доступен в контейнере

### Ошибки авторизации
- Проверьте настройки CORS в Keycloak
- Убедитесь, что redirect URIs корректно настроены
- Проверьте, что клиент настроен как Public
