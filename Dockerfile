# Use OpenJDK 17 slim image (matches Java version in pom.xml)
FROM openjdk:17-jdk-slim

# Copy pre-built JAR from target/ directory into the image as app.jar
COPY target/*.jar app.jar

# Expose the port the app runs on (matches server.port=8080)
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "/app.jar"]