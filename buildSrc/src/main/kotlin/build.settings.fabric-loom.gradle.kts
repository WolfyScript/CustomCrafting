import org.gradle.accessors.dm.LibrariesForLibs

val Project.libs
    get() = extensions.getByType(LibrariesForLibs::class)

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm")
    id("net.fabricmc.fabric-loom-remap")
}

repositories {
    mavenCentral()
    maven(url = "https://artifacts.wolfyscript.com/artifactory/gradle-dev")
    maven(url = "https://libraries.minecraft.net/")
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.maven.apache.org/maven2/")
    mavenLocal()
}

tasks {
    // Disable remapping without having to disable the tasks
    // This will get shaded into other platforms that then use their specific remapper instead.
    // Additionally, this will be a public api, which should work across all platforms.
    remapJar {
        targetNamespace = "named"
    }
    remapSourcesJar {
        targetNamespace = "named"
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
}
