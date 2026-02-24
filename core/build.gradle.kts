plugins {
    alias(libs.plugins.dependencyManagement)
    `java-library`
}

dependencies {
    api(libs.common.library)
    api(libs.spring.boot.starter.webmvc)
    api(libs.spring.boot.starter.data.jpa)
}
