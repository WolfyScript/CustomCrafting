plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    alias(sharedLibs.plugins.artifactory)
    id("build.settings.default")
    id("build.spigotlike")
}

dependencies {
    implementation(sharedLibs.bundles.jackson)
}