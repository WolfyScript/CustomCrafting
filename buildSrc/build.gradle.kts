plugins {
    `kotlin-dsl`
}

repositories {
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
    maven("https://artifacts.wolfyscript.com/artifactory/gradle-dev")
}

dependencies {
    compileOnly(files(libs::class.java.protectionDomain.codeSource.location))

    implementation(libs.plugins.devtools.docker.run.text())
    implementation(libs.plugins.devtools.docker.minecraft.text())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.20")
}

kotlin {
    jvmToolchain(21)
}

fun Provider<PluginDependency>.text(): String {
    val t = get()
    val id = t.pluginId
    val version = t.version
    return "$id:$id.gradle.plugin:$version"
}
