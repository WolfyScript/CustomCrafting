plugins {
    kotlin("jvm")
    id("build.settings.default")
    id("build.settings.fabric-loom")
}

dependencies {
    api(project(":core:core-api"))
    implementation(libs.bundles.exposed)
    implementation(libs.bundles.database.drivers)
    compileOnly(libs.jackson.kotlin)
}

tasks {
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
