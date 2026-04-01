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
    `maven-publish`
}

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://repo.codemc.io/repository/maven-public/")
    maven(url = "https://repo.papermc.io/repository/maven-public/")
    maven(url = "https://artifacts.wolfyscript.com/artifactory/gradle-dev")
    maven(url = "https://repo.dmulloy2.net/repository/public/")
    maven(url = "https://repo.maven.apache.org/maven2/")
    maven(url = "https://mvn.lumine.io/repository/maven-public/")
    maven(url = "https://repo.oraxen.com/releases")
    maven(url = "https://repo.extendedclip.com/releases/")
    maven(url = "https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    implementation(project(":core:core-api"))
    implementation(project(":core:core-common"))
    implementation(kotlin("reflect"))
    api(sharedLibs.scafall.spigot)
    implementation(sharedLibs.scafall.loader)
    implementation(sharedLibs.jackson.kotlin)
    implementation(sharedLibs.caffeine)
    compileOnly(sharedLibs.papermc.paper)
    compileOnly(sharedLibs.mojang.authlib)
    compileOnly(sharedLibs.jetbrains.annotations)
    compileOnly(sharedLibs.netty.all)
    compileOnly(sharedLibs.bundles.exposed)
    compileOnly(sharedLibs.bundles.database.drivers)

    api(libs.protocollib)
    api(libs.bstats)
    compileOnly(libs.mythic.dist)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.oraxen)
    compileOnly(libs.nbtapi)

    paperweight.paperDevBundle(sharedLibs.versions.papermc.get())
}

fun archiveName() = "${project.rootProject.name}-${project.version}-spigot-${sharedLibs.versions.minecraft.get()}"

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        artifact(file("$rootDir/gradle.properties"))
    }
}
