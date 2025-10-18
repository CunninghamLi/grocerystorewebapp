pluginManagement {
    plugins {
        kotlin("jvm") version "2.1.20"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "Milestone1"

include(
    "api-gateway",
    "customer-service",
    "order-service",
    "payments-service",
    "product-service"
)
