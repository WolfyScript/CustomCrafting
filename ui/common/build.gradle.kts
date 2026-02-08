plugins {
    kotlin("jvm")
    id("build.settings.default")
    id("build.settings.fabric-loom")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.compose)
}

repositories {
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    api(libs.viewportl)
    implementation(projects.core.coreApi)
    implementation(projects.editor.editorCommon)

    implementation(libs.bundles.exposed)
    implementation(libs.bundles.database.drivers)
    compileOnly(libs.jackson.kotlin)
}
