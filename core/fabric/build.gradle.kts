import org.gradle.kotlin.dsl.invoke
import utils.archiveName

plugins {
    kotlin("jvm")
    `java-library`
    alias(sharedLibs.plugins.shadow)
    id(sharedLibs.plugins.devtools.docker.minecraft.get().pluginId)
    id("build.settings.default")
    id("build.settings.fabric-loom")
    id("build.docker.run")
    id("build.docs.changelog")
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

val customArchiveName = archiveName("fabric", sharedLibs.versions.minecraft.get())

tasks {
    processResources {
        doNotTrackState("Always process resources to stay up-to-date with versions")

        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version,
                "minecraftVersion" to sharedLibs.versions.minecraft.get(),
                "fabricLoaderVersion" to sharedLibs.versions.fabric.loader.get(),
                "javaVersion" to kotlin.target.compilerOptions.jvmTarget.get().target,
                "scafallVersion" to libs.versions.scafall.get()
            )
        }
    }
    shadowJar {
        // Mappings are in the runtime classpath. Not sure why they are included even though we use include for dependencies...
        // So to be sure nothing else slips in, just accept dependencies from the shadow configuration.
        configurations = listOf(project.configurations.shadow.get())
        archiveFileName = "${customArchiveName}.jar"
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
    gitChangelog {
        file.set(File(".changelog/core-fabric.md"))
        settingsFile.set("${rootProject.rootDir.absolutePath}/.changelog/settings-core-fabric.json")
    }
}

minecraftServers {
    libName.set("${customArchiveName}.jar")
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
