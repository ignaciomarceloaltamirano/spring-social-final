FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ./target/social-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar","java-app.jar", "--spring.config.location=classpath:application.properties,classpath:application-docker.properties"]

