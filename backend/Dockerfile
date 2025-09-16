# Стадия сборки
FROM maven:3.9-eclipse-temurin-17 AS build

# Установка рабочей директории
WORKDIR /app

# Копирование pom.xml для кеширования зависимостей
COPY rfc-service/pom.xml .

# Загрузка зависимостей (кешируется если pom.xml не изменился)
RUN mvn dependency:go-offline -B

# Копирование исходного кода
COPY rfc-service/src ./src

# Сборка приложения
RUN mvn clean package -DskipTests -B

# Стадия запуска
FROM eclipse-temurin:17-jre-jammy

# Создание пользователя для безопасности
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Установка рабочей директории
WORKDIR /app

# Копирование собранного JAR файла из стадии сборки
COPY --from=build /app/target/*.jar app.jar

# Изменение владельца файла
RUN chown spring:spring app.jar

# Переключение на пользователя spring
USER spring

# Открытие порта
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
