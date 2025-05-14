# ───── Build Stage ─────
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia pom e código-fonte para otimizar cache de camadas
COPY pom.xml .
COPY src ./src

# Empacota a aplicação (skipTests para acelerar)
RUN mvn clean package -DskipTests

# ───── Runtime Stage ─────
FROM openjdk:21-slim
WORKDIR /app

# Copia o JAR gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Porta padrão do Spring Boot
EXPOSE 8080

# Executa o JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
