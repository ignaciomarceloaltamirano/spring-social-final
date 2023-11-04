FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine
COPY --from=build /target/social_final.jar java-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","java-app.jar", "--spring.config.location=classpath:application.properties,classpath:application-docker.properties"]
#COPY /target/spring_social_app.jar java-app.jar
