import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
}

application {
    mainClass.set("io.github.honey.HoneyLauncher")
}

dependencies {
    implementation(project(":honey-frontend"))

    implementation("org.springframework.boot:spring-boot-starter")

    // mariadb integration
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    val slf4j = "2.0.17"
    implementation("org.slf4j:slf4j-simple:$slf4j")

    val javalin = "6.5.0"
    implementation("io.javalin:javalin:$javalin")
    implementation("io.javalin.community.routing:routing-dsl:$javalin")

    val jackson = "2.19.2"
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jackson")
}

tasks.withType<ShadowJar> {
    archiveFileName.set("honey-${archiveVersion.get()}.jar")
    mergeServiceFiles()
}