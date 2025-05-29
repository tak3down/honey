import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
}

application {
    mainClass.set("io.github.honey.Honey")
}

dependencies {
    implementation(project(":honey-frontend"))
    implementation("org.springframework.boot:spring-boot-starter-web")
}

tasks.withType<ShadowJar> {
    archiveFileName.set("honey-${archiveVersion.get()}.jar")
    mergeServiceFiles()
}