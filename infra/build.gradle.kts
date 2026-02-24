plugins {
    alias(libs.plugins.dependencyManagement)
    `java-library`
}

dependencies {
    implementation(libs.common.library)
    implementation(project(":core"))

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.aws.s3)
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    runtimeOnly(libs.postgresql)
}
