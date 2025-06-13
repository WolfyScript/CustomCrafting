plugins {
    kotlin("jvm")
    id("build.settings.default")
    alias(libs.plugins.fabric.loom)
}

dependencies {
    api(project(":api"))
    minecraft(libs.minecraft)
    compileOnly(libs.jackson.kotlin)
    mappings(loom.officialMojangMappings())
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
