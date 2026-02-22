# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Instala curl para healthchecks se necessário
RUN apk add --no-cache curl

COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Configurações de performance para Java 21 e Virtual Threads
# Usamos as flags que você já validou no seu ambiente
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
