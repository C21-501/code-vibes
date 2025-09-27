# Стадия сборки
FROM gradle:8.5-jdk17 AS build

# Установка рабочей директории
WORKDIR /app

# Копирование Gradle wrapper и файлов конфигурации для кеширования зависимостей
COPY gradle ./gradle
COPY gradlew ./gradlew
COPY gradlew.bat ./gradlew.bat
COPY settings.gradle ./
COPY build.gradle ./

# Копирование исходного кода
COPY src ./src

# Сборка приложения
RUN ./gradlew clean build -x test --no-daemon

# Стадия запуска
FROM eclipse-temurin:17-jre-jammy

# Создание пользователя для безопасности
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Установка рабочей директории
WORKDIR /app

# Копирование собранного JAR файла из стадии сборки
COPY --from=build /app/build/libs/*.jar app.jar

# Изменение владельца файла
RUN chown spring:spring app.jar

# Переключение на пользователя spring
USER spring

# Открытие порта
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-Dconsole.encoding=UTF-8", "-jar", "/app/app.jar"]
