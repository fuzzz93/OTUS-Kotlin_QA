import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.4.0"
    kotlin("plugin.serialization") version "2.4.0"
    application
    id("io.qameta.allure") version "2.12.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:3.5.0")
    implementation("io.ktor:ktor-client-cio:3.5.0")
    implementation("io.ktor:ktor-client-content-negotiation:3.5.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.5.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")

    implementation("org.slf4j:slf4j-simple:2.0.18")

    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.8.0")

    implementation("org.xerial:sqlite-jdbc:3.47.1.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.mockk:mockk:1.13.16")
    testImplementation("io.qameta.allure:allure-junit5:2.29.1")
}

kotlin {
    jvmToolchain(22)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_22)
    }
}

application {
    mainClass.set("Homeworks.HomeworkFiveKt")
}

allure {
    version.set("2.29.1")
    adapter {
        aspectjWeaver.set(true)
        autoconfigure.set(true)
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
