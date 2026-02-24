rootProject.name = "intern-hub-lms-service"

include("api")
include("core")
include("infra")
includeBuild("../Intern-Hub-Common-Library")

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
