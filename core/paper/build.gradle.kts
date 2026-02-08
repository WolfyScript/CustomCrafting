plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    alias(libs.plugins.artifactory)
    id("build.settings.default")
    id("build.settings.fabric-loom")
    id("build.spigotlike")
}

dependencies {
    implementation(projects.core.coreSpigotlike)
    paperweight.paperDevBundle(libs.versions.papermc.get())
}
