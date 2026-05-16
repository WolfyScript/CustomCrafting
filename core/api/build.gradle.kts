plugins {
    `java-library`
    `maven-publish`
    id("build.settings.default")
    id("build.settings.fabric-loom")
}

dependencies {
}

publishing {
    publications {
        create<MavenPublication>("lib") {
            from(components.getByName("java"))
            groupId = "com.wolfyscript.customcrafting.core"
            artifactId = "api"
            artifact(tasks.kotlinSourcesJar) {
                classifier = "sources"
            }
        }
    }
}
