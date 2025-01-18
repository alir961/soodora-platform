FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml ./
COPY infrastructure/pom.xml infrastructure/
COPY domain/pom.xml domain/
COPY rest/pom.xml rest/
COPY application/pom.xml application/
COPY test/pom.xml test/

RUN mvn dependency:go-offline -B

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/rest/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
