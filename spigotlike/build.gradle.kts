plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.artifactory)
    alias(libs.plugins.paperweight.userdev)
    alias(libs.plugins.resource.factory.bukkit)
    id("build.settings.default")
    id("build.docker.run")
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
    api(project(":api"))
    api(project(":common"))
    api(kotlin("reflect"))
    api(libs.scafall.loader)
    shadow(libs.bundles.jackson)
    api(libs.jackson.kotlin)
    api(libs.protocollib)
    api(libs.bstats)
    api(libs.scafall.spigot.api)
    api(libs.caffeine)
    api(libs.mythic.dist)
    api(libs.papermc.paper)
    api(libs.mojang.authlib)
    api(libs.jetbrains.annotations)
    api(libs.netty.all)
    api(libs.placeholderapi)
    api(libs.oraxen)
    api(libs.wolfyutils.spigot)
    api(libs.nbtapi)
    api(libs.bundles.exposed)
    api(libs.bundles.database.drivers)
    paperweight.paperDevBundle(libs.versions.papermc.get())
}

fun archiveName() = "${project.name}-${project.version}"

tasks {
    shadowJar {
        archiveFileName.set("${archiveName()}-mojmap.jar")

        dependencies {
            include(project(":api"))
            include(project(":common"))
        }
        metaInf.duplicatesStrategy = DuplicatesStrategy.FAIL
    }
    assemble {
        dependsOn(shadowJar)
    }
}
