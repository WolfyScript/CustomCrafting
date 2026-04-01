val Project.sharedLibs
    get() = extensions.getByType(org.gradle.accessors.dm.LibrariesForSharedLibs::class)

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven(url = "https://artifacts.wolfyscript.com/artifactory/gradle-dev")
    maven(url = "https://libraries.minecraft.net/")
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.maven.apache.org/maven2/")
    mavenLocal()
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }
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

dependencies {
    api(sharedLibs.scafall.api)
    implementation(sharedLibs.bundles.jetbrains)

    compileOnly(sharedLibs.inject.guice)
    compileOnly(sharedLibs.slf4j.api)

    implementation(sharedLibs.bundles.sentry)

    compileOnly(sharedLibs.bundles.minecraft.deps)
    compileOnlyApi(sharedLibs.bundles.jackson)
    compileOnlyApi(sharedLibs.bundles.adventure)

    testImplementation(sharedLibs.bundles.testing)
    testImplementation(kotlin("test"))
}
