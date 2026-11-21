import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

val Project.sharedLibs
    get() = extensions.getByType(org.gradle.accessors.dm.LibrariesForSharedLibs::class)
val Project.libs
    get() = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

plugins {
    kotlin("jvm")
    id("io.papermc.paperweight.userdev")
    `java-library`
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
    maven(url = "https://repo.codemc.io/repository/maven-public/") {
        content { includeGroup("de.tr7zw") }
    }

    maven(url = "https://repo.papermc.io/repository/maven-public/")
    maven(url = "https://repo.maven.apache.org/maven2/")
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(project(":core:core-api"))
    implementation(project(":core:core-common"))
    implementation(kotlin("reflect"))
    implementation(libs.scafall.wrappers.spigot)
    implementation(sharedLibs.jackson.kotlin)
    implementation(sharedLibs.caffeine)
    compileOnly(sharedLibs.papermc.paper)
    compileOnly(sharedLibs.mojang.authlib)
    compileOnly(sharedLibs.jetbrains.annotations)
    compileOnly(sharedLibs.netty.all)
    compileOnly(sharedLibs.bundles.exposed)
    compileOnly(sharedLibs.bundles.database.drivers)

    compileOnly(sharedLibs.item.nbt.api)

    paperweight.paperDevBundle(sharedLibs.versions.papermc.get())
}

fun archiveName() = "${project.rootProject.name}-${project.version}-spigot-${sharedLibs.versions.minecraft.get()}"
