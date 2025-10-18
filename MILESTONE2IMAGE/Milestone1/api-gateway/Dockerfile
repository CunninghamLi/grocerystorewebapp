# In Milestone1/api-gateway/Dockerfile

# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy Gradle wrapper files first to leverage Docker layer caching for dependencies
COPY gradlew .
COPY gradle ./gradle

# Copy build configuration files
COPY build.gradle.kts .
COPY settings.gradle.kts .

# --- ADD THIS LINE TO COPY YOUR SOURCE CODE ---
COPY src ./src
# --- END OF ADDITION ---

# Grant execution rights to gradlew
RUN chmod +x ./gradlew

# Build the application
# Now Gradle will find your source code in /app/src/
RUN ./gradlew bootJar --no-daemon

# Diagnostic step: (Optional - uncomment to verify JAR name and location)
# RUN echo "Contents of /app/build/libs/ after Gradle build:" && ls -l /app/build/libs/

# Move the generated JAR file to /app/app.jar for the ENTRYPOINT
RUN mv /app/build/libs/api-gateway-0.0.1-SNAPSHOT.jar /app/app.jar

# Make port 8080 available
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","/app/app.jar"]