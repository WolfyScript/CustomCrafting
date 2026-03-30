plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    alias(libs.plugins.artifactory)
    id("build.settings.default")
    id("build.spigotlike")
}

dependencies {
    implementation(libs.bundles.jackson)
}