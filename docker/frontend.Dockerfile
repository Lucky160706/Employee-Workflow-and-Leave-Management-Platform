# Build context la thu muc goc repo (xem docker-compose.yml).
# Node 22 (khong phai 20): Node 20 het han ho tro tu 04/2026, va Vite 8 yeu cau
# node ^20.19 || >=22.12 - pin "20" se dung vao ban Node 20 cu hon va build vo.
FROM node:22-alpine AS build
WORKDIR /app
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
