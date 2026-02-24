plugins {
    alias(libs.plugins.dependencyManagement)
    `java-library`
}

dependencies {
    implementation(project(":core"))

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.aws.s3)

    runtimeOnly(libs.postgresql)
}
