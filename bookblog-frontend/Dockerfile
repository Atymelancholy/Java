# Используем легковесный образ
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Копируем только собранный JAR
COPY target/BookBlogTest-*.jar app.jar

# Оптимизированные параметры JVM
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

# Точка входа
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
