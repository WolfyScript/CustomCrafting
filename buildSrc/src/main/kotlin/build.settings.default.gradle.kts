plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.parchmentmc.org/")
        content { includeGroup("org.parchmentmc.data") }
    }
    maven {
        url = uri("https://maven.neoforged.net/releases")
        content { includeGroup("org.parchmentmc.data") }
    }
    maven(url = "https://artifacts.wolfyscript.com/artifactory/gradle-dev")
    maven(url = "https://libraries.minecraft.net/")
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.maven.apache.org/maven2/")
    mavenLocal()
}

kotlin {
    jvmToolchain(21)
}

tasks {
    // Make sure all tasks which produce archives (jar, sources jar, javadoc jar, etc) produce more consistent output
    withType(AbstractArchiveTask::class).configureEach {
        isReproducibleFileOrder = true
        isPreserveFileTimestamps = false
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<Javadoc> {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }
}

val Project.libs
    get() = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

dependencies {
    api(libs.scafall.api)
    implementation(libs.bundles.jetbrains)

    compileOnly(libs.inject.guice)

    compileOnly(libs.bundles.minecraft.deps)
    compileOnlyApi(libs.bundles.jackson)
    compileOnlyApi(libs.bundles.adventure)

    testImplementation(libs.bundles.testing)
    testImplementation(kotlin("test"))
}
