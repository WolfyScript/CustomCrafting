plugins {
    `kotlin-dsl`
}

repositories {
    // Use the plugin portal to apply community plugins in convention plugins.
    maven {
        url = uri(providers.gradleProperty("wolfyReleasesUrl").get())
        mavenContent { releasesOnly() }
    }
    maven {
        url = uri(providers.gradleProperty("wolfySnapshotsUrl").get())
        mavenContent { snapshotsOnly() }
    }
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net")
        content {
            includeGroupAndSubgroups("net.fabricmc")
            includeGroup("fabric-loom")
        }
    }
    gradlePluginPortal()
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly(files(sharedLibs::class.java.protectionDomain.codeSource.location))
    compileOnly(files(libs::class.java.protectionDomain.codeSource.location))

    implementation(sharedLibs.plugins.devtools.docker.run.depNotation())
    implementation(sharedLibs.plugins.devtools.docker.minecraft.depNotation())
    implementation(sharedLibs.plugins.paperweight.userdev.depNotation())
    implementation(sharedLibs.plugins.artifactory.depNotation())
    implementation(sharedLibs.plugins.modrinth.minotaur.depNotation())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${sharedLibs.versions.kotlin.get()}")
    implementation(sharedLibs.plugins.fabric.loom.depNotation())
    implementation(sharedLibs.plugins.git.changelog.depNotation())
}

kotlin {
    jvmToolchain(Integer.parseInt(sharedLibs.versions.jdk.get()))
}

fun Provider<PluginDependency>.depNotation(): String {
    val t = get()
    val id = t.pluginId
    val version = t.version
    return "$id:$id.gradle.plugin:$version"
}
