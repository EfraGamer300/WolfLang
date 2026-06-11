plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.8"
}

val mcVersion = project.findProperty("mcVersion")?.toString() ?: "1.21.4"
val apiVersion = project.findProperty("apiVersion")?.toString() ?: mcVersion
val javaRelease = project.findProperty("javaRelease")?.toString()?.toIntOrNull()
    ?: if ((mcVersion.split(".").getOrNull(1)?.toIntOrNull() ?: 0) >= 21) 21 else 17

group = "dev.wolfstudios.wolflang"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${mcVersion}-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")

    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.mysql:mysql-connector-j:9.2.0")
    implementation("org.xerial:sqlite-jdbc:3.47.1.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.release.set(javaRelease)
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("apiVersion" to apiVersion)
    }
}

tasks.shadowJar {
    relocate("com.zaxxer.hikari", "dev.wolfstudios.wolflang.libs.hikari")
    relocate("com.mysql.cj", "dev.wolfstudios.wolflang.libs.mysql")
    relocate("org.slf4j", "dev.wolfstudios.wolflang.libs.slf4j")
    mergeServiceFiles()
    minimize {
        exclude(dependency("org.xerial:sqlite-jdbc:.*"))
    }
    archiveFileName.set("WolfLang-${mcVersion}.jar")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
