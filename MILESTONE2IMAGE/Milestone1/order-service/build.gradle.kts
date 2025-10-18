plugins {
    java
    id("org.springframework.boot") version "3.2.4" // Or your actual Spring Boot version
    id("io.spring.dependency-management") version "1.1.4"
    id("jacoco") // Apply JaCoCo plugin for code coverage
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web") // Includes Jackson
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Jackson JSR310 for Java 8 Date/Time types (LocalDateTime, ZonedDateTime, etc.)
    // This is crucial for serializing/deserializing java.time objects
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Jakarta Persistence API (to support @Embeddable, @Enumerated annotations if used in domain objects)
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

    // Lombok for boilerplate code reduction
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // MapStruct for DTO/Entity mapping
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0") // For Lombok + MapStruct integration

    // Developer Tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    // developmentOnly("org.springframework.boot:spring-boot-docker-compose") // If used

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:mongodb") // For MongoDB integration testing
}

tasks.withType<JavaCompile> {
    // options.compilerArgs.add("-Xlint:all") // Enable all lint warnings
    // options.compilerArgs.add("-Xlint:-serial") // Example: disable specific lint warning for serialVersionUID
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // Run JaCoCo report after tests
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // Ensure tests run before report generation
    reports {
        xml.required.set(true) // Common for CI systems
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/test/html"))
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.40".toBigDecimal() // Adjust your desired minimum coverage
            }
        }
    }
}

// If you use a specific application-docker.yml for Docker profile
// ensure it's correctly copied to the right location or that Spring profiles handle it.
// The task below copies application-docker.yml into BOOT-INF/classes.
// Ensure `spring.profiles.active=docker` is set in your Docker environment.
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE // Good practice

    // This ensures application-docker.yml is included if present.
    // If your main application.yml contains docker specific configs activated by profile, this might not be needed.
    from("src/main/resources") {
        include("application-docker.yml") // Only include if it exists to avoid build error
        into("BOOT-INF/classes")
    }
}