# Этап сборки с оптимизацией памяти
FROM maven:3.8.6-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
# Кэшируем зависимости
RUN mvn -B dependency:go-offline -Dmaven.test.skip=true

COPY src ./src
# Сборка с ограничением памяти
RUN mvn -B package -DskipTests \
    -Dmaven.javadoc.skip=true \
    -Dmaven.compile.fork=true \
    -T 1C \
    -Dspring-boot.repackage.mainClass=com.example.BookBlogApplication

# Финальный образ
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
