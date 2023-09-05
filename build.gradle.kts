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

/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version ("8.1.1")
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://maven.wolfyscript.com/repository/public/")
    }

    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
    maven(url = "https://repo.maven.apache.org/maven2/")
    maven(url = "https://mvn.lumine.io/repository/maven-public/")
}

dependencies {
    api("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT")
    api("org.bstats:bstats-bukkit:3.0.0")
    compileOnly("io.lumine:Mythic-Dist:5.3.5")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:3.11.50")
    compileOnly("org.jetbrains:annotations:23.0.0")
    compileOnly("io.netty:netty-all:4.1.85.Final")
    compileOnly("me.clip:placeholderapi:2.10.4")
    compileOnly("com.github.oraxen:oraxen:1.156.0")
    compileOnly("com.wolfyscript.wolfyutils.spigot:wolfyutils-spigot:4.16.12-beta.1")
}

group = "com.wolfyscript.customcrafting"
version = "4.16.8.4"
description = "customcrafting-spigot"
java.sourceCompatibility = JavaVersion.VERSION_16

tasks.named<ShadowJar>("shadowJar") {

    dependencies {
        include(dependency("org.bstats:.*"))
    }

    relocate("org.bstats", "com.wolfyscript.customcrafting.bukkit.metrics")

}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
    repositories {
        mavenLocal()
        maven {
            url = if ((version as String).endsWith("-SNAPSHOT")) {
                name = "snapshots"
                uri("https://maven.wolfyscript.com/repository/snapshots/")
            } else {
                name = "releases"
                uri("https://maven.wolfyscript.com/repository/releases/")
            }
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}