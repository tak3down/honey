import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

application {
    mainClass.set("io.github.honey.HoneyLauncher")
}

dependencies {
    api(project(":honey-frontend"))

    val slf4j = "2.0.17"
    api("org.slf4j:slf4j-simple:$slf4j")

    val javalin = "6.5.0"
    api("io.javalin:javalin:$javalin")
    api("io.javalin.community.routing:routing-dsl:$javalin")

    val jackson = "2.19.0"
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jackson")
}

tasks.withType<ShadowJar> {
    archiveFileName.set("honey-${archiveVersion.get()}.jar")
    mergeServiceFiles()
}