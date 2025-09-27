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
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${libs.versions.minecraft.get()}:${libs.versions.parchment.get()}@zip")
    })
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
