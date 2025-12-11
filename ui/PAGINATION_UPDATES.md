# Обновление пагинации в UI

## Изменения

### 1. Унификация пагинации между страницами

Приведены к единому виду пагинация на страницах:
- `rfc-list.html` - список RFC
- `reference-team.html` - список команд

### 2. Фиксированный размер страницы

- **Размер страницы зафиксирован на 20 элементов** согласно OpenAPI спецификации (`pageable-size` default: 20)
- Удалена возможность изменения размера страницы через UI
- Во всех API запросах используется `size=20`

### 3. Соответствие OpenAPI спецификации

Пагинация полностью соответствует спецификации из `Common.yaml`:

#### Параметры запроса:
- `page` - номер страницы (0-indexed, начинается с 0)
- `size` - размер страницы (зафиксирован на 20)

#### PageResponse schema:
```yaml
PageResponse:
  properties:
    totalElements:    # Общее количество элементов
      type: integer
      format: int64
    totalPages:       # Общее количество страниц
      type: integer
    size:            # Размер страницы (всегда 20)
      type: integer
    number:          # Номер текущей страницы (0-indexed)
      type: integer
    first:           # Является ли первой страницей
      type: boolean
    last:            # Является ли последней страницей
      type: boolean
```

### 4. Единообразный UI пагинации

Обе страницы теперь используют идентичную пагинацию:

```
Показано 1-20 из 156 записей (страница 1 из 8)
[← Предыдущая] [1] ... [3] [4] [5] [6] [7] ... [8] [Следующая →]
```

**Функционал:**
- Кнопки "Предыдущая" / "Следующая" для навигации
- Номера страниц с автоматическим сжатием (показывает до 5 страниц + первая/последняя)
- Троеточие (...) для пропущенных страниц
- Подсветка текущей страницы
- Автоматическое отключение кнопок на первой/последней странице

### 5. API запросы

#### GET /rfc (список RFC)
```
GET /rfc?page=0&size=20&status=NEW&urgency=PLANNED&requesterId=1
```

#### GET /team (список команд)
```
GET /team?page=0&size=20&name=Backend
```

#### Загрузка справочников
Для справочных данных (системы, команды) используется увеличенный размер:
```
GET /system?page=0&size=200  # для загрузки справочника систем
GET /team?page=0&size=200    # для загрузки справочника команд
```

### 6. Изменения в reference-team.html

**Удалено:**
- Поле "Элементов на странице" из фильтров
- Функция `changePageSize()`
- Переменная `pageSize` (заменена на константу)

**Обновлено:**
- `pageSize` теперь константа со значением 20
- Функции пагинации переписаны для соответствия rfc-list.html
- Обновлена структура HTML пагинации
- Добавлены стили для `.pagination-dots`

### 7. Изменения в rfc-list.html

**Обновлено:**
- Комментарии с указанием на фиксированный размер страницы
- Документация в коде с уточнением параметров OpenAPI
- Функция `goToPage()` с подробными комментариями по API запросам
- Глобальная переменная `subsystemCounter` вынесена в общую секцию
- Функция `addSubsystemCard()` исправлена для работы с кешем подсистем

## Проверка

Обе страницы протестированы на:
- ✅ Корректное отображение информации о пагинации
- ✅ Правильная работа кнопок навигации
- ✅ Корректное отображение номеров страниц
- ✅ Автоматическое сжатие списка страниц
- ✅ Отключение кнопок на границах диапазона
- ✅ Отсутствие ошибок линтера

## Дальнейшая интеграция

При подключении к реальному API необходимо:

1. Раскомментировать код с fetch запросами
2. Обработать PageResponse из ответа API
3. Вызвать `updatePaginationUI(pageResponse)` для обновления UI
4. Обновить таблицу данными из `pageResponse.content`

Пример для RFC:
```javascript
async function loadRfcs(page) {
    const response = await fetch(`/api/rfc?page=${page}&size=20`, {
        headers: { 'Authorization': 'Bearer ' + getAuthToken() }
    });
    const pageResponse = await response.json(); // RfcPageResponse
    
    // Update table with pageResponse.content
    renderRfcTable(pageResponse.content);
    
    // Update pagination UI
    updatePaginationUI(pageResponse);
}
```

Аналогично для Teams с учетом фильтра по названию:
```javascript
async function loadTeams(page, nameFilter) {
    let url = `/api/team?page=${page}&size=20`;
    if (nameFilter) url += `&name=${encodeURIComponent(nameFilter)}`;
    
    const response = await fetch(url, {
        headers: { 'Authorization': 'Bearer ' + getAuthToken() }
    });
    const pageResponse = await response.json(); // TeamPageResponse
    
    renderTeamsTable(pageResponse.content);
    updatePaginationInfo(); // uses global state updated from pageResponse
}
```

