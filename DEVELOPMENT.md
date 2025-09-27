# Инструкции по разработке

## Запуск в режиме разработки

### С Hot Module Replacement (HMR)

Для разработки с поддержкой HMR используйте отдельный docker-compose файл:

```bash
# Создайте файл .env с переменными окружения
echo "POSTGRES_PASSWORD=postgres" > .env

# Запустите в режиме разработки
docker-compose -f docker-compose.dev.yml up --build

# Или в фоновом режиме
docker-compose -f docker-compose.dev.yml up --build -d
```

В этом режиме:

- Frontend доступен на http://localhost:5173 с HMR
- Backend доступен на http://localhost:8080
- Keycloak доступен на http://localhost:8081
- PostgreSQL на порту 5432

### Продакшн режим

Для запуска в продакшн режиме:

```bash
# Создайте файл .env с переменными окружения
echo "POSTGRES_PASSWORD=your_secure_password" > .env

# Запустите в продакшн режиме
docker-compose up --build

# Или в фоновом режиме
docker-compose up --build -d
```

В этом режиме:

- Frontend доступен на http://localhost:5173 (статические файлы через nginx)
- Backend доступен на http://localhost:8080
- Keycloak доступен на http://localhost:8081

## Структура проекта

### Фронтенд

- `frontend/src/pages/References.tsx` - Страница справочников
- `frontend/src/components/references/` - Компоненты для работы со справочниками
- `frontend/src/api/referenceApi.ts` - API для CRUD операций
- `frontend/src/types/api.ts` - Типы данных

### Бэкенд

- Расположен в `backend/rfc-service/`
- Содержит собственный `Dockerfile` и `docker-compose.yml`
- CRUD endpoints защищены ролями `CAB_MANAGER` и `ADMIN`
- `POST/PUT/DELETE /api/systems` - Управление системами
- `POST/PUT/DELETE /api/teams` - Управление командами
- `POST/PUT/DELETE /api/users` - Управление пользователями

### Запуск только бэкенда

```bash
# Перейти в директорию бэкенда
cd backend/rfc-service

# Запустить только бэкенд с зависимостями
docker-compose up --build

# Или из корневой директории
docker-compose -f backend/rfc-service/docker-compose.yml up --build
```

## Функциональность

Страница "Справочники" (`/references`) содержит три таба:

1. **Системы** - управление системами с привязкой к ответственным командам
2. **Команды** - управление командами с назначением руководителей
3. **Пользователи** - управление пользователями с назначением ролей

Каждый таб имеет полный CRUD интерфейс:

- Просмотр списка в виде таблицы
- Создание новых записей через модальные окна
- Редактирование существующих записей
- Удаление записей с подтверждением

## Алиасы импортов

Настроены алиасы для удобства импортов:

- `@/components/*` вместо `../components/*`
- `@/api/*` вместо `../api/*`
- `@/types/*` вместо `../types/*`
- И т.д.

## Остановка сервисов

```bash
# Остановить и удалить контейнеры (разработка)
docker-compose -f docker-compose.dev.yml down

# Остановить и удалить контейнеры (продакшн)
docker-compose down

# Остановить и удалить контейнеры вместе с volumes
docker-compose down -v
```
