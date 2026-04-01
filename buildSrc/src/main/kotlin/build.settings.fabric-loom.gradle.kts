import org.gradle.accessors.dm.LibrariesForSharedLibs

val Project.sharedLibs
    get() = extensions.getByType(LibrariesForSharedLibs::class)

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm")
    id("net.fabricmc.fabric-loom")
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

}

dependencies {
    minecraft(sharedLibs.minecraft)
}
