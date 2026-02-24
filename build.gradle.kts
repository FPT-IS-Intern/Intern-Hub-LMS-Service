plugins {
    alias(libs.plugins.springBoot) apply false
    alias(libs.plugins.dependencyManagement) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.sonar) apply false
    alias(libs.plugins.jib) apply false
}

allprojects {
    group = "com.fis.lms_service"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    plugins.withType<JavaPlugin> {
        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }

        tasks.withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }
}
