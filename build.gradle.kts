plugins {
    `java-library`
    application
}

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "application")

    group = "io.github.honey"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

        withJavadocJar()
        withSourcesJar()
    }
}