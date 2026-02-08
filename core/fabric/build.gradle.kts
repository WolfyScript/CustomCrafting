plugins {
    kotlin("jvm")
    `java-library`
    id(libs.plugins.devtools.docker.minecraft.get().pluginId)
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
    api(shadow(project(":core:core-api"))!!)
    api(shadow(project(":core:core-common"))!!)

    implementation(libs.scafall.loader)
    implementation(libs.bundles.exposed)
    implementation(libs.bundles.database.drivers)
    compileOnly(libs.jackson.kotlin)

    // TODO: Change next MC release
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
}
