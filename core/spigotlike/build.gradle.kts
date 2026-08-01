plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    id("build.settings.default")
    id("build.spigotlike")
}

dependencies {
    implementation(sharedLibs.bundles.jackson)
}