plugins {
    `java-library`
    `maven-publish`
    id("build.settings.default")
}

dependencies {

}

publishing {
    publications {
        create<MavenPublication>("lib") {
            from(components.getByName("java"))
            groupId = "com.wolfyscript.customcrafting.api"
            artifactId = "api"
            artifact(tasks.kotlinSourcesJar) {
                classifier = "sources"
            }
        }
    }
}
