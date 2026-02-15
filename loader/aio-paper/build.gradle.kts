import utils.archiveName

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
    implementation(projects.core.corePaper)
    implementation(projects.editor.editorCommon)
    implementation(projects.ui.uiCommon)

    paperweight.paperDevBundle(libs.versions.papermc.get())
}

val customArchiveName = archiveName("paper", libs.versions.minecraft.get())

tasks {
    shadowJar {
        archiveFileName.set("${customArchiveName}.jar")
        metaInf.duplicatesStrategy = DuplicatesStrategy.FAIL
        dependencies {
            include(project(project.projects.core.coreApi))
            include(project(project.projects.core.coreCommon))
            include(project(project.projects.core.coreSpigotlike))
            include(project(project.projects.core.corePaper))
            include(project(project.projects.editor.editorCommon))
            include(project(project.projects.ui.uiCommon))

            libs.bundles.sentry.get().forEach {
                include(dependency(it))
            }
        }
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
        relocate("org.bstats", "com.wolfyscript.customcrafting.bukkit.metrics")
        relocate("io.sentry", "com.wolfyscript.customcrafting.core.sentry")
//        relocate("com.fasterxml.jackson", "com.wolfyscript.scafall.lib.jackson")
    }
    assemble {
        dependsOn(shadowJar)
    }
}

artifacts {
    archives(tasks.shadowJar)
}

bukkitPluginYaml {
    name = "CustomCrafting"
    version = project.version.toString()
    main = "com.wolfyscript.customcrafting.paper.PaperLoaderPlugin"
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
        register("paper") {
            destFileName.set("customcrafting.jar")
            version.set(libs.versions.minecraft.get())
            type.set("PAPER")
            imageVersion.set("java21")
            ports.add("25570:25565")
        }
    }
}