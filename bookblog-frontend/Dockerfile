# Сборка
FROM node:18-alpine AS builder
WORKDIR /app
COPY frontend/package*.json .
RUN npm install
COPY frontend .
RUN npm run build

# Запуск
FROM nginx:alpine
COPY --from=builder /app/build /usr/share/nginx/html
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
