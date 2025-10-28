FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml ./
COPY infrastructure/pom.xml infrastructure/
COPY domain/pom.xml domain/
COPY api/pom.xml api/
COPY service/pom.xml service/
COPY test/pom.xml test/
COPY coverage/pom.xml coverage/

RUN mvn dependency:go-offline -B

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/api/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
