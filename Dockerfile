# ---------- Build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Baixa as dependências em uma camada separada para aproveitar o cache do Docker
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests

# ---------- Runtime ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN useradd --system --no-create-home alerta360
COPY --from=build /app/target/alerta360-*.jar app.jar
USER alerta360

EXPOSE 3000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
