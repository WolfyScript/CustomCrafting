plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.artifactory)
    id("build.settings.default")
    id("build.docker.run")
    id("build.spigotlike")
}

dependencies {
    shadow(libs.bundles.jackson)
    paperweight.paperDevBundle(libs.versions.papermc.get())
}

fun archiveName() = "${project.name}-${project.version}"

tasks {
    shadowJar {
        dependencies {
            include(project(":core:core-api"))
            include(project(":core:core-common"))
        }
        metaInf.duplicatesStrategy = DuplicatesStrategy.FAIL
    }
    reobfJar {
        enabled = false
    }
    build {
        dependsOn(shadowJar)
    }
}

artifacts {
    archives(tasks.shadowJar)
    default(tasks.shadowJar)
    implementation(tasks.shadowJar)
}