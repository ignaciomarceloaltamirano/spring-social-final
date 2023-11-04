FROM openjdk:17-jdk-alpine
RUN mvn clean package -DskipTests
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar","java-app.jar", "--spring.config.location=classpath:application.properties,classpath:application-docker.properties"]