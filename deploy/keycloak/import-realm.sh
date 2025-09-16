#!/bin/bash

# Скрипт для автоматического импорта Realm в Keycloak
# Ожидает запуска Keycloak и импортирует cab-realm.json

set -e

KEYCLOAK_URL="http://localhost:8081"
ADMIN_USER="admin"
ADMIN_PASSWORD="admin"
REALM_FILE="/opt/keycloak/data/import/cab-realm.json"
REALM_NAME="cab-realm"

echo "Ожидание запуска Keycloak..."

# Функция для проверки доступности Keycloak
wait_for_keycloak() {
    local max_attempts=60
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s -f "${KEYCLOAK_URL}/health/ready" >/dev/null 2>&1; then
            echo "Keycloak запущен и готов к работе!"
            return 0
        fi
        
        echo "Попытка ${attempt}/${max_attempts}: Keycloak еще не готов, ожидание..."
        sleep 5
        ((attempt++))
    done
    
    echo "Ошибка: Keycloak не запустился в течение 5 минут"
    return 1
}

# Функция для получения access token
get_access_token() {
    local token_response
    token_response=$(curl -s -X POST "${KEYCLOAK_URL}/realms/master/protocol/openid-connect/token" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "username=${ADMIN_USER}" \
        -d "password=${ADMIN_PASSWORD}" \
        -d "grant_type=password" \
        -d "client_id=admin-cli")
    
    echo "$token_response" | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4
}

# Функция для проверки существования Realm
realm_exists() {
    local access_token=$1
    local response
    response=$(curl -s -w "%{http_code}" -o /dev/null \
        -H "Authorization: Bearer ${access_token}" \
        "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}")
    
    [ "$response" = "200" ]
}

# Функция для импорта Realm
import_realm() {
    local access_token=$1
    
    echo "Импорт Realm из файла: ${REALM_FILE}"
    
    local response
    response=$(curl -s -w "%{http_code}" -o /tmp/import_response.json \
        -X POST "${KEYCLOAK_URL}/admin/realms" \
        -H "Authorization: Bearer ${access_token}" \
        -H "Content-Type: application/json" \
        -d "@${REALM_FILE}")
    
    if [ "$response" = "201" ]; then
        echo "Realm '${REALM_NAME}' успешно импортирован!"
        return 0
    else
        echo "Ошибка при импорте Realm. HTTP код: ${response}"
        echo "Ответ сервера:"
        cat /tmp/import_response.json
        return 1
    fi
}

# Основная логика
main() {
    echo "Начало процесса импорта Realm для CAB приложения"
    
    # Ожидание запуска Keycloak
    if ! wait_for_keycloak; then
        exit 1
    fi
    
    # Получение access token
    echo "Получение access token..."
    ACCESS_TOKEN=$(get_access_token)
    
    if [ -z "$ACCESS_TOKEN" ]; then
        echo "Ошибка: Не удалось получить access token"
        exit 1
    fi
    
    echo "Access token получен успешно"
    
    # Проверка существования Realm
    if realm_exists "$ACCESS_TOKEN"; then
        echo "Realm '${REALM_NAME}' уже существует, пропускаем импорт"
        exit 0
    fi
    
    # Проверка существования файла Realm
    if [ ! -f "$REALM_FILE" ]; then
        echo "Ошибка: Файл Realm не найден: ${REALM_FILE}"
        exit 1
    fi
    
    # Импорт Realm
    if import_realm "$ACCESS_TOKEN"; then
        echo "Импорт Realm завершен успешно!"
    else
        echo "Ошибка при импорте Realm"
        exit 1
    fi
}

# Запуск основной функции
main "$@"
