#!/usr/bin/env python3
"""
Telegram Bot - Многофункциональный бот с примерами различных возможностей
"""

import os
import logging
from datetime import datetime
from dotenv import load_dotenv
from telegram import Update, InlineKeyboardButton, InlineKeyboardMarkup
from telegram.ext import (
    Application,
    CommandHandler,
    MessageHandler,
    CallbackQueryHandler,
    ConversationHandler,
    ContextTypes,
    filters
)
from keyboards import get_main_keyboard, get_inline_keyboard, get_settings_keyboard

# Загрузка переменных окружения
load_dotenv()

# Настройка логирования
logging.basicConfig(
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    level=logging.INFO
)
logger = logging.getLogger(__name__)

# Состояния для ConversationHandler
WAITING_FOR_NAME, WAITING_FOR_AGE = range(2)

# Данные пользователей (в реальном проекте используйте БД)
user_data = {}


async def start(update: Update, context: ContextTypes.DEFAULT_TYPE) -> None:
    """Обработчик команды /start"""
    user = update.effective_user
    user_id = user.id
    
    # Сохраняем данные пользователя
    if user_id not in user_data:
        user_data[user_id] = {
            'username': user.username,
            'first_name': user.first_name,
            'registered': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        }
    
    welcome_text = (
        f"👋 Привет, {user.first_name}!\n\n"
        "Я многофункциональный бот. Вот что я умею:\n\n"
        "📝 /help - Список всех команд\n"
        "⚙️ /settings - Настройки\n"
        "👤 /profile - Ваш профиль\n"
        "🎲 /random - Случайное число\n"
        "📊 /poll - Создать опрос\n"
        "💬 /register - Регистрация с диалогом\n"
        "🔄 /echo - Эхо-режим\n\n"
        "Выберите действие из меню ниже:"
    )
    
    await update.message.reply_text(
        welcome_text,
        reply_markup=get_main_keyboard()
    )


async def help_command(update: Update, context: ContextTypes.DEFAULT_TYPE) -> None:
    """Обработчик команды /help"""
    help_text = """
📚 **Доступные команды:**

🔹 /start - Начать работу с ботом
🔹 /help - Показать это сообщение
🔹 /profile - Информация о вашем профиле
🔹 /settings - Настройки бота
🔹 /random [min] [max] - Случайное число
🔹 /poll - Создать опрос
🔹 /register - Пройти регистрацию
🔹 /echo [text] - Повторить текст
🔹 /cancel - Отменить текущую операцию

**💡 Дополнительные функции:**
• Отправьте любой текст, и я отвечу
• Используйте клавиатуру для быстрого доступа
• Нажимайте на inline-кнопки для действий
    """
    
    await update.message.reply_text(
        help_text,
        parse_mode='Markdown'
    )


async def profile(update: Update, context: ContextTypes.DEFAULT_TYPE) -> None:
    """Показать профиль пользователя"""
    user_id = update.effective_user.id
    
    if user_id in user_data:
        data = user_data[user_id]
        profile_text = (
            "👤 **Ваш профиль:**\n\n"
            f"🆔 ID: `{user_id}`\n"
            f"👤 Username: @{data.get('username', 'не указан')}\n"
            f"📝 Имя: {data.get('first_name', 'не указано')}\n"
            f"📅 Дата регистрации: {data.get('registered', 'неизвестно')}\n"
        )
        
        if 'full_name' in data:
            profile_text += f"📋 Полное имя: {data['full_name']}\n"
        if 'age' in data:
            profile_text += f"🎂 Возраст: {data['age']}\n"
    else:
        profile_text = "❌ Профиль не найден. Используйте /start для начала."
    
    await update.message.reply_text(
        profile_text,
        parse_mode='Markdown'
    )


async def settings(update: Update, context: ContextTypes.DEFAULT_TYPE) -> None:
    """Показать настройки"""
    settings_text = "⚙️ **Настройки бота**\n\nВыберите параметр для изменения:"
    
    await update.message.reply_text(
        settings_text,
        reply_markup=get_settings_keyboard(),
        parse_mode='Markdown'
    )


async def random_number(update: Update, context: ContextTypes.DEFAULT_TYPE) -> None:
    """Генерация случайного числа"""
    import random
    
    # Парсим аргументы команды
    args = context.args
    
    try:
        if len(args) == 2:
            min_val = int(args[0])
            max_val = int(args[1])
        elif len(args) == 1:
            min_val = 1
            max_val = int(args[0])
        else:
            min_val = 1
            max_val = 100
        
        if min_val > max_val:
            min_val, max_val = max_val, min_val
        
        number = random.randint(min_val, max_val)
        
        await update.message.reply_text(
            f"🎲 Случайное число от {min_val} до {max_val}:\n\n"
            f"**{number}**",
            parse_mode='Markdown'
        )
    except (ValueError, IndexError):
        await update.message.reply_text(
            "❌ Неверный формат команды.\n"
            "Используйте: /random [min] [max]\n"
            "Пример: /random 1 10"
        )


async def create_poll(update: Update, context: ContextTypes.DEFAULT_TYPE) -> None:
    """Создать опрос"""
    questions = ["Отлично! 🎉", "Хорошо 👍", "Нормально 😐", "Плохо 👎"]
    
    message = await context.bot.send_poll(
        update.effective_chat.id,
        "📊 Как вам этот бот?",
        questions,
        is_anonymous=False,
        allows_multiple_answers=False,
    )
    
    # Сохраняем информацию об опросе
    context.bot_data[message.poll.id] = {
        "questions": questions,
        "message_id": message.message_id,
        "chat_id": update.effective_chat.id,
        "answers": 0,
    }


async def echo(update: Update, context: ContextTypes.DEFAULT_TYPE) -> None:
    """Эхо-функция"""
    text = ' '.join(context.args) if context.args else update.message.text
    
    if text and text != '/echo':
        await update.message.reply_text(f"🔄 {text}")
    else:
        await update.message.reply_text(
            "📝 Отправьте текст после команды /echo\n"
            "Пример: /echo Привет, мир!"
        )


async def start_registration(update: Update, context: ContextTypes.DEFAULT_TYPE) -> int:
    """Начать процесс регистрации"""
    await update.message.reply_text(
        "📝 Давайте пройдем регистрацию!\n\n"
        "Как вас зовут? (полное имя)",
        reply_markup=InlineKeyboardMarkup([
            [InlineKeyboardButton("❌ Отмена", callback_data="cancel_registration")]
        ])
    )
    return WAITING_FOR_NAME


async def get_name(update: Update, context: ContextTypes.DEFAULT_TYPE) -> int:
    """Получить имя и запросить возраст"""
    user_id = update.effective_user.id
    name = update.message.text
    
    if user_id not in user_data:
        user_data[user_id] = {}
    
    user_data[user_id]['full_name'] = name
    
    await update.message.reply_text(
        f"Приятно познакомиться, {name}!\n\n"
        "Сколько вам лет?",
        reply_markup=InlineKeyboardMarkup([
            [InlineKeyboardButton("❌ Отмена", callback_data="cancel_registration")]
        ])
    )
    return WAITING_FOR_AGE


async def get_age(update: Update, context: ContextTypes.DEFAULT_TYPE) -> int:
    """Получить возраст и завершить регистрацию"""
    user_id = update.effective_user.id
    
    try:
        age = int(update.message.text)
        
        if age < 1 or age > 120:
            await update.message.reply_text(
                "❌ Пожалуйста, введите реальный возраст (от 1 до 120)"
            )
            return WAITING_FOR_AGE
        
        user_data[user_id]['age'] = age
        
        await update.message.reply_text(
            f"✅ Регистрация завершена!\n\n"
            f"📋 Имя: {user_data[user_id]['full_name']}\n"
            f"🎂 Возраст: {age}\n\n"
            "Используйте /profile для просмотра профиля",
            reply_markup=get_main_keyboard()
        )
        
        return ConversationHandler.END
        
    except ValueError:
        await update.message.reply_text(
            "❌ Пожалуйста, введите число"
        )
        return WAITING_FOR_AGE


async def cancel_registration(update: Update, context: ContextTypes.DEFAULT_TYPE) -> int:
    """Отменить регистрацию"""
    await update.message.reply_text(
        "❌ Регистрация отменена",
        reply_markup=get_main_keyboard()
    )
    return ConversationHandler.END


async def button_handler(update: Update, context: ContextTypes.DEFAULT_TYPE) -> None:
    """Обработчик inline-кнопок"""
    query = update.callback_query
    await query.answer()
    
    data = query.data
    
    if data == "cancel_registration":
        await query.message.reply_text(
            "❌ Регистрация отменена",
            reply_markup=get_main_keyboard()
        )
        return ConversationHandler.END
    
    elif data.startswith("settings_"):
        setting = data.replace("settings_", "")
        await query.message.edit_text(
            f"⚙️ Вы выбрали настройку: **{setting.replace('_', ' ').title()}**\n\n"
            "ℹ️ В реальном боте здесь будет функционал изменения настроек",
            parse_mode='Markdown'
        )
    
    elif data == "back_to_menu":
        await query.message.edit_text(
            "📋 Главное меню",
            reply_markup=get_inline_keyboard()
        )


async def text_handler(update: Update, context: ContextTypes.DEFAULT_TYPE) -> None:
    """Обработчик текстовых сообщений"""
    text = update.message.text.lower()
    
    # Обработка кнопок клавиатуры
    if text == "📊 статистика":
        total_users = len(user_data)
        await update.message.reply_text(
            f"📊 **Статистика бота:**\n\n"
            f"👥 Всего пользователей: {total_users}\n"
            f"📅 Бот запущен: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n",
            parse_mode='Markdown'
        )
    
    elif text == "ℹ️ о боте":
        await update.message.reply_text(
            "🤖 **О боте**\n\n"
            "Это демонстрационный Telegram бот на Python.\n"
            "Создан с использованием библиотеки python-telegram-bot.\n\n"
            "📚 Версия: 1.0.0\n"
            "👨‍💻 Разработчик: @your_username\n"
            "📝 Лицензия: MIT",
            parse_mode='Markdown'
        )
    
    elif text == "🎮 игра":
        keyboard = [
            [
                InlineKeyboardButton("🪨 Камень", callback_data="game_rock"),
                InlineKeyboardButton("📄 Бумага", callback_data="game_paper"),
                InlineKeyboardButton("✂️ Ножницы", callback_data="game_scissors")
            ]
        ]
        await update.message.reply_text(
            "🎮 Давайте сыграем в 'Камень, Ножницы, Бумага'!\n"
            "Выберите ваш ход:",
            reply_markup=InlineKeyboardMarkup(keyboard)
        )
    
    elif text == "⚙️ настройки":
        await settings(update, context)
    
    else:
        # Эхо для неизвестных сообщений
        await update.message.reply_text(
            f"Вы написали: {update.message.text}\n\n"
            "Используйте /help для списка команд"
        )


async def game_handler(update: Update, context: ContextTypes.DEFAULT_TYPE) -> None:
    """Обработчик игры Камень-Ножницы-Бумага"""
    import random
    
    query = update.callback_query
    await query.answer()
    
    user_choice = query.data.replace("game_", "")
    choices = ["rock", "paper", "scissors"]
    bot_choice = random.choice(choices)
    
    choices_emoji = {
        "rock": "🪨",
        "paper": "📄", 
        "scissors": "✂️"
    }
    
    # Определяем победителя
    if user_choice == bot_choice:
        result = "🤝 Ничья!"
    elif (user_choice == "rock" and bot_choice == "scissors") or \
         (user_choice == "paper" and bot_choice == "rock") or \
         (user_choice == "scissors" and bot_choice == "paper"):
        result = "🎉 Вы выиграли!"
    else:
        result = "😔 Вы проиграли!"
    
    await query.message.edit_text(
        f"🎮 **Результат игры:**\n\n"
        f"Ваш выбор: {choices_emoji[user_choice]}\n"
        f"Мой выбор: {choices_emoji[bot_choice]}\n\n"
        f"{result}",
        parse_mode='Markdown'
    )


async def error_handler(update: Update, context: ContextTypes.DEFAULT_TYPE) -> None:
    """Обработчик ошибок"""
    logger.error(f"Update {update} caused error {context.error}")
    
    if update and update.effective_message:
        await update.effective_message.reply_text(
            "❌ Произошла ошибка при обработке вашего запроса. "
            "Пожалуйста, попробуйте позже."
        )


def main() -> None:
    """Главная функция"""
    # Получаем токен из переменных окружения
    token = os.getenv('BOT_TOKEN')
    
    if not token:
        logger.error("BOT_TOKEN не найден в переменных окружения!")
        return
    
    # Создаем приложение
    application = Application.builder().token(token).build()
    
    # Регистрируем обработчики команд
    application.add_handler(CommandHandler("start", start))
    application.add_handler(CommandHandler("help", help_command))
    application.add_handler(CommandHandler("profile", profile))
    application.add_handler(CommandHandler("settings", settings))
    application.add_handler(CommandHandler("random", random_number))
    application.add_handler(CommandHandler("poll", create_poll))
    application.add_handler(CommandHandler("echo", echo))
    
    # Регистрируем ConversationHandler для регистрации
    conv_handler = ConversationHandler(
        entry_points=[CommandHandler('register', start_registration)],
        states={
            WAITING_FOR_NAME: [MessageHandler(filters.TEXT & ~filters.COMMAND, get_name)],
            WAITING_FOR_AGE: [MessageHandler(filters.TEXT & ~filters.COMMAND, get_age)],
        },
        fallbacks=[
            CommandHandler('cancel', cancel_registration),
            CallbackQueryHandler(cancel_registration, pattern="^cancel_registration$")
        ],
    )
    application.add_handler(conv_handler)
    
    # Обработчики кнопок
    application.add_handler(CallbackQueryHandler(button_handler, pattern="^settings_"))
    application.add_handler(CallbackQueryHandler(button_handler, pattern="^back_to_menu$"))
    application.add_handler(CallbackQueryHandler(game_handler, pattern="^game_"))
    
    # Обработчик текстовых сообщений
    application.add_handler(MessageHandler(filters.TEXT & ~filters.COMMAND, text_handler))
    
    # Обработчик ошибок
    application.add_error_handler(error_handler)
    
    # Запускаем бота
    logger.info("Бот запущен...")
    application.run_polling(allowed_updates=Update.ALL_TYPES)


if __name__ == '__main__':
    main()