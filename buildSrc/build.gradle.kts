plugins {
    `kotlin-dsl`
}

repositories {
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
    maven("https://artifacts.wolfyscript.com/artifactory/gradle-dev")
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net")
        content {
            includeGroupAndSubgroups("net.fabricmc")
            includeGroup("fabric-loom")
        }
    }
}

dependencies {
    compileOnly(files(libs::class.java.protectionDomain.codeSource.location))

    implementation(libs.plugins.devtools.docker.run.depNotation())
    implementation(libs.plugins.devtools.docker.minecraft.depNotation())
    implementation(libs.plugins.paperweight.userdev.depNotation())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    implementation(libs.plugins.fabric.loom.depNotation())
}

kotlin {
    jvmToolchain(21)
}

fun Provider<PluginDependency>.depNotation(): String {
    val t = get()
    val id = t.pluginId
    val version = t.version
    return "$id:$id.gradle.plugin:$version"
}
