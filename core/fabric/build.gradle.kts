import utils.archiveName

plugins {
    kotlin("jvm")
    `java-library`
    alias(sharedLibs.plugins.shadow)
    id(sharedLibs.plugins.devtools.docker.minecraft.get().pluginId)
    id("build.settings.default")
    id("build.settings.fabric-loom")
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

    implementation(sharedLibs.bundles.exposed)
    implementation(sharedLibs.bundles.database.drivers)
    compileOnly(sharedLibs.jackson.kotlin)

    implementation(sharedLibs.fabric.loader)
    implementation(sharedLibs.fabric.api)
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
        finalizedBy("fabric_copy")

        dependencies {
            include(project(project.projects.core.coreApi))
            include(project(project.projects.core.coreCommon))

            sharedLibs.bundles.sentry.get().forEach {
                include(dependency(it))
            }
        }
        metaInf.duplicatesStrategy = DuplicatesStrategy.FAIL
        relocate("io.sentry", "com.wolfyscript.customcrafting.sentry")
    }
    java {
//        withSourcesJar()
    }
}

minecraftServers {
    libName.set("${archiveName("fabric", sharedLibs.versions.minecraft.get())}.jar")
    servers {
        register("fabric") {
            destPath.set("mods")
            destFileName.set("customcrafting.jar")
            version.set(sharedLibs.versions.minecraft.get())
            type.set("FABRIC")
            imageVersion.set("java${sharedLibs.versions.jdk.get()}")
            ports.add("25569:25565")
            extraEnv.put("MODRINTH_PROJECTS", "fabric-api, fabric-language-kotlin")
            extraEnv.put("FABRIC_LOADER_VERSION", sharedLibs.versions.fabric.loader.get())
        }
    }
}
