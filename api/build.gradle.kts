plugins {
    alias(libs.plugins.springBoot)
    alias(libs.plugins.dependencyManagement)
    java
}

dependencies {
    implementation(libs.common.library)
    implementation(libs.security.starter)
    implementation(project(":core"))
    implementation(project(":infra"))

    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.liquibase)
    implementation(libs.openapi.doc)
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    runtimeOnly(libs.postgresql)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
}

tasks.bootJar {
    enabled = true
}

tasks.jar {
    enabled = false
}
