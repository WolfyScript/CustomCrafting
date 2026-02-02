import utils.archiveName

plugins {
    kotlin("jvm")
    `java-library`
    alias(libs.plugins.shadow)
    alias(libs.plugins.fabric.loom)
    id(libs.plugins.devtools.docker.minecraft.get().pluginId)
    id("build.settings.default")
    id("build.docker.run")
}

loom {
    serverOnlyMinecraftJar()

    mods {
        create("customcrafting") {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    api(shadow(projects.core.coreApi)!!)
    api(shadow(projects.core.coreCommon)!!)
    api(shadow(projects.core.coreFabric)!!)

    api(shadow(projects.editor.editorCommon)!!)
    api(shadow(projects.ui.uiCommon)!!)

    implementation(libs.scafall.loader)
    implementation(libs.bundles.exposed)
    implementation(libs.bundles.database.drivers)
    compileOnly(libs.jackson.kotlin)

    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings()) // TODO: Remove next MC release
    // TODO: Change next MC release
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }
    shadowJar {
        // Mappings are in the runtime classpath. Not sure why they are included even though we use include for dependencies...
        // So to be sure nothing else slips in, just accept dependencies from the shadow configuration.
        configurations = listOf(project.configurations.shadow.get())
        finalizedBy(remapJar)

        dependencies {
            include(project(project.projects.core.coreApi))
            include(project(project.projects.core.coreCommon))
            include(project(project.projects.core.coreFabric))

            include(project(project.projects.editor.editorCommon))
            include(project(project.projects.ui.uiCommon))

            libs.bundles.sentry.get().forEach {
                include(dependency(it))
            }
        }
        metaInf.duplicatesStrategy = DuplicatesStrategy.FAIL
        relocate("io.sentry", "com.wolfyscript.customcrafting.sentry")
    }
    java {
//        withSourcesJar()
    }
    // TODO: Remove next MC release
    remapJar {
        dependsOn(shadowJar)
        finalizedBy("fabric_copy")
        inputFile.set(shadowJar.get().archiveFile)
        archiveBaseName.set(project.rootProject.name)
        archiveClassifier.set("fabric-${libs.versions.minecraft.get()}")
    }
}

minecraftServers {
    libName.set("${archiveName("fabric", libs.versions.minecraft.get())}.jar")
    servers {
        register("fabric") {
            destPath.set("mods")
            destFileName.set("customcrafting.jar")
            version.set(libs.versions.minecraft.get())
            type.set("FABRIC")
            imageVersion.set("java21")
            ports.add("25569:25565")
            extraEnv.put("MODRINTH_PROJECTS", "fabric-api, fabric-language-kotlin")
            extraEnv.put("FABRIC_LOADER_VERSION", libs.versions.fabric.loader.get())
        }
    }
}
