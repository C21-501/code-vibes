#!/usr/bin/env python3
"""
Скрипт для первичной настройки Telegram бота
"""

import os
import sys
import subprocess
from pathlib import Path


def print_header(text):
    """Вывод заголовка"""
    print("\n" + "=" * 50)
    print(f"  {text}")
    print("=" * 50 + "\n")


def check_python_version():
    """Проверка версии Python"""
    print("🔍 Проверка версии Python...")
    version = sys.version_info
    if version.major < 3 or (version.major == 3 and version.minor < 8):
        print(f"❌ Требуется Python 3.8 или выше. У вас: {version.major}.{version.minor}")
        return False
    print(f"✅ Python {version.major}.{version.minor} - OK")
    return True


def create_virtual_environment():
    """Создание виртуального окружения"""
    print("\n🔧 Создание виртуального окружения...")
    
    if Path("venv").exists():
        response = input("⚠️  Виртуальное окружение уже существует. Пересоздать? (y/n): ")
        if response.lower() != 'y':
            print("⏩ Пропускаем создание окружения")
            return True
        
        # Удаляем старое окружение
        import shutil
        shutil.rmtree("venv")
    
    try:
        subprocess.run([sys.executable, "-m", "venv", "venv"], check=True)
        print("✅ Виртуальное окружение создано")
        return True
    except subprocess.CalledProcessError:
        print("❌ Ошибка при создании виртуального окружения")
        return False


def install_dependencies():
    """Установка зависимостей"""
    print("\n📦 Установка зависимостей...")
    
    # Определяем путь к pip в виртуальном окружении
    if os.name == 'nt':  # Windows
        pip_path = Path("venv/Scripts/pip")
    else:  # Unix/Linux/MacOS
        pip_path = Path("venv/bin/pip")
    
    if not pip_path.exists():
        print("❌ pip не найден в виртуальном окружении")
        return False
    
    try:
        # Обновляем pip
        subprocess.run([str(pip_path), "install", "--upgrade", "pip"], check=True)
        
        # Устанавливаем зависимости
        subprocess.run([str(pip_path), "install", "-r", "requirements.txt"], check=True)
        print("✅ Зависимости установлены")
        return True
    except subprocess.CalledProcessError:
        print("❌ Ошибка при установке зависимостей")
        return False


def setup_env_file():
    """Настройка файла .env"""
    print("\n⚙️  Настройка конфигурации...")
    
    env_path = Path(".env")
    env_example_path = Path(".env.example")
    
    if env_path.exists():
        response = input("⚠️  Файл .env уже существует. Перезаписать? (y/n): ")
        if response.lower() != 'y':
            print("⏩ Используем существующий .env")
            return True
    
    if not env_example_path.exists():
        print("❌ Файл .env.example не найден")
        return False
    
    print("\n📝 Для работы бота необходим токен от @BotFather")
    print("   1. Откройте Telegram и найдите @BotFather")
    print("   2. Отправьте команду /newbot")
    print("   3. Следуйте инструкциям для создания бота")
    print("   4. Скопируйте полученный токен\n")
    
    token = input("🔑 Введите токен бота: ").strip()
    
    if not token:
        print("❌ Токен не может быть пустым")
        return False
    
    # Читаем пример конфигурации
    with open(env_example_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Заменяем токен
    content = content.replace("your_bot_token_here", token)
    
    # Опционально: запрашиваем ID администратора
    admin_id = input("\n👤 Введите ваш Telegram ID (опционально, нажмите Enter для пропуска): ").strip()
    if admin_id:
        content = content.replace("your_telegram_user_id", admin_id)
    
    # Сохраняем .env
    with open(env_path, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print("✅ Файл .env создан")
    return True


def create_directories():
    """Создание необходимых директорий"""
    print("\n📁 Создание директорий...")
    
    dirs = ["logs", "downloads", "temp", "bot_data"]
    
    for dir_name in dirs:
        dir_path = Path(dir_name)
        if not dir_path.exists():
            dir_path.mkdir(parents=True)
            print(f"   ✅ Создана директория: {dir_name}")
        else:
            print(f"   ⏩ Директория уже существует: {dir_name}")
    
    return True


def print_instructions():
    """Вывод инструкций по запуску"""
    print_header("🎉 Установка завершена!")
    
    print("📌 Для запуска бота используйте:\n")
    
    if os.name == 'nt':  # Windows
        print("   1. Активация виртуального окружения:")
        print("      .\\venv\\Scripts\\activate\n")
        print("   2. Запуск бота:")
        print("      python bot.py")
    else:  # Unix/Linux/MacOS
        print("   1. Активация виртуального окружения:")
        print("      source venv/bin/activate\n")
        print("   2. Запуск бота:")
        print("      python bot.py")
    
    print("\n📚 Полезные команды:")
    print("   - Остановка бота: Ctrl+C")
    print("   - Деактивация окружения: deactivate")
    print("   - Просмотр логов: tail -f bot.log")
    
    print("\n💡 Совет: Добавьте бота в группу или канал для")
    print("   тестирования групповых функций!")


def main():
    """Главная функция"""
    print_header("🤖 Установка Telegram Bot")
    
    # Проверяем версию Python
    if not check_python_version():
        print("\n❌ Установка прервана")
        return 1
    
    # Создаем виртуальное окружение
    if not create_virtual_environment():
        print("\n❌ Установка прервана")
        return 1
    
    # Устанавливаем зависимости
    if not install_dependencies():
        print("\n❌ Установка прервана")
        return 1
    
    # Настраиваем .env
    if not setup_env_file():
        print("\n❌ Установка прервана")
        return 1
    
    # Создаем директории
    create_directories()
    
    # Выводим инструкции
    print_instructions()
    
    # Предлагаем запустить бота
    print("\n" + "=" * 50)
    response = input("\n🚀 Запустить бота сейчас? (y/n): ")
    
    if response.lower() == 'y':
        print("\n▶️  Запуск бота...\n")
        
        if os.name == 'nt':  # Windows
            python_path = Path("venv/Scripts/python")
        else:  # Unix/Linux/MacOS
            python_path = Path("venv/bin/python")
        
        try:
            subprocess.run([str(python_path), "bot.py"])
        except KeyboardInterrupt:
            print("\n\n⏹️  Бот остановлен")
        except Exception as e:
            print(f"\n❌ Ошибка при запуске: {e}")
    
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except KeyboardInterrupt:
        print("\n\n⚠️  Установка прервана пользователем")
        sys.exit(1)
    except Exception as e:
        print(f"\n❌ Непредвиденная ошибка: {e}")
        sys.exit(1)