FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . /app
CMD ["java", "-jar", "target/task-tracker-api-0.0.1-SNAPSHOT.jar"]