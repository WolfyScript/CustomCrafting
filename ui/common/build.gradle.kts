plugins {
    kotlin("jvm")
    id("build.settings.default")
    id("build.settings.fabric-loom")
    alias(sharedLibs.plugins.compose.compiler)
    alias(sharedLibs.plugins.jetbrains.compose)
}

repositories {
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    api(libs.viewportl)
    implementation(projects.core.coreApi)
    implementation(projects.editor.editorCommon)

    implementation(sharedLibs.bundles.exposed)
    implementation(sharedLibs.bundles.database.drivers)
    compileOnly(sharedLibs.jackson.kotlin)
}
