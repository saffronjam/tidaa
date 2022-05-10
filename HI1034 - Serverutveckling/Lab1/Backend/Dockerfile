FROM openjdk:17-alpine
ADD target/Backend-0.0.1-SNAPSHOT.jar backend-docker.jar
ENTRYPOINT ["java", "-jar", "backend-docker.jar"]