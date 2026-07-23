# Build context la thu muc goc repo (xem docker-compose.yml) de Dockerfile nay
# nam trong docker/ ma van COPY duoc code trong backend/.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY backend/pom.xml .
# Tai dependency truoc, tach rieng khoi COPY source - Docker cache layer nay lai,
# lan build sau chi sua code se khong phai tai lai toan bo dependency.
RUN mvn -q dependency:go-offline
COPY backend/src ./src
RUN mvn -q package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /app/target/leave-approval-system.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
