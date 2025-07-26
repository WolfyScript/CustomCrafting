plugins {
    kotlin("jvm")
    id("build.settings.default")
    alias(libs.plugins.fabric.loom)
}

dependencies {
    api(project(":api"))
    implementation(libs.bundles.exposed)
    implementation(libs.bundles.database.drivers)
    compileOnly(libs.jackson.kotlin)

    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
}

java {
    withSourcesJar()
}

tasks {
    // Disable remapping without having to disable the tasks
    // This will get shaded into other platforms that then use their specific remapper instead.
    // Additionally, this will be a public api, which should work across all platforms.
    remapJar {
        targetNamespace = "named"
    }
    remapSourcesJar {
        targetNamespace = "named"
    }
}
