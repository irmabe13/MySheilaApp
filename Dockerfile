# ---- Build stage ----
FROM gradle:8.8-jdk21-alpine AS build
WORKDIR /app

COPY build.gradle settings.gradle gradle.properties* gradlew ./
COPY gradle ./gradle

# Télécharger le wrapper et deps
RUN ./gradlew --no-daemon dependencies || true

# Copier le code
COPY src ./src

# Build du JAR Spring Boot
RUN ./gradlew --no-daemon clean bootJar

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
# copie le jar (nom flexible avec wildcard)
COPY --from=build /app/build/libs/*SNAPSHOT.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
