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
    alias(libs.plugins.devtools.docker.minecraft)
    id("build.settings.default")
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
    paperweight.paperDevBundle(libs.versions.papermc.get())
}

tasks {
    shadowJar {
        archiveFileName = "customcrafting-spigot-mojmap.jar"

        dependencies {
            include(project(":common"))

            include(dependency(libs.typesafe.config))
            include(dependency(libs.jackson.kotlin))
            include(dependency(libs.jackson.dataformat.hocon))
            include(dependency(libs.jackson.core))
            include(dependency(libs.jackson.annotations))
            include(dependency(libs.jackson.databind))
            include(dependency(libs.caffeine))
            include(dependency(libs.jetbrains.annotations))
            include(dependency(libs.bstats))
        }
        metaInf.duplicatesStrategy = DuplicatesStrategy.FAIL

        relocate("org.bstats", "com.wolfyscript.customcrafting.bukkit.metrics")
    }
    assemble {
        dependsOn(reobfJar)
    }
    reobfJar {
        outputJar.set(layout.buildDirectory.file("libs/customcrafting-spigot-reobf.jar"))
    }
    register<Copy>("createInnerJar") {
        mustRunAfter(reobfJar)
        dependsOn(reobfJar)
        from(reobfJar)
        into(layout.buildDirectory.file("inner"))
        rename { "customcrafting-spigot.innerjar" }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        artifact(file("$rootDir/gradle.properties"))
    }
}
