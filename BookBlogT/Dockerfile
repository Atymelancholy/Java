# Многоэтапная сборка для уменьшения размера
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn/ .mvn/
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw package -DskipTests

# Финальный образ
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Копируем собранный JAR
COPY --from=builder /app/target/BookBlogTest-*.jar app.jar

# Создаем директорию для логов (соответствует application.properties)
RUN mkdir -p /app/logs

# Оптимизированные параметры JVM
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

# Переменная окружения только для пароля БД
ENV DB_PASSWORD=""

# Запуск приложения
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]