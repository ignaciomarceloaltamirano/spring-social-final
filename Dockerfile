FROM openjdk:17-jdk-alpine
RUN #mvn clean package -DskipTests
COPY . .

COPY --from=build /target/social_final.jar java-app.jar
ENTRYPOINT ["java", "-jar","java-app.jar", "--spring.config.location=classpath:application.properties,classpath:application-docker.properties"]