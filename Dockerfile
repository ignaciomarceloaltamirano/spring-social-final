FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY /target/*.jar java-app.jar
ENTRYPOINT ["java", "-jar","java-app.jar", "--spring.config.location=classpath:application.properties,classpath:application-docker.properties"]

