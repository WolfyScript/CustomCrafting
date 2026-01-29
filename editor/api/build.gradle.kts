plugins {
    `java-library`
    `maven-publish`
    id("build.settings.default")
}

dependencies {
    implementation(projects.core.coreApi)
    compileOnly(libs.scafall.loader)

}

publishing {
    publications {
        create<MavenPublication>("lib") {
            from(components.getByName("java"))
            groupId = "com.wolfyscript.customcrafting.editor"
            artifactId = "api"
            artifact(tasks.kotlinSourcesJar) {
                classifier = "sources"
            }
        }
    }
}
