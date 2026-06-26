import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.4.0"
    kotlin("plugin.serialization") version "2.4.0"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor 3.x — HTTP-клиент
    implementation("io.ktor:ktor-client-core:3.5.0")
    implementation("io.ktor:ktor-client-cio:3.5.0")
    implementation("io.ktor:ktor-client-content-negotiation:3.5.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.5.0")

    // kotlinx
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0") // сверь актуальную

    // Логирование
    implementation("org.slf4j:slf4j-simple:2.0.18")

    // Официальный Kotlin-драйвер MongoDB
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.8.0")
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