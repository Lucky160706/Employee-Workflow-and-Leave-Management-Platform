# Build context tro ve thu muc goc repo (xem docker-compose.yml).
# Phai la Node 22 chu khong phai 20: Vite 8 doi ^20.19 || >=22.12, ma tag "20" co the
# keo ve ban Node 20 cu hon the la build vo. Node 20 cung het han ho tro tu 04/2026.
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
