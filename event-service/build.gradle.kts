plugins {
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    java
}

group = "ca.gbc.comp3095"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {

    // --- Spring Boot Core ---
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")  // WebClient

    // --- Database ---
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("org.postgresql:postgresql")

    // --- Kafka ---
    implementation("org.springframework.kafka:spring-kafka")

    // --- Resilience4j (CircuitBreaker + Retry) ---
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.2.0")

    // --- Lombok (optional but helpful) ---
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    // --- Testing ---
    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // --- Testcontainers ---
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.20.3"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
}

tasks.test {
    useJUnitPlatform()
}
