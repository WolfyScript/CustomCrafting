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

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.goooler.shadow)
    alias(libs.plugins.jfrog.artifactory)
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
    api(libs.protocollib)
    api(libs.bstats)
    compileOnly(libs.mythic.dist)
    compileOnly(libs.io.papermc.paper)
    compileOnly(libs.mojang.authlib)
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.netty)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.oraxen)
    compileOnly(libs.wolfyutils.spigot)
    compileOnly(libs.nbtapi)
}

java.sourceCompatibility = JavaVersion.VERSION_21

tasks.named<ProcessResources>("processResources") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    filesMatching("**/*.yml") {
        expand(project.properties)
    }
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
    dependencies {
        include(dependency("com.wolfyscript.customcrafting:.*"))
        include(dependency("${libs.bstats.get().group}:.*"))
    }

    relocate("org.bstats", "com.wolfyscript.customcrafting.bukkit.metrics")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        artifact(file("$rootDir/gradle.properties"))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
