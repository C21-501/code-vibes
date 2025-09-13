# 🤖 Telegram Bot на Python

Многофункциональный Telegram бот с примерами различных возможностей и обработчиков.

## 📋 Возможности

- ✅ Обработка команд и текстовых сообщений
- ✅ Inline и Reply клавиатуры
- ✅ Диалоговая система (ConversationHandler)
- ✅ Регистрация пользователей
- ✅ Создание опросов
- ✅ Мини-игра "Камень-Ножницы-Бумага"
- ✅ Система профилей пользователей
- ✅ Настройки и конфигурация
- ✅ Обработка ошибок и логирование

## 🚀 Быстрый старт

### 1. Клонирование репозитория

```bash
git clone <your-repo-url>
cd telegram-bot
```

### 2. Установка зависимостей

```bash
pip install -r requirements.txt
```

### 3. Создание бота в Telegram

1. Откройте Telegram и найдите [@BotFather](https://t.me/botfather)
2. Отправьте команду `/newbot`
3. Придумайте имя для бота (например: "My Awesome Bot")
4. Придумайте username для бота (должен заканчиваться на "bot", например: myawesome_bot)
5. Скопируйте токен, который выдаст BotFather

### 4. Настройка конфигурации

Создайте файл `.env` на основе `.env.example`:

```bash
cp .env.example .env
```

Откройте `.env` и вставьте ваш токен:

```env
BOT_TOKEN=1234567890:ABCdefGHIjklMNOpqrsTUVwxyz
```

### 5. Запуск бота

```bash
python bot.py
```

## 📂 Структура проекта

```
telegram-bot/
├── bot.py            # Основной файл бота
├── keyboards.py      # Модуль с клавиатурами
├── requirements.txt  # Зависимости проекта
├── .env.example     # Пример конфигурации
├── .env            # Ваша конфигурация (не коммитится)
└── README.md       # Документация
```

## 📝 Доступные команды

| Команда | Описание |
|---------|----------|
| `/start` | Начать работу с ботом |
| `/help` | Показать список команд |
| `/profile` | Информация о профиле |
| `/settings` | Настройки бота |
| `/random [min] [max]` | Случайное число |
| `/poll` | Создать опрос |
| `/register` | Пройти регистрацию |
| `/echo [text]` | Повторить текст |
| `/cancel` | Отменить текущую операцию |

## 🎮 Функции бота

### Регистрация пользователей
Бот поддерживает пошаговую регистрацию с сохранением данных пользователя.

### Игра "Камень-Ножницы-Бумага"
Встроенная мини-игра доступна через кнопку "🎮 Игра" в главном меню.

### Создание опросов
Команда `/poll` создает опрос для получения обратной связи.

### Профили пользователей
Система профилей сохраняет информацию о пользователях (в реальном проекте рекомендуется использовать БД).

## 🔧 Настройка и кастомизация

### Добавление новых команд

В файле `bot.py` добавьте новую функцию-обработчик:

```python
async def my_command(update: Update, context: ContextTypes.DEFAULT_TYPE) -> None:
    await update.message.reply_text("Ответ на вашу команду")
```

Зарегистрируйте обработчик в функции `main()`:

```python
application.add_handler(CommandHandler("mycommand", my_command))
```

### Добавление новых клавиатур

В файле `keyboards.py` создайте новую функцию:

```python
def get_my_keyboard():
    keyboard = [
        ["Кнопка 1", "Кнопка 2"],
        ["Кнопка 3"]
    ]
    return ReplyKeyboardMarkup(keyboard, resize_keyboard=True)
```

## 🗄️ База данных

Для production-версии рекомендуется подключить базу данных:

### SQLite
```python
import sqlite3
conn = sqlite3.connect('bot.db')
```

### PostgreSQL
```python
import psycopg2
conn = psycopg2.connect(DATABASE_URL)
```

### MongoDB
```python
from pymongo import MongoClient
client = MongoClient('mongodb://localhost:27017/')
```

## 🚀 Деплой

### Heroku

1. Создайте `Procfile`:
```
worker: python bot.py
```

2. Создайте приложение на Heroku:
```bash
heroku create your-bot-name
heroku config:set BOT_TOKEN=your_token_here
git push heroku main
heroku ps:scale worker=1
```

### VPS/VDS

1. Подключитесь к серверу через SSH
2. Клонируйте репозиторий
3. Установите зависимости
4. Используйте systemd или supervisor для автозапуска

Пример systemd сервиса (`/etc/systemd/system/telegram-bot.service`):

```ini
[Unit]
Description=Telegram Bot
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/telegram-bot
Environment="PATH=/home/ubuntu/.local/bin"
ExecStart=/usr/bin/python3 /home/ubuntu/telegram-bot/bot.py
Restart=always

[Install]
WantedBy=multi-user.target
```

### Docker

Создайте `Dockerfile`:

```dockerfile
FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD ["python", "bot.py"]
```

Запуск:
```bash
docker build -t telegram-bot .
docker run -d --env-file .env telegram-bot
```

## 📊 Мониторинг и логи

### Настройка логирования

В файле `bot.py` уже настроено базовое логирование. Для более подробных логов измените уровень:

```python
logging.basicConfig(
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    level=logging.DEBUG  # Изменить на DEBUG для подробных логов
)
```

### Сохранение логов в файл

```python
logging.basicConfig(
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    level=logging.INFO,
    handlers=[
        logging.FileHandler('bot.log'),
        logging.StreamHandler()
    ]
)
```

## 🔒 Безопасность

1. **Никогда не коммитьте файл `.env` с токеном**
2. Добавьте `.env` в `.gitignore`
3. Используйте переменные окружения для чувствительных данных
4. Валидируйте входные данные от пользователей
5. Ограничивайте доступ к административным командам

## 📚 Полезные ресурсы

- [Документация python-telegram-bot](https://docs.python-telegram-bot.org/)
- [Telegram Bot API](https://core.telegram.org/bots/api)
- [BotFather](https://t.me/botfather)
- [Примеры ботов](https://github.com/python-telegram-bot/python-telegram-bot/tree/master/examples)

## 🤝 Вклад в проект

Приветствуются pull requests. Для больших изменений сначала откройте issue для обсуждения.

## 📄 Лицензия

MIT License - свободно используйте в своих проектах!

## 💬 Поддержка

Если у вас есть вопросы или предложения:
- Создайте issue в репозитории
- Напишите разработчику в Telegram: @your_username

---

**Happy coding! 🚀**