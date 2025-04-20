# Этап сборки с оптимизацией кэша
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app

# 1. Копируем только POM для кэширования зависимостей
COPY pom.xml .
RUN mvn -B dependency:go-offline

# 2. Копируем исходный код
COPY src ./src

# 3. Сборка с явным указанием main class
RUN mvn -B package \
    -DskipTests \
    -Dmaven.test.skip=true \
    -Dspring-boot.repackage.mainClass=com.example.BookBlogApplication

# Финальный образ
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/BookBlog-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
