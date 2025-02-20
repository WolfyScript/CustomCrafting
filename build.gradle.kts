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
    //id("com.wolfyscript.devtools.docker.minecraft_servers") version "2.0-SNAPSHOT"
    id("com.jfrog.artifactory") version "5.2.0"
    id("com.modrinth.minotaur") version "2.+"
}

repositories {
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
    compileOnly(project(":spigot"))
}

java.sourceCompatibility = JavaVersion.VERSION_17

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")

    from(project(":spigot1_21").tasks.jar.get().archiveFile)
    from(project(":spigot").tasks.shadowJar.get().archiveFile)
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

val debugPort: String = "5006"

/*
minecraftDockerRun {
    val customEnv = env.get().toMutableMap()
    customEnv["MEMORY"] = "2G"
    customEnv["JVM_OPTS"] = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${debugPort}"
    env.set(customEnv)
    arguments("--cpus", "2", "-it") // Constrain to only use 2 cpus, and allow for console interactivity with 'docker attach'
}
*/

/*
minecraftServers {
    serversDir.set(file("${System.getProperty("user.home")}${File.separator}minecraft${File.separator}test_servers_v4"))
    libName.set("${project.name}-${version}.jar")
    val debugPortMapping = "${debugPort}:${debugPort}"
    servers {
        register("spigot_1_17") {
            version.set("1.17.1")
            type.set("SPIGOT")
            ports.set(setOf(debugPortMapping, "25565:25565"))
        }
        register("spigot_1_18") {
            version.set("1.18.2")
            type.set("SPIGOT")
            ports.set(setOf(debugPortMapping, "25566:25565"))
        }
        register("spigot_1_19") {
            version.set("1.19.4")
            type.set("SPIGOT")
            ports.set(setOf(debugPortMapping, "25567:25565"))
        }
        register("spigot_1_20_6") {
            version.set("1.20.6")
            type.set("SPIGOT")
            imageVersion.set("java21")
            ports.set(setOf(debugPortMapping, "25568:25565"))
        }
        register("spigot_1_21") {
            version.set("1.21.1")
            type.set("SPIGOT")
            extraEnv.put("BUILD_FROM_SOURCE", "true")
            imageVersion.set("java21-graalvm") // graalvm contains the jdk required to build from source
            ports.set(setOf(debugPortMapping, "25569:25565"))
        }
        // Paper test servers
        register("paper_1_21") {
            version.set("1.21")
            type.set("PAPER")
            imageVersion.set("java21")
            ports.set(setOf("5007:5007", "25570:25565"))
        }
        register("paper_1_20") {
            version.set("1.20.6")
            type.set("PAPER")
            imageVersion.set("java21")
            ports.set(setOf("5007:5007", "25570:25565"))
        }
        register("paper_1_19") {
            version.set("1.19.4")
            type.set("PAPER")
            ports.set(setOf(debugPortMapping, "25571:25565"))
        }
        // Purpur
        register("purpur_1_20") {
            version.set("1.20.4")
            type.set("PURPUR")
            ports.set(setOf(debugPortMapping, "25572:25565"))
        }
    }
}
*/

artifactory {
    publish {
        contextUrl = "https://artifacts.wolfyscript.com/artifactory"
        repository {
            repoKey = "gradle-dev-local"
            username = System.getenv("wolfyRepoPublishUsername")
            password = System.getenv("wolfyRepoPublishToken")
        }
        defaults {
            publications("maven")
            setPublishArtifacts(true)
            setPublishPom(true)
            isPublishBuildInfo = false
        }
    }
}

// build.gradle.kts
modrinth {
    token.set(System.getenv("MODRINTH_TOKEN")) // Remember to have the MODRINTH_TOKEN environment variable set or else this will fail - just make sure it stays private!
    projectId.set("customcrafting") // This can be the project ID or the slug. Either will work!
    versionNumber.set(project.version.toString()) // You don't need to set this manually. Will fail if Modrinth has this version already
    versionType.set("release") // TODO: Automatically determine this from the version
    uploadFile.set(tasks.shadowJar) // Use the shadowed jar !!
    changelog.set(System.getenv("CHANGELOG"))
    gameVersions.addAll("1.21.4") // Must be an array, even with only one version
    loaders.addAll("bukkit", "spigot", "paper", "purpur") // Must also be an array - no need to specify this if you're using Loom or ForgeGradle
    dependencies { // A special DSL for creating dependencies
        // scope.type
        // The scope can be `required`, `optional`, `incompatible`, or `embedded`
        // The type can either be `project` or `version`
        required.project("wolfyutils") // Creates a new required dependency on Fabric API
    }
}


