rootProject.name = "intern-hub-lms-service"

include("common")
include("api")
include("core")
include("infra")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}
