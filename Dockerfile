# Building
FROM maven:3-eclipse-temurin-21 AS build

LABEL FULL_NAME="Fernando Murillo Noya"
LABEL EMAIL_MAINTAINER="fernando.murillo.noya@gmail.com"
LABEL PROJECT="API Gateway"
LABEL COURSE_WORK_GROUP="Grupo 1"

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw clean package -DskipTests

# Running
FROM eclipse-temurin:21-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

ENV SERVER_PORT=9000
ENV REDIS_HOST=redis
ENV REDIS_PORT=6379
ENV EUREKA_HOST=eureka
ENV EUREKA_PORT=8761
ENV JWT_SECRET=mySuperSecretKeyForJwtGenerationThatIsAtLeast32CharactersLong!

EXPOSE 9000

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]