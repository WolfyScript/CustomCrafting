plugins {
    `java-library`
    `maven-publish`
    id("build.settings.default")
}

dependencies {
    compileOnly(libs.scafall.loader)

}

publishing {
    publications {
        create<MavenPublication>("lib") {
            from(components.getByName("java"))
            groupId = "com.wolfyscript.customcrafting.ui"
            artifactId = "api"
            artifact(tasks.kotlinSourcesJar) {
                classifier = "sources"
            }
        }
    }
}
