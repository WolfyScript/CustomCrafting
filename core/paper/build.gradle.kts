plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.artifactory)
    id("build.settings.default")
    id("build.spigotlike")
}

dependencies {
    implementation(projects.core.coreSpigotlike)
    paperweight.paperDevBundle(libs.versions.papermc.get())
}

fun archiveName() = "${project.rootProject.name}-${project.version}-paper-${libs.versions.minecraft.get()}"

tasks {
    shadowJar {
        archiveFileName.set("${archiveName()}-mojmap.jar")
        metaInf.duplicatesStrategy = DuplicatesStrategy.FAIL
        dependencies {
            include(project(":core:core-spigotlike"))
            libs.bundles.sentry.get().forEach {
                include(dependency(it))
            }
        }
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
        relocate("org.bstats", "com.wolfyscript.customcrafting.bukkit.metrics")
        relocate("io.sentry", "com.wolfyscript.customcrafting.sentry")
//        relocate("com.fasterxml.jackson", "com.wolfyscript.scafall.lib.jackson")
    }
    assemble {
        dependsOn(shadowJar)
    }
}

artifacts {
    archives(tasks.shadowJar)
}
