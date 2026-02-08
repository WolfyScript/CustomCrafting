plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    alias(libs.plugins.artifactory)
    id("build.settings.default")
    id("build.settings.fabric-loom")
    id("build.docker.run")
    id("build.spigotlike")
}

dependencies {
    shadow(libs.bundles.jackson)
    paperweight.paperDevBundle(libs.versions.papermc.get())
}

artifacts {
    archives(tasks.shadowJar)
    default(tasks.shadowJar)
    implementation(tasks.shadowJar)
}