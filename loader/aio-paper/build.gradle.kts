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
    implementation(shadow(projects.core.coreApi)!!)
    implementation(shadow(projects.core.coreCommon)!!)
    implementation(shadow(projects.core.coreSpigotlike)!!)
    implementation(shadow(projects.core.corePaper)!!)
    implementation(shadow(projects.editor.editorCommon)!!)
    implementation(shadow(projects.ui.uiCommon)!!)

    paperweight.paperDevBundle(libs.versions.papermc.get())
}

val customArchiveName = archiveName("paper", libs.versions.minecraft.get())

tasks {
    shadowJar {
        // Mappings are in the runtime classpath. Not sure why they are included even though we use include for dependencies...
        // So to be sure nothing else slips in, just accept dependencies from the shadow configuration.
        configurations = listOf(project.configurations.shadow.get())

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
    name = "customcrafting"
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