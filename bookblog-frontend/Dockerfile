# Этап сборки
FROM node:18-alpine AS build
WORKDIR /app

# Сначала копируем только package.json для кеширования зависимостей
COPY package.json package-lock.json ./

# Устанавливаем все зависимости, включая devDependencies
RUN npm install --force

# Копируем остальные файлы
COPY . .

# Сборка проекта
RUN npm run build

# Этап запуска
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80