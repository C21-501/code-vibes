# CAB System - Change Advisory Board Automation

Система автоматизации CAB (Change Advisory Board) для управления изменениями в IT-инфраструктуре.

## Архитектура

### Стек технологий

- **Backend**: Java 21 + Spring Boot 3
- **Frontend**: React + Vite
- **База данных**: PostgreSQL 16
- **Миграции**: Liquibase
- **Аутентификация**: Keycloak
- **Контейнеризация**: Docker + Docker Compose

### Структура проекта

```
code-vibes/
├── backend/            # Java Spring Boot приложение
│   └── rfc-service/    # RFC сервис
├── frontend/           # React + Vite приложение
├── deploy/             # Конфигурации развертывания
├── docker-compose.yml  # Основная конфигурация Docker Compose
├── .env               # Переменные окружения (не в git)
└── README.md          # Этот файл
```

## Быстрый старт

### Предварительные требования

- Docker и Docker Compose
- Java 21+ (для локальной разработки backend)
- Node.js 20+ (для локальной разработки frontend)

### Запуск всего стека

1. Клонируйте репозиторий:

   ```bash
   git clone <repository-url>
   cd code-vibes
   ```

2. Скопируйте и настройте переменные окружения:

   ```bash
   cp .env.example .env
   # Отредактируйте .env по необходимости
   ```

3. Запустите все сервисы (по умолчанию защита включена):
   ```bash
   docker-compose up -d
   ```

### Доступ к сервисам

После запуска будут доступны:

- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080
- **Keycloak Admin**: http://localhost:8081 (admin/admin)
- **PostgreSQL**: localhost:5432 (postgres/password)

## Разработка

### Backend

Перейдите в директорию `backend/rfc-service` для работы с Java приложением.

### Запуск только инфраструктуры (без backend)

```bash
docker compose -f backend/rfc-service/compose-local.yaml up -d
```

### Запуск только бэкенда

Для запуска только бэкенда с базой данных и Keycloak:

```bash
cd backend/rfc-service
APP_SECURITY_ENABLED=true docker compose up --build
```

Или из корневой директории:

```bash
APP_SECURITY_ENABLED=true docker compose -f backend/rfc-service/docker-compose.yml up --build
```

### Режим без защиты

APP_SECURITY_ENABLED=false конфигурируется в .env файле по примеру из .env.example или (если .env файл не создан)

- В корневом compose:

  ```bash
  APP_SECURITY_ENABLED=false docker compose up -d
  ```

- Только бэкенд:
  ```bash
  cd backend/rfc-service
  APP_SECURITY_ENABLED=false docker compose up --build
  ```
- На винде 
  ```
  $env:APP_SECURITY_ENABLED="false"
  docker compose up -d
  ```

### Frontend

Перейдите в директорию `frontend` для работы с React приложением.

## Сервисы Docker Compose

- **db**: PostgreSQL 16 с базой данных `cab_db`
- **backend**: Java Spring Boot приложение (порт 8080)
- **frontend**: React + Vite приложение (порт 5173)
- **keycloak**: Сервер аутентификации (порт 8081)

Все сервисы подключены к сети `cab-network` для взаимодействия между собой.

## Переменные окружения

Основные переменные в файле `.env`:

- `POSTGRES_PASSWORD` - пароль для PostgreSQL
- `KEYCLOAK_ADMIN` - логин администратора Keycloak
- `KEYCLOAK_ADMIN_PASSWORD` - пароль администратора Keycloak

## Лицензия

См. файл [LICENSE](LICENSE)
