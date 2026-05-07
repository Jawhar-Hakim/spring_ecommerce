# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy the project files
COPY . .

# Fix line endings for mvnw (in case of Windows development) and make it executable
RUN mvn clean install


# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Optimize for low memory environments (Render Free Tier: 512MB RAM)
# -Xmx384M leaves some room for the OS and non-heap memory
ENV JAVA_OPTS="-Xmx384M -Xms256M"

# Render uses the PORT environment variable
EXPOSE 8080

# Use shell form to allow environment variable expansion
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]
