#FROM openjdk:17-jdk-alpine
#WORKDIR /app
#COPY /target/*.jar java-app.jar
#
#ENTRYPOINT ["java", "-jar","java-app.jar", "--spring.config.location=classpath:application.properties,classpath:application-docker.properties"]
#

FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar java-app.jar

ENTRYPOINT ["java", "-jar", "java-app.jar", "--spring.config.location=classpath:application.properties,classpath:application-docker.properties"]
