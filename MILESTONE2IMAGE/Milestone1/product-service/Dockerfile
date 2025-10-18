# Stage 1: Build with Gradle
FROM gradle:7.5 AS builder
WORKDIR /usr/src/app

# Copy everything (src, build.gradle.kts, settings.gradle.kts, etc.)
COPY . .

RUN gradle bootJar

# Stage 2: Run with OpenJDK
FROM openjdk:17
COPY --from=builder /usr/src/app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
