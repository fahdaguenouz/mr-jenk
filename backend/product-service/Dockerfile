# Use a lightweight Java 21 environment
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the compiled jar file from the Jenkins/Maven build into the container
COPY target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]