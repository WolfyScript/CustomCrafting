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
    id("io.github.goooler.shadow") version "8.1.7"
    id("com.wolfyscript.devtools.docker.minecraft_servers") version "2.0-SNAPSHOT"
    id("com.jfrog.artifactory") version "5.2.0"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven (url = "https://repo.codemc.io/repository/maven-public/")
    maven(url = "https://repo.papermc.io/repository/maven-public/")
    maven(url = "https://artifacts.wolfyscript.com/artifactory/gradle-dev")
    maven(url = "https://repo.dmulloy2.net/repository/public/")
    maven(url = "https://repo.maven.apache.org/maven2/")
    maven(url = "https://mvn.lumine.io/repository/maven-public/")
    maven(url = "https://repo.oraxen.com/releases")
}

dependencies {
    api("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT")
    api("org.bstats:bstats-bukkit:3.0.0")
    compileOnly("io.lumine:Mythic-Dist:5.3.5")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:3.11.50")
    compileOnly("org.jetbrains:annotations:23.0.0")
    compileOnly("io.netty:netty-all:4.1.85.Final")
    compileOnly("me.clip:placeholderapi:2.10.4")
    compileOnly("io.th0rgal:oraxen:1.170.0")
    compileOnly("com.wolfyscript.wolfyutils.spigot:wolfyutils-spigot:4.17-beta.2-SNAPSHOT")
}

java.sourceCompatibility = JavaVersion.VERSION_17

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
        include(dependency("org.bstats:.*"))
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
