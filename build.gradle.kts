/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    `java-library`
    `maven-publish`
}


repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api("com.google.guava:guava:31.1-jre")
    api("com.google.code.gson:gson:2.10")
    api("org.apache.commons:commons-lang3:3.12.0")
    compileOnly("se.curity.identityserver:identityserver.sdk:8.4.1")
    compileOnly("org.slf4j:slf4j-api:2.0.3")
    compileOnly("jakarta.validation:jakarta.validation-api:3.0.0")
}

group = "com.example.curity"
version = "1.0.0-SNAPSHOT"
description = "Curity iProov Authentication Action"
java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

tasks.clean.get().finalizedBy("cleanIProov")

tasks.register<Copy>("copyIProovFrontendResources") {
    dependsOn("installIProovDeps")

    from("node_modules/@iproov/web-sdk")
    into(layout.projectDirectory.dir("src/main/resources/webroot/assets"))
}

tasks.processResources.get().dependsOn("copyIProovFrontendResources")

tasks.register<Sync>("copyDependencies") {
    from(configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("iProov"))
}

tasks.register<Copy>("copyJar") {
    val jarTask = tasks.jar.get()
    dependsOn(jarTask)

    from(jarTask.destinationDirectory)
    into(layout.buildDirectory.dir("iProov"))
}

tasks.register<GradleBuild>("buildPlugin") {
    tasks = listOf(
        "copyDependencies",
        "copyJar",
        "cleanIProov"
    )
}

tasks.register<Exec>("installIProovDeps") {
    commandLine("npm", "install", "@iproov/web-sdk")
}

tasks.register("cleanIProov"){
    doLast {
        println("Cleaning up iProov npm dependencies")
        delete("node_modules")
        delete("package.json")
        delete("package-lock.json")
        delete("src/main/resources/webroot")
    }
}
