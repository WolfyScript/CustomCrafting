import org.gradle.accessors.dm.LibrariesForSharedLibs

val Project.sharedLibs
    get() = extensions.getByType(LibrariesForSharedLibs::class)

plugins {
    `java-library`
    kotlin("jvm")
    id("net.fabricmc.fabric-loom")
}

repositories {
    maven {
        url = uri(providers.gradleProperty("wolfyReleasesUrl").get())
        mavenContent { releasesOnly() }
    }
    maven {
        url = uri(providers.gradleProperty("wolfySnapshotsUrl").get())
        mavenContent { snapshotsOnly() }
    }
    maven(url = "https://libraries.minecraft.net/")
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.maven.apache.org/maven2/")
    mavenLocal()
    mavenCentral()
}

tasks {

}

dependencies {
    minecraft(sharedLibs.minecraft)
}
