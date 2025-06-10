plugins {
    kotlin("jvm")
    id("build.settings.default")
    alias(libs.plugins.fabric.loom)
}

dependencies {
    api(project(":api"))
    minecraft(libs.minecraft)
    compileOnly(libs.jackson.kotlin)
    mappings(loom.officialMojangMappings())
}
