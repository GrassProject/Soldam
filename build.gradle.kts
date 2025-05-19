import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.1.21"
    id("com.gradleup.shadow") version "9.0.0-beta10"
}

group = "com.github.soldam"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") // Paper
    maven("https://repo.codemc.org/repository/maven-public/") // CommandAPI
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.21.4-R0.1-SNAPSHOT") // Paper
    implementation("dev.jorel","commandapi-bukkit-shade-mojang-mapped","10.0.0") // CommandAPI

    compileOnly(fileTree("lib") {
        include("*.jar")
    })
}

kotlin {
    jvmToolchain(21)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.withType<ShadowJar> {
    exclude("kotlin/**")
    exclude("org/**")

    relocate("dev.jorel.commandapi", "com.github.soldam.lib.commandapi")

    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }

    archiveFileName.set("${rootProject.name}-${rootProject.version}.jar")
    destinationDirectory=file("C:\\Users\\aa010\\Desktop\\SoldamPlugin\\plugins")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}