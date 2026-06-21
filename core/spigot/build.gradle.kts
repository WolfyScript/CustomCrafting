import utils.archiveName

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
    alias(sharedLibs.plugins.shadow)
    alias(sharedLibs.plugins.artifactory)
    alias(sharedLibs.plugins.resource.factory.bukkit)
    id("build.settings.default")
    id("build.spigotlike")
    id("build.docker.run")
}

dependencies {
    implementation(projects.core.coreSpigotlike)
    implementation(projects.core.coreApi)
    implementation(projects.core.coreCommon)

    paperweight.paperDevBundle(sharedLibs.versions.papermc.get())
}

val customArchiveName = archiveName("spigot", sharedLibs.versions.minecraft.get())

tasks {
    shadowJar {
        archiveFileName.set("${customArchiveName}.jar")

        finalizedBy("spigot_copy")

        dependencies {
            include(project(project.projects.core.coreApi))
            include(project(project.projects.core.coreCommon))
            include(project(project.projects.core.coreSpigotlike))
            include(project(project.projects.core.coreSpigot))
            sharedLibs.bundles.sentry.get().forEach {
                include(dependency(it))
            }
        }
        metaInf.duplicatesStrategy = DuplicatesStrategy.FAIL

        relocate("org.bstats", "com.wolfyscript.customcrafting.spigot.bstats")
        relocate("io.sentry", "com.wolfyscript.customcrafting.core.sentry")
    }
}

artifacts {
    archives(tasks.reobfJar)
}

bukkitPluginYaml {
    name = "CustomCrafting"
    version = project.version.toString()
    main = "com.wolfyscript.customcrafting.spigot.SpigotLoaderPlugin"
    apiVersion = sharedLibs.versions.minecraft.get() // Only support the latest Minecraft version!
    authors.add("WolfyScript")
    depend.add("scafall")

    libraries.apply {
//        libs.bundles.exposed.get().forEach {
//            add(it.toString())
//        }
        sharedLibs.bundles.database.drivers.get().forEach {
            add(it.toString())
        }

        addAll(
            sharedLibs.typesafe.config.get().toString(),
            sharedLibs.caffeine.get().toString(),
            libs.bstats.get().toString(),
        )
    }
}

minecraftServers {
    libName.set("${customArchiveName}.jar")
    servers {
        register("spigot") {
            destFileName.set("customcrafting.jar")
            version.set(sharedLibs.versions.minecraft.get())
            type.set("SPIGOT")
            extraEnv.put("BUILD_FROM_SOURCE", "true")
            imageVersion.set("java${sharedLibs.versions.jdk.get()}-graalvm") // graalvm contains the jdk required to build from source
            ports.add("25569:25565")
        }
    }
}
