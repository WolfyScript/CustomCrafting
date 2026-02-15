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
    alias(libs.plugins.shadow)
    alias(libs.plugins.artifactory)
    alias(libs.plugins.resource.factory.bukkit)
    id("build.settings.default")
    id("build.settings.fabric-loom")
    id("build.docker.run")
    id("build.spigotlike")
}

repositories {
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(projects.core.coreApi)
    implementation(projects.core.coreCommon)
    implementation(projects.core.coreSpigotlike)
    implementation(projects.core.coreSpigot)
    implementation(projects.editor.editorCommon)
    implementation(projects.ui.uiCommon)

    paperweight.paperDevBundle(libs.versions.papermc.get())
}

val customArchiveName = archiveName("spigot", libs.versions.minecraft.get())

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION

tasks {
    shadowJar {
        archiveFileName.set("${customArchiveName}-mojmap.jar")

        finalizedBy(reobfJar)

        dependencies {
            include(project(project.projects.core.coreApi))
            include(project(project.projects.core.coreCommon))
            include(project(project.projects.core.coreSpigotlike))
            include(project(project.projects.core.coreSpigot))
            include(project(project.projects.editor.editorCommon))
            include(project(project.projects.ui.uiCommon))
            libs.bundles.sentry.get().forEach {
                include(dependency(it))
            }
        }
        metaInf.duplicatesStrategy = DuplicatesStrategy.FAIL

        relocate("org.bstats", "com.wolfyscript.customcrafting.spigot.bstats")
        relocate("io.sentry", "com.wolfyscript.customcrafting.core.sentry")
    }
    reobfJar {
        dependsOn(shadowJar)
        finalizedBy("spigot_copy")
        outputJar.set(layout.buildDirectory.file("libs/${customArchiveName}.jar"))
    }
}

artifacts {
    archives(tasks.reobfJar)
}

bukkitPluginYaml {
    name = "CustomCrafting"
    version = project.version.toString()
    main = "com.wolfyscript.customcrafting.spigot.SpigotLoaderPlugin"
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
    libName.set("${customArchiveName}.jar")
    servers {
        register("spigot") {
            destFileName.set("customcrafting.jar")
            version.set(libs.versions.minecraft.get())
            type.set("SPIGOT")
            extraEnv.put("BUILD_FROM_SOURCE", "true")
            imageVersion.set("java21-graalvm") // graalvm contains the jdk required to build from source
            ports.add("25569:25565")
        }
    }
}
