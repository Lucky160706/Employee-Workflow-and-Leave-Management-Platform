# Build context tro ve thu muc goc repo (xem docker-compose.yml), nho vay Dockerfile
# nam trong docker/ van COPY duoc code ben backend/.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY backend/pom.xml .
# Tai dependency truoc khi COPY source. Docker cache lai layer nay nen nhung lan build
# sau, neu chi sua code thi khong phai tai lai ca dong dependency.
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
