/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
    implementation(project(":api"))
    implementation(project(":common"))
    implementation(kotlin("reflect"))
    implementation(libs.scafall.loader)
    shadow(libs.bundles.jackson)
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
    compileOnly(libs.wolfyutils.spigot)
    compileOnly(libs.nbtapi)
    compileOnly(libs.bundles.exposed)
    compileOnly(libs.bundles.database.drivers)
    paperweight.paperDevBundle(libs.versions.papermc.get())
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION

tasks {
    shadowJar {
        archiveClassifier.set("")

        finalizedBy(reobfJar)

        dependencies {
            include(project(":api"))
            include(project(":common"))

            // Need to shade this for now, because when defined in plugin.yml it causes classloader issue for
            // kotlin stdlib etc., because those are transitive dependencies and cause duplicate class definitions.
//            libs.bundles.exposed.get().forEach {
//               include(dependency(it))
//            }
        }
        metaInf.duplicatesStrategy = DuplicatesStrategy.FAIL

        relocate("org.bstats", "com.wolfyscript.customcrafting.bukkit.metrics")
    }
    assemble {
        dependsOn(reobfJar)
    }
    reobfJar {
        finalizedBy(jar)
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        artifact(file("$rootDir/gradle.properties"))
    }
}

artifacts {
    archives(tasks.reobfJar)
}

bukkitPluginYaml {
    name = "CustomCrafting"
    version = project.version.toString()
    main = "com.wolfyscript.customcrafting.spigot.loader.SpigotLoaderPlugin"
    apiVersion = libs.versions.minecraft.get() // Only support the latest Minecraft version!
    authors.add("WolfyScript")
    depend.add("scafall")

    libraries.apply {
//        libs.bundles.exposed.get().forEach {
//            add(it.toString())
//        }
        libs.bundles.database.drivers.get().forEach {
            add(it.toString())
        }

        addAll(
            libs.typesafe.config.get().toString(),
            libs.caffeine.get().toString(),
            libs.bstats.get().toString(),
        )
    }
}

minecraftServers {
    libName.set("${project.name}-${version}.jar")
    servers {
        register("spigot") {
            destFileName.set("customcrafting.jar")
            version.set(libs.versions.minecraft.get())
            type.set("SPIGOT")
            extraEnv.put("BUILD_FROM_SOURCE", "true")
            imageVersion.set("java21-graalvm") // graalvm contains the jdk required to build from source
            ports.add("25569:25565")
        }
        register("paper") {
            destFileName.set("customcrafting.jar")
            version.set(libs.versions.minecraft.get())
            type.set("PAPER")
            imageVersion.set("java21")
            ports.add("25570:25565")
        }
    }
}
