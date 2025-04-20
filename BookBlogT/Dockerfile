# Этап сборки с оптимизацией
FROM maven:3.8.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Копируем только pom.xml для кэширования зависимостей
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Копируем исходный код
COPY src ./src

# Сборка с явным указанием main class
RUN mvn -B package -DskipTests \
    -Dspring-boot.repackage.mainClass=com.example.BookBlogApplication \
    -Dmaven.test.skip=true \
    -Dmaven.javadoc.skip=true

# Финальный образ
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/BookBlogTest-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
