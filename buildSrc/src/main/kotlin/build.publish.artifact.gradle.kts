import org.gradle.accessors.dm.LibrariesForSharedLibs
import org.gradle.kotlin.dsl.getByType

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm")
    id("com.jfrog.artifactory")
}

val Project.sharedLibs
    get() = extensions.getByType(LibrariesForSharedLibs::class)

artifactory {
    publish {
        contextUrl = "https://artifacts.wolfyscript.com/artifactory"
        repository {
            repoKey = "gradle-dev-local"
            username = System.getenv("ARTIFACTORY_USERNAME")
            password = System.getenv("ARTIFACTORY_TOKEN")
        }
        defaults {
            publications("maven")
            setPublishArtifacts(true)
            setPublishPom(true)
            isPublishBuildInfo = false
        }
    }
}
