plugins {
    `java-library`
    `maven-publish`
    id("build.settings.default")
    id("build.settings.fabric-loom")
    id("build.publish.artifact")
}

dependencies {
    implementation(sharedLibs.bundles.exposed)
    implementation(sharedLibs.bundles.database.drivers)
    compileOnly(sharedLibs.jackson.kotlin)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components.getByName("java"))
            groupId = "com.wolfyscript.customcrafting.core"
            artifactId = "api"
            artifact(tasks.kotlinSourcesJar) {
                classifier = "sources"
            }
        }
    }
}
