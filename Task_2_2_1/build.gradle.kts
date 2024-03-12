plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    jacoco
}

jacoco {
    toolVersion = "0.8.9"
    reportsDirectory.set(layout.buildDirectory.dir("reports/jacoco"))
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

tasks.withType<JavaExec>() {
    standardInput = System.`in`
}

dependencies {
    // Use JUnit test framework.
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // This dependency is used by the application.
    implementation("com.google.guava:guava:32.1.1-jre")
    implementation("org.jetbrains:annotations:16.0.2")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    // Define the main class for the application.
    mainClass.set("ru.nsu.kuklin.pizzeria.Main")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}