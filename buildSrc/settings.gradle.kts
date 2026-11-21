rootProject.name = "buildSrc" // fix gradle warning with type-safe project accessors

dependencyResolutionManagement {
    repositories {
//        mavenLocal()
        maven {
            url = uri(providers.gradleProperty("wolfyReleasesUrl").get())
            mavenContent { releasesOnly() }
        }
        maven {
            url = uri(providers.gradleProperty("wolfySnapshotsUrl").get())
            mavenContent { snapshotsOnly() }
        }
        mavenLocal()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
        create("sharedLibs") {
            from("com.wolfyscript.scafall:scafall-versions:2.1.0")
        }
    }
}
