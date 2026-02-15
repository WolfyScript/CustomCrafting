import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

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
    implementation(libs.scafall.loader)
    implementation(libs.jackson.kotlin)
    api(libs.protocollib)
    api(libs.bstats)
    api(libs.scafall.spigot.api)
    implementation(libs.caffeine)
    compileOnly(libs.mythic.dist)
    compileOnly(libs.papermc.paper)
    compileOnly(libs.mojang.authlib)
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.netty.all)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.oraxen)
    compileOnly(libs.nbtapi)
    compileOnly(libs.bundles.exposed)
    compileOnly(libs.bundles.database.drivers)
}

fun archiveName() = "${project.rootProject.name}-${project.version}-spigot-${libs.versions.minecraft.get()}"

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        artifact(file("$rootDir/gradle.properties"))
    }
}
