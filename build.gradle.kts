import com.diffplug.gradle.spotless.SpotlessExtension
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
    alias(libs.plugins.springBoot) apply false
    alias(libs.plugins.dependencyManagement) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.sonar) apply false
    alias(libs.plugins.jib) apply false
}

allprojects {
    group = "com.intern.hub"
    version = "1.0.0"

    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

subprojects {
    plugins.withId("io.spring.dependency-management") {
        extensions.configure<DependencyManagementExtension> {
            imports {
                mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.versions.springBoot.get()}")
                mavenBom("io.opentelemetry:opentelemetry-bom:${libs.versions.openTelemetry.get()}")
            }
        }
    }

    plugins.withId("java") {
        apply(plugin = "com.diffplug.spotless")

        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(25))
            }
        }

        extensions.configure<SpotlessExtension> {
            java {
                // Use Google Java Format to keep code style consistent across modules.
                googleJavaFormat(libs.versions.googleJavaFormat.get())
                target("src/**/*.java")
            }
        }

        dependencies {
            "compileOnly"(libs.lombok)
            "annotationProcessor"(libs.lombok)
            "testCompileOnly"(libs.lombok)
            "testAnnotationProcessor"(libs.lombok)
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
