import masecla.modrinth4j.model.version.ProjectVersion
import org.gradle.accessors.dm.LibrariesForSharedLibs
import org.gradle.kotlin.dsl.getByType
import java.util.Locale
import java.util.Locale.getDefault

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm")
    id("com.modrinth.minotaur")
}

val Project.sharedLibs
    get() = extensions.getByType(LibrariesForSharedLibs::class)

private fun determineVersionType(): ProjectVersion.VersionType {
    val split = project.version.toString().split("-")
    return when {
        split.size < 2 -> ProjectVersion.VersionType.RELEASE
        split[1].startsWith("alpha") -> ProjectVersion.VersionType.ALPHA
        split[1].startsWith("beta") -> ProjectVersion.VersionType.BETA
        else -> ProjectVersion.VersionType.ALPHA
    }
}

modrinth {
    token.set(findProperty("modrinthPublishToken")?.toString() ?: System.getenv("MODRINTH_TOKEN")) // Remember to have the MODRINTH_TOKEN environment variable set or else this will fail - just make sure it stays private!
    projectId.set("customcrafting")
    versionNumber.set(project.version.toString()) // Will fail if Modrinth has this version already
    versionType.set(determineVersionType().toString().lowercase(Locale.ROOT))
    changelog.set(file("$rootDir/.changelog/${project.name}.md").readText())
    gameVersions.addAll(sharedLibs.versions.minecraft.get())

    dependencies {
        required.project("scafall")
    }
}
