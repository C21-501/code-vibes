"""
Модуль с клавиатурами для Telegram бота
"""

from telegram import ReplyKeyboardMarkup, InlineKeyboardMarkup, InlineKeyboardButton, KeyboardButton


def get_main_keyboard():
    """Главная клавиатура бота"""
    keyboard = [
        ["📊 Статистика", "ℹ️ О боте"],
        ["🎮 Игра", "⚙️ Настройки"],
    ]
    return ReplyKeyboardMarkup(
        keyboard,
        resize_keyboard=True,
        one_time_keyboard=False
    )


def get_inline_keyboard():
    """Inline клавиатура для главного меню"""
    keyboard = [
        [
            InlineKeyboardButton("📱 Канал", url="https://t.me/channel"),
            InlineKeyboardButton("💬 Группа", url="https://t.me/group")
        ],
        [
            InlineKeyboardButton("📝 Написать разработчику", url="https://t.me/username")
        ],
        [
            InlineKeyboardButton("⭐ Оценить бота", callback_data="rate_bot")
        ]
    ]
    return InlineKeyboardMarkup(keyboard)


def get_settings_keyboard():
    """Клавиатура настроек"""
    keyboard = [
        [
            InlineKeyboardButton("🔔 Уведомления", callback_data="settings_notifications"),
            InlineKeyboardButton("🌐 Язык", callback_data="settings_language")
        ],
        [
            InlineKeyboardButton("🎨 Тема", callback_data="settings_theme"),
            InlineKeyboardButton("⏰ Часовой пояс", callback_data="settings_timezone")
        ],
        [
            InlineKeyboardButton("🔐 Конфиденциальность", callback_data="settings_privacy")
        ],
        [
            InlineKeyboardButton("◀️ Назад", callback_data="back_to_menu")
        ]
    ]
    return InlineKeyboardMarkup(keyboard)


def get_yes_no_keyboard():
    """Клавиатура Да/Нет"""
    keyboard = [
        [
            InlineKeyboardButton("✅ Да", callback_data="yes"),
            InlineKeyboardButton("❌ Нет", callback_data="no")
        ]
    ]
    return InlineKeyboardMarkup(keyboard)


def get_cancel_keyboard():
    """Клавиатура с кнопкой отмены"""
    keyboard = [
        [InlineKeyboardButton("❌ Отмена", callback_data="cancel")]
    ]
    return InlineKeyboardMarkup(keyboard)


def get_share_keyboard():
    """Клавиатура для отправки контакта и локации"""
    keyboard = [
        [
            KeyboardButton("📱 Отправить контакт", request_contact=True),
            KeyboardButton("📍 Отправить локацию", request_location=True)
        ],
        ["◀️ Назад"]
    ]
    return ReplyKeyboardMarkup(
        keyboard,
        resize_keyboard=True,
        one_time_keyboard=True
    )


def get_admin_keyboard():
    """Клавиатура администратора"""
    keyboard = [
        ["📊 Статистика", "👥 Пользователи"],
        ["📢 Рассылка", "⚙️ Настройки"],
        ["📝 Логи", "🔄 Перезапуск"],
        ["◀️ В главное меню"]
    ]
    return ReplyKeyboardMarkup(
        keyboard,
        resize_keyboard=True,
        one_time_keyboard=False
    )


def get_pagination_keyboard(page: int, total_pages: int, callback_prefix: str):
    """Клавиатура для пагинации"""
    keyboard = []
    
    # Кнопки навигации
    nav_buttons = []
    
    if page > 1:
        nav_buttons.append(
            InlineKeyboardButton("⬅️", callback_data=f"{callback_prefix}_prev_{page}")
        )
    
    nav_buttons.append(
        InlineKeyboardButton(f"{page}/{total_pages}", callback_data=f"{callback_prefix}_page_{page}")
    )
    
    if page < total_pages:
        nav_buttons.append(
            InlineKeyboardButton("➡️", callback_data=f"{callback_prefix}_next_{page}")
        )
    
    keyboard.append(nav_buttons)
    
    # Кнопка закрытия
    keyboard.append([
        InlineKeyboardButton("❌ Закрыть", callback_data=f"{callback_prefix}_close")
    ])
    
    return InlineKeyboardMarkup(keyboard)


def get_rating_keyboard():
    """Клавиатура для оценки"""
    keyboard = []
    
    # Звезды от 1 до 5
    stars_row = []
    for i in range(1, 6):
        stars_row.append(
            InlineKeyboardButton(
                "⭐" * i,
                callback_data=f"rate_{i}"
            )
        )
    
    keyboard.append(stars_row[:3])  # Первые 3 звезды
    keyboard.append(stars_row[3:])  # Последние 2 звезды
    
    return InlineKeyboardMarkup(keyboard)


def get_confirmation_keyboard(action: str):
    """Клавиатура подтверждения действия"""
    keyboard = [
        [
            InlineKeyboardButton(
                "✅ Подтвердить",
                callback_data=f"confirm_{action}"
            ),
            InlineKeyboardButton(
                "❌ Отмена",
                callback_data=f"cancel_{action}"
            )
        ]
    ]
    return InlineKeyboardMarkup(keyboard)