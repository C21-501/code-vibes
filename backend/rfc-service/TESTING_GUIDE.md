# Руководство по тестированию CAB System (Change Advisory Board)

## Описание системы

CAB System - это система управления изменениями в IT-инфраструктуре через процесс RFC (Request for Change). Система автоматизирует процесс согласования и выполнения изменений в различных подсистемах компании.

### Основные концепции:

- **RFC (Request for Change)** - запрос на изменение, который проходит несколько этапов согласования
- **Система** - большая IT-система компании (например, "Биллинг", "CRM")
- **Подсистема** - часть системы (например, "API Биллинга", "База данных Биллинга")
- **Команда** - группа разработчиков, ответственных за подсистему
- **Статусы RFC**: NEW → UNDER_REVIEW → APPROVED → IMPLEMENTED (или REJECTED)

### Роли пользователей:

- **USER** - обычный пользователь, может создавать RFC
- **RFC_APPROVER** - согласующий, должен одобрить RFC перед выполнением
- **CAB_MANAGER** - менеджер CAB, управляет процессом
- **ADMIN** - администратор системы

---

## Предварительные требования

1. Запущен Docker с базой данных PostgreSQL (порт 5050)
2. Запущен Keycloak (порт 8081)
3. Запущен backend сервис (порт 8080)
4. Установлен REST-клиент (Postman, Insomnia, curl, или HTTPie)

### Запуск инфраструктуры:

```bash
# Из директории backend/rfc-service
docker-compose -f docker-compose-infra.yml up -d

# Проверка статуса
docker ps
```

---

## Этап 1: Авторизация и создание пользователей

### 1.1. Получение токена администратора

**Цель проверки:** Убедиться, что система аутентификации работает

**Метод:** `POST /user/login`

**Тело запроса:**
```json
{
  "username": "admin",
  "password": "admin"
}
```

**Ожидаемый результат:**
- HTTP 200 OK
- В ответе есть `accessToken`, `refreshToken`, `expiresIn`

**Сохраните `accessToken`** - он понадобится для всех последующих запросов!

**Пример для curl:**
```bash
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

---

### 1.2. Проверка текущего пользователя

**Цель проверки:** Убедиться, что токен работает и мы можем получить информацию о себе

**Метод:** `GET /user/me`

**Заголовки:**
```
Authorization: Bearer {accessToken}
```

**Ожидаемый результат:**
- HTTP 200 OK
- В ответе данные пользователя admin с ролью ADMIN

**Пример для curl:**
```bash
curl -X GET http://localhost:8080/user/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

### 1.3. Создание пользователя с ролью USER

**Цель проверки:** Создать обычного пользователя, который будет создавать RFC

**Метод:** `POST /user`

**Заголовки:**
```
Authorization: Bearer {admin_token}
Content-Type: application/json
```

**Тело запроса:**
```json
{
  "username": "john.doe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "password": "password123",
  "role": "USER"
}
```

**Ожидаемый результат:**
- HTTP 201 Created
- В ответе созданный пользователь с ID

**Сохраните ID** пользователя!

---

### 1.4. Создание пользователя RFC_APPROVER

**Цель проверки:** Создать согласующего, который будет одобрять RFC

**Метод:** `POST /user`

**Тело запроса:**
```json
{
  "username": "approver.smith",
  "email": "approver.smith@example.com",
  "firstName": "Jane",
  "lastName": "Smith",
  "password": "password123",
  "role": "RFC_APPROVER"
}
```

**Ожидаемый результат:**
- HTTP 201 Created

---

### 1.5. Создание пользователя CAB_MANAGER

**Цель проверки:** Создать менеджера CAB

**Метод:** `POST /user`

**Тело запроса:**
```json
{
  "username": "manager.johnson",
  "email": "manager.johnson@example.com",
  "firstName": "Bob",
  "lastName": "Johnson",
  "password": "password123",
  "role": "CAB_MANAGER"
}
```

**Ожидаемый результат:**
- HTTP 201 Created

---

### 1.6. Получение списка всех пользователей

**Цель проверки:** Убедиться, что все пользователи созданы

**Метод:** `GET /user?page=0&size=20`

**Ожидаемый результат:**
- HTTP 200 OK
- В списке 4 пользователя: admin, john.doe, approver.smith, manager.johnson

---

## Этап 2: Создание команд

### 2.1. Создание команды Backend

**Цель проверки:** Создать команду разработчиков backend

**Метод:** `POST /team`

**Заголовки:**
```
Authorization: Bearer {admin_token}
Content-Type: application/json
```

**Тело запроса:**
```json
{
  "name": "Backend Team",
  "description": "Команда разработчиков backend-сервисов"
}
```

**Ожидаемый результат:**
- HTTP 201 Created
- В ответе команда с ID

**Сохраните ID** команды как `backend_team_id`!

---

### 2.2. Создание команды Frontend

**Цель проверки:** Создать команду разработчиков frontend

**Метод:** `POST /team`

**Тело запроса:**
```json
{
  "name": "Frontend Team",
  "description": "Команда разработчиков frontend-приложений"
}
```

**Ожидаемый результат:**
- HTTP 201 Created

**Сохраните ID** команды как `frontend_team_id`!

---

### 2.3. Получение списка команд

**Цель проверки:** Проверить, что команды созданы

**Метод:** `GET /team?page=0&size=20`

**Ожидаемый результат:**
- HTTP 200 OK
- В списке 2 команды

---

## Этап 3: Создание систем и подсистем

### 3.1. Создание системы "Биллинг"

**Цель проверки:** Создать систему верхнего уровня

**Метод:** `POST /system`

**Заголовки:**
```
Authorization: Bearer {admin_token}
Content-Type: application/json
```

**Тело запроса:**
```json
{
  "name": "Биллинг",
  "description": "Система биллинга и тарификации"
}
```

**Ожидаемый результат:**
- HTTP 201 Created
- В ответе система с ID

**Сохраните ID** как `billing_system_id`!

---

### 3.2. Создание подсистемы "API Биллинга"

**Цель проверки:** Создать подсистему и привязать к команде

**Метод:** `POST /system/{billing_system_id}/subsystem`

**Тело запроса:**
```json
{
  "name": "API Биллинга",
  "description": "REST API для работы с тарифами",
  "teamId": {backend_team_id}
}
```

**Ожидаемый результат:**
- HTTP 201 Created
- Подсистема привязана к Backend Team

**Сохраните ID** как `billing_api_subsystem_id`!

---

### 3.3. Создание подсистемы "База данных Биллинга"

**Цель проверки:** Создать вторую подсистему

**Метод:** `POST /system/{billing_system_id}/subsystem`

**Тело запроса:**
```json
{
  "name": "База данных Биллинга",
  "description": "PostgreSQL база данных биллинга",
  "teamId": {backend_team_id}
}
```

**Ожидаемый результат:**
- HTTP 201 Created

**Сохраните ID** как `billing_db_subsystem_id`!

---

### 3.4. Создание системы "CRM"

**Цель проверки:** Создать вторую систему

**Метод:** `POST /system`

**Тело запроса:**
```json
{
  "name": "CRM",
  "description": "Система управления клиентами"
}
```

**Ожидаемый результат:**
- HTTP 201 Created

**Сохраните ID** как `crm_system_id`!

---

### 3.5. Создание подсистемы "Веб-интерфейс CRM"

**Цель проверки:** Создать frontend подсистему

**Метод:** `POST /system/{crm_system_id}/subsystem`

**Тело запроса:**
```json
{
  "name": "Веб-интерфейс CRM",
  "description": "React-приложение для работы с CRM",
  "teamId": {frontend_team_id}
}
```

**Ожидаемый результат:**
- HTTP 201 Created

**Сохраните ID** как `crm_web_subsystem_id`!

---

### 3.6. Получение всех систем

**Цель проверки:** Проверить созданные системы

**Метод:** `GET /system?page=0&size=20`

**Ожидаемый результат:**
- HTTP 200 OK
- В списке 2 системы: Биллинг и CRM

---

### 3.7. Получение подсистем Биллинга

**Цель проверки:** Проверить подсистемы системы

**Метод:** `GET /system/{billing_system_id}/subsystem`

**Ожидаемый результат:**
- HTTP 200 OK
- В списке 2 подсистемы: API и База данных

---

## Этап 4: Создание и управление RFC

### 4.1. Получение токена пользователя john.doe

**Цель проверки:** Авторизоваться под обычным пользователем

**Метод:** `POST /user/login`

**Тело запроса:**
```json
{
  "username": "john.doe",
  "password": "password123"
}
```

**Ожидаемый результат:**
- HTTP 200 OK
- Новый accessToken

**Сохраните токен** как `user_token`!

---

### 4.2. Создание RFC

**Цель проверки:** Создать запрос на изменение, затрагивающий несколько подсистем

**Метод:** `POST /rfc`

**Заголовки:**
```
Authorization: Bearer {user_token}
Content-Type: application/json
```

**Тело запроса:**
```json
{
  "title": "Миграция тарифных планов на новую структуру",
  "description": "Необходимо изменить структуру хранения тарифных планов в БД и обновить API для работы с новой структурой",
  "implementationDate": "2025-12-01T10:00:00Z",
  "urgency": "PLANNED",
  "affectedSystems": [
    {
      "systemId": {billing_system_id},
      "affectedSubsystems": [
        {
          "subsystemId": {billing_api_subsystem_id},
          "executorId": {john_doe_user_id}
        },
        {
          "subsystemId": {billing_db_subsystem_id},
          "executorId": {john_doe_user_id}
        }
      ]
    }
  ]
}
```

**Ожидаемый результат:**
- HTTP 201 Created
- RFC создан со статусом NEW
- В поле `actions` должны быть: UPDATE, DELETE (так как john.doe - создатель)
- Для каждой подсистемы:
  - `confirmationStatus`: PENDING
  - `executionStatus`: PENDING

**Сохраните ID** RFC как `rfc_id`!

---

### 4.3. Получение RFC по ID

**Цель проверки:** Проверить детальную информацию о RFC

**Метод:** `GET /rfc/{rfc_id}`

**Заголовки:**
```
Authorization: Bearer {user_token}
```

**Ожидаемый результат:**
- HTTP 200 OK
- Статус RFC: NEW
- Две затронутые подсистемы с правильными названиями
- Все статусы подсистем в PENDING

---

### 4.4. Получение списка всех RFC

**Цель проверки:** Проверить фильтрацию RFC

**Метод:** `GET /rfc?page=0&size=20&status=NEW`

**Ожидаемый результат:**
- HTTP 200 OK
- В списке наш RFC

---

## Этап 5: Подтверждение исполнителями

### 5.1. Подтверждение первой подсистемы

**Цель проверки:** Исполнитель подтверждает готовность выполнить изменение

**Метод:** `PATCH /rfc/{rfc_id}/subsystem/{billing_api_subsystem_id}/confirmation`

**Заголовки:**
```
Authorization: Bearer {user_token}
Content-Type: application/json
```

**Тело запроса:**
```json
{
  "status": "CONFIRMED",
  "comment": "API готов к изменениям, миграция данных подготовлена"
}
```

**Ожидаемый результат:**
- HTTP 200 OK
- `confirmationStatus` изменился на CONFIRMED
- В таблице `rfc_affected_subsystem_history` создана запись с operation=UPDATE

---

### 5.2. Проверка автоматического изменения статуса RFC

**Цель проверки:** Проверить, что статус RFC автоматически изменился на UNDER_REVIEW

**Метод:** `GET /rfc/{rfc_id}`

**Ожидаемый результат:**
- HTTP 200 OK
- Статус RFC: UNDER_REVIEW (так как вторая подсистема еще в PENDING)

**Ожидание:** Подождите 3-5 секунд (cron job работает каждые 3 секунды)

---

### 5.3. Подтверждение второй подсистемы

**Цель проверки:** Подтвердить вторую подсистему

**Метод:** `PATCH /rfc/{rfc_id}/subsystem/{billing_db_subsystem_id}/confirmation`

**Тело запроса:**
```json
{
  "status": "CONFIRMED",
  "comment": "База данных готова к миграции схемы"
}
```

**Ожидаемый результат:**
- HTTP 200 OK
- Обе подсистемы теперь CONFIRMED

---

### 5.4. Проверка изменения статуса RFC после всех подтверждений

**Цель проверки:** Статус RFC остается UNDER_REVIEW, так как нет одобрения от RFC_APPROVER

**Метод:** `GET /rfc/{rfc_id}`

**Ожидаемый результат:**
- Статус RFC: UNDER_REVIEW
- Обе подсистемы в статусе CONFIRMED

---

## Этап 6: Согласование RFC аппрувером

### 6.1. Получение токена аппрувера

**Цель проверки:** Авторизоваться под согласующим

**Метод:** `POST /user/login`

**Тело запроса:**
```json
{
  "username": "approver.smith",
  "password": "password123"
}
```

**Ожидаемый результат:**
- HTTP 200 OK

**Сохраните токен** как `approver_token`!

---

### 6.2. Проверка доступных действий для аппрувера

**Цель проверки:** Аппрувер должен видеть действие APPROVE

**Метод:** `GET /rfc/{rfc_id}`

**Заголовки:**
```
Authorization: Bearer {approver_token}
```

**Ожидаемый результат:**
- В поле `actions` должно быть: APPROVE

---

### 6.3. Согласование RFC

**Цель проверки:** RFC_APPROVER одобряет изменение

**Метод:** `POST /rfc/{rfc_id}/approve`

**Заголовки:**
```
Authorization: Bearer {approver_token}
Content-Type: application/json
```

**Тело запроса:**
```json
{
  "comment": "Изменения одобрены, риски оценены как низкие"
}
```

**Ожидаемый результат:**
- HTTP 200 OK
- Создана запись об одобрении

---

### 6.4. Получение списка одобрений RFC

**Цель проверки:** Проверить, что одобрение сохранено

**Метод:** `GET /rfc/{rfc_id}/approvals`

**Ожидаемый результат:**
- HTTP 200 OK
- В списке одно одобрение от approver.smith
- `isApproved`: true

---

### 6.5. Проверка автоматического изменения статуса на APPROVED

**Цель проверки:** После одобрения всех аппруверов статус меняется на APPROVED

**Метод:** `GET /rfc/{rfc_id}`

**Ожидание:** Подождите 3-5 секунд для работы cron job

**Ожидаемый результат:**
- Статус RFC: APPROVED
- Обе подсистемы в confirmationStatus: CONFIRMED
- Обе подсистемы в executionStatus: PENDING

---

## Этап 7: Выполнение изменений

### 7.1. Начало выполнения первой подсистемы

**Цель проверки:** Исполнитель начинает выполнять изменения

**Метод:** `PATCH /rfc/{rfc_id}/subsystem/{billing_api_subsystem_id}/execution`

**Заголовки:**
```
Authorization: Bearer {user_token}
Content-Type: application/json
```

**Тело запроса:**
```json
{
  "status": "IN_PROGRESS",
  "comment": "Начал развертывание новой версии API"
}
```

**Ожидаемый результат:**
- HTTP 200 OK
- `executionStatus`: IN_PROGRESS
- В истории создана запись

---

### 7.2. Попытка пропустить статус (негативный тест)

**Цель проверки:** Нельзя пропускать статусы (PENDING -> DONE)

**Метод:** `PATCH /rfc/{rfc_id}/subsystem/{billing_db_subsystem_id}/execution`

**Тело запроса:**
```json
{
  "status": "DONE",
  "comment": "Попытка пропустить IN_PROGRESS"
}
```

**Ожидаемый результат:**
- HTTP 400 Bad Request
- Ошибка валидации: "Нельзя пропустить промежуточный статус"

---

### 7.3. Завершение первой подсистемы

**Цель проверки:** Исполнитель завершает работу

**Метод:** `PATCH /rfc/{rfc_id}/subsystem/{billing_api_subsystem_id}/execution`

**Тело запроса:**
```json
{
  "status": "DONE",
  "comment": "API успешно развернут и протестирован"
}
```

**Ожидаемый результат:**
- HTTP 200 OK
- `executionStatus`: DONE

---

### 7.4. Выполнение второй подсистемы

**Цель проверки:** Выполнить изменения во второй подсистеме

**Шаг 1:** Начать выполнение
```
PATCH /rfc/{rfc_id}/subsystem/{billing_db_subsystem_id}/execution
{
  "status": "IN_PROGRESS",
  "comment": "Применение миграций БД"
}
```

**Шаг 2:** Завершить выполнение
```
PATCH /rfc/{rfc_id}/subsystem/{billing_db_subsystem_id}/execution
{
  "status": "DONE",
  "comment": "Миграция выполнена успешно, rollback скрипты подготовлены"
}
```

**Ожидаемый результат:**
- Обе подсистемы в статусе DONE

---

### 7.5. Проверка финального статуса RFC

**Цель проверки:** После выполнения всех подсистем RFC переходит в IMPLEMENTED

**Метод:** `GET /rfc/{rfc_id}`

**Ожидание:** Подождите 3-5 секунд для работы cron job

**Ожидаемый результат:**
- Статус RFC: IMPLEMENTED
- Все подсистемы:
  - `confirmationStatus`: CONFIRMED
  - `executionStatus`: DONE

---

## Этап 8: Негативное тестирование - Отклонение RFC

### 8.1. Создание второго RFC

**Цель проверки:** Создать RFC для проверки сценария отклонения

**Метод:** `POST /rfc`

**Заголовки:**
```
Authorization: Bearer {user_token}
```

**Тело запроса:**
```json
{
  "title": "Обновление UI компонентов CRM",
  "description": "Миграция на новую версию библиотеки UI компонентов",
  "implementationDate": "2025-11-20T14:00:00Z",
  "urgency": "URGENT",
  "affectedSystems": [
    {
      "systemId": {crm_system_id},
      "affectedSubsystems": [
        {
          "subsystemId": {crm_web_subsystem_id},
          "executorId": {john_doe_user_id}
        }
      ]
    }
  ]
}
```

**Сохраните ID** как `rfc2_id`!

---

### 8.2. Отклонение подсистемы

**Цель проверки:** Исполнитель отклоняет изменение

**Метод:** `PATCH /rfc/{rfc2_id}/subsystem/{crm_web_subsystem_id}/confirmation`

**Заголовки:**
```
Authorization: Bearer {user_token}
```

**Тело запроса:**
```json
{
  "status": "REJECTED",
  "comment": "Новая версия библиотеки несовместима с текущим кодом, требуется больше времени"
}
```

**Ожидаемый результат:**
- HTTP 200 OK
- `confirmationStatus`: REJECTED

---

### 8.3. Проверка автоматического отклонения RFC

**Цель проверки:** При отклонении подсистемы весь RFC отклоняется

**Метод:** `GET /rfc/{rfc2_id}`

**Ожидание:** Подождите 3-5 секунд

**Ожидаемый результат:**
- Статус RFC: REJECTED

---

### 8.4. Попытка изменить статус отклоненной подсистемы (негативный тест)

**Цель проверки:** Нельзя изменить статус REJECTED

**Метод:** `PATCH /rfc/{rfc2_id}/subsystem/{crm_web_subsystem_id}/confirmation`

**Тело запроса:**
```json
{
  "status": "CONFIRMED",
  "comment": "Попытка изменить после отклонения"
}
```

**Ожидаемый результат:**
- HTTP 400 Bad Request
- Ошибка: "Нельзя изменить статус подтверждения из REJECTED"

---

## Этап 9: Тестирование прав доступа

### 9.1. Попытка изменить чужую подсистему (негативный тест)

**Цель проверки:** Только исполнитель или ADMIN могут менять статусы

**Подготовка:** Создайте нового пользователя:
```json
{
  "username": "hacker.bob",
  "email": "hacker@example.com",
  "firstName": "Bob",
  "lastName": "Hacker",
  "password": "password123",
  "role": "USER"
}
```

Получите его токен через `/user/login`

**Метод:** `PATCH /rfc/{rfc_id}/subsystem/{billing_api_subsystem_id}/execution`

**Заголовки:**
```
Authorization: Bearer {hacker_token}
```

**Тело запроса:**
```json
{
  "status": "IN_PROGRESS",
  "comment": "Попытка изменить чужую подсистему"
}
```

**Ожидаемый результат:**
- HTTP 403 Forbidden
- Ошибка: "Недостаточно прав для изменения статуса подсистемы"

---

### 9.2. Попытка согласовать RFC без роли (негативный тест)

**Цель проверки:** Только RFC_APPROVER может согласовывать

**Метод:** `POST /rfc/{rfc_id}/approve`

**Заголовки:**
```
Authorization: Bearer {hacker_token}
```

**Ожидаемый результат:**
- HTTP 403 Forbidden
- Ошибка: "Недостаточно прав для согласования RFC"

---

## Этап 10: Проверка фильтрации и поиска

### 10.1. Фильтрация RFC по статусу

**Цель проверки:** Получить только реализованные RFC

**Метод:** `GET /rfc?status=IMPLEMENTED&page=0&size=20`

**Ожидаемый результат:**
- HTTP 200 OK
- В списке только RFC со статусом IMPLEMENTED

---

### 10.2. Фильтрация по срочности

**Цель проверки:** Получить срочные RFC

**Метод:** `GET /rfc?urgency=URGENT&page=0&size=20`

**Ожидаемый результат:**
- В списке только RFC с urgency=URGENT

---

### 10.3. Фильтрация по создателю

**Цель проверки:** Получить RFC конкретного пользователя

**Метод:** `GET /rfc?requesterId={john_doe_user_id}&page=0&size=20`

**Ожидаемый результат:**
- В списке только RFC, созданные john.doe

---

### 10.4. Поиск пользователей

**Цель проверки:** Найти пользователей по имени

**Метод:** `GET /user?searchString=john&page=0&size=20`

**Ожидаемый результат:**
- В списке пользователи с "john" в имени или username

---

## Этап 11: Проверка работы с вложениями (если реализовано)

### 11.1. Загрузка файла

**Метод:** `POST /attachment`

**Заголовки:**
```
Authorization: Bearer {user_token}
Content-Type: multipart/form-data
```

**Тело:**
- file: (выберите файл размером < 5MB)

**Ожидаемый результат:**
- HTTP 201 Created
- В ответе ID файла

---

### 11.2. Привязка файла к RFC при создании

**Метод:** `POST /rfc`

**В тело добавить:**
```json
{
  "title": "RFC с документацией",
  ...
  "attachmentIds": [{file_id}]
}
```

**Ожидаемый результат:**
- RFC создан с прикрепленным файлом

---

## Проверочный чек-лист

### Функциональное тестирование:
- [ ] Авторизация работает для всех ролей
- [ ] Создание пользователей с разными ролями
- [ ] Создание команд
- [ ] Создание систем и подсистем
- [ ] Создание RFC с несколькими подсистемами
- [ ] Подтверждение подсистем исполнителями
- [ ] Согласование RFC аппруверами
- [ ] Выполнение изменений (PENDING → IN_PROGRESS → DONE)
- [ ] Отклонение RFC
- [ ] Автоматическое изменение статусов RFC (cron job)
- [ ] История изменений статусов сохраняется

### Валидация и безопасность:
- [ ] Нельзя пропустить статус выполнения
- [ ] Нельзя вернуться к предыдущему статусу
- [ ] Нельзя изменить статус из REJECTED
- [ ] Только исполнитель или ADMIN могут менять статусы подсистем
- [ ] Только RFC_APPROVER/CAB_MANAGER/ADMIN могут согласовывать RFC
- [ ] Создатель RFC может его редактировать и удалять

### API и данные:
- [ ] Пагинация работает корректно
- [ ] Фильтрация по статусу работает
- [ ] Фильтрация по срочности работает
- [ ] Поиск пользователей работает
- [ ] Все ID возвращаются корректно
- [ ] Timestamps создаются автоматически

---

## Ожидаемый финальный результат

После прохождения всех этапов у вас должно быть:

1. **4 пользователя:**
   - admin (ADMIN)
   - john.doe (USER)
   - approver.smith (RFC_APPROVER)
   - manager.johnson (CAB_MANAGER)
   - hacker.bob (USER)

2. **2 команды:**
   - Backend Team
   - Frontend Team

3. **2 системы:**
   - Биллинг (с 2 подсистемами)
   - CRM (с 1 подсистемой)

4. **Минимум 2 RFC:**
   - Один в статусе IMPLEMENTED (успешно прошел все этапы)
   - Один в статусе REJECTED (был отклонен)

5. **История изменений:**
   - Все изменения статусов записаны в `rfc_affected_subsystem_history`
   - Все одобрения записаны в `rfc_approval`

---

## Инструменты для проверки данных в БД

Если нужно проверить данные напрямую в базе:

```bash
# Подключение к PostgreSQL
docker exec -it cab_db psql -U postgres -d cab_db

# Проверка пользователей
SELECT id, username, role FROM users;

# Проверка RFC и их статусов
SELECT id, title, status, urgency FROM rfc;

# Проверка статусов подсистем
SELECT id, rfc_id, subsystem_id, confirmation_status, execution_status
FROM rfc_affected_subsystem;

# Проверка истории изменений
SELECT * FROM rfc_affected_subsystem_history
ORDER BY create_datetime DESC LIMIT 20;

# Проверка одобрений
SELECT * FROM rfc_approval;
```

---

## Troubleshooting

### Проблема: Токен невалиден
**Решение:** Получите новый токен через `/user/login`. Токены истекают через 5 минут.

### Проблема: Статус RFC не меняется автоматически
**Решение:** Подождите 3-5 секунд. Cron job запускается каждые 3 секунды.

### Проблема: 403 Forbidden при изменении статуса
**Решение:** Проверьте, что вы используете токен исполнителя подсистемы или ADMIN.

### Проблема: База данных пустая
**Решение:** Проверьте, что Liquibase миграции выполнились. Посмотрите логи backend сервиса.

---

## Заключение

Эта инструкция покрывает все основные сценарии использования CAB System. После прохождения всех этапов система полностью протестирована и готова к использованию.

Для автоматизации тестирования рекомендуется создать коллекцию в Postman или набор тестов с использованием данной инструкции как основы.