import org.gradle.accessors.dm.LibrariesForSharedLibs
import org.gradle.kotlin.dsl.getByType

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm")
    id("com.modrinth.minotaur")
}

val Project.sharedLibs
    get() = extensions.getByType(LibrariesForSharedLibs::class)

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN")) // Remember to have the MODRINTH_TOKEN environment variable set or else this will fail - just make sure it stays private!
    projectId.set("customcrafting")
    versionNumber.set(project.version.toString()) // Will fail if Modrinth has this version already
    versionType.set("alpha") // TODO: Automatically determine this from the version
    changelog.set(System.getenv("CHANGELOG")) // TODO: Load generated changelog
    changelog.set(file("$rootDir/.changelog/${project.name}.md").readText())
    gameVersions.addAll(sharedLibs.versions.minecraft.get())

    dependencies {
        required.project("scafall")
    }
}
