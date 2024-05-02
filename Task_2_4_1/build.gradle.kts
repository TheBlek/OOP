plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    jacoco
    groovy
    application
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
    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    // Use JUnit test framework.
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // This dependency is used by the application.
    implementation("com.google.guava:guava:32.1.1-jre")
    // https://mvnrepository.com/artifact/org.eclipse.jgit/org.eclipse.jgit
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.9.0.202403050737-r")
    // https://mvnrepository.com/artifact/org.gradle/gradle-tooling-api
    implementation("org.gradle:gradle-tooling-api:7.3-20210825160000+0000")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.13")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation("org.apache.commons:commons-lang3:3.14.0")
    // https://mvnrepository.com/artifact/org.codehaus.groovy/groovy-all
    implementation("org.codehaus.groovy:groovy-all:3.0.21")
    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    implementation("org.jsoup:jsoup:1.17.2")
    // https://mvnrepository.com/artifact/org.thymeleaf/thymeleaf
    implementation("org.thymeleaf:thymeleaf:3.1.2.RELEASE")
    // https://mvnrepository.com/artifact/com.puppycrawl.tools/checkstyle
    implementation("com.puppycrawl.tools:checkstyle:10.15.0")
    implementation("com.github.stefanbirkner:system-lambda:1.2.1")
}

sourceSets {
    main {
        groovy {
            // override the default locations, rather than adding additional ones
            srcDirs(listOf("src/main/groovy", "src/main/java"))
        }
        java {
            srcDirs() // don't compile Java code twice
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("ru.nsu.kuklin.dsl.Application")
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
