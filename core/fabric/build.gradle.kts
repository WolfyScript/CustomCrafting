plugins {
    kotlin("jvm")
    `java-library`
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
    api(projects.core.coreApi)
    api(projects.core.coreCommon)

    implementation(sharedLibs.scafall.loader)
    implementation(sharedLibs.bundles.exposed)
    implementation(sharedLibs.bundles.database.drivers)
    compileOnly(sharedLibs.jackson.kotlin)

    implementation(sharedLibs.fabric.loader)
    implementation(sharedLibs.fabric.api)
}
