plugins {
    kotlin("jvm")
    id("build.settings.default")
    id("build.settings.fabric-loom")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.compose)
}

repositories {
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    api(libs.viewportl)
    implementation(projects.core.coreApi)
    implementation(projects.editor.editorCommon)

    implementation(libs.bundles.exposed)
    implementation(libs.bundles.database.drivers)
    compileOnly(libs.jackson.kotlin)

    minecraft(libs.minecraft)
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
    assemble {
        dependsOn(remapJar)
    }
    processResources {
        include("**/*.properties")
        expand("customcrafting_release" to project.version)
        doNotTrackState("not updated when properties change") // always run the task to stay up-to-date
    }
}

artifacts {
    archives(tasks.remapJar)
}
