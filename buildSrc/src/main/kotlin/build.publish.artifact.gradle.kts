import org.gradle.accessors.dm.LibrariesForSharedLibs
import org.gradle.kotlin.dsl.getByType

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm")
}

val Project.sharedLibs
    get() = extensions.getByType(LibrariesForSharedLibs::class)

val wolfyReleasesUrl = providers.gradleProperty("wolfyReleasesUrl").get()
val wolfySnapshotsUrl = providers.gradleProperty("wolfySnapshotsUrl").get()

publishing {
    repositories {
        maven {
            val isSnapshot = version.toString().endsWith("-SNAPSHOT")
            val repoName = if (isSnapshot) "snapshots" else "releases"
            name = repoName
            url = uri(if (isSnapshot) wolfySnapshotsUrl else wolfyReleasesUrl)
            credentials {
                username = findProperty("wolfy-repo-publishing-token")?.toString() ?: ""
                password = findProperty("wolfy-repo-publishing-secret")?.toString() ?: ""
            }
        }
    }
}
