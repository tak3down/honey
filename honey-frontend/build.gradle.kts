import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node") version "7.1.0"
}

node {
    version.set("22.16.0")
    download.set(true)
}

val buildTask = tasks.register<NpmTask>("buildFrontend") {
    args.set(listOf("run", "build"))
    dependsOn(tasks.npmInstall)

    inputs.dir(project.fileTree("app"))
    inputs.dir(project.fileTree("public"))
    inputs.dir("node_modules")
    inputs.files("next.config.ts", "next-env.d.ts", "tsconfig.json", "postcss.config.mjs")
    outputs.dir(project.projectDir.resolve("out"))

}

val copyFrontendTask = tasks.register<Copy>("copyFrontendToStatic") {
    dependsOn(buildTask)
    from(project.projectDir.resolve("out"))
    into(project.layout.buildDirectory.dir("resources/main/static"))
}

tasks.processResources {
    dependsOn(copyFrontendTask)
}