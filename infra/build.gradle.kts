plugins {
    id("java-library")
}

dependencies {

    api(project(":common"))

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.kafka)
    implementation(libs.spring.boot.starter.liquibase)

    runtimeOnly(libs.postgresql)

    testImplementation(rootProject.libs.spring.boot.starter.test)
}

