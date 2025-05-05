# Use official OpenJDK image as base
FROM eclipse-temurin:17-jdk-alpine

# Set environment variable
ENV JAVA_OPTS=""

# Set working directory
WORKDIR /app

# Copy the JAR file (you should build it first using `mvn clean package`)
COPY target/operations-service-1.0.0.jar app.jar

# Expose port (make sure this matches your Eureka `server.port`)
EXPOSE 8081

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
