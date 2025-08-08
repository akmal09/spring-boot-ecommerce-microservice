# Use official OpenJDK image as base
FROM eclipse-temurin:17-jdk-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Download Datadog APM agent
RUN curl -Lo dd-java-agent.jar https://dtdg.co/latest-java-tracer

# Set environment variable
ENV JAVA_OPTS=""
ENV DD_SERVICE=operations-service
ENV DD_ENV=development
ENV DD_VERSION=1.0.0

# Set working directory
WORKDIR /app

# Copy the JAR file (you should build it first using `mvn clean package`)
COPY target/operations-service-1.0.0.jar app.jar

# Expose port (make sure this matches your Eureka `server.port`)
EXPOSE 8082

# Run the application with Datadog APM
ENTRYPOINT ["sh", "-c", "java -javaagent:dd-java-agent.jar $JAVA_OPTS -jar app.jar"]
