plugins {
    kotlin("jvm")
}

group = "com.github.lolmageap"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(Dependencies.ELASTIC_SEARCH_REST_HIGH_LEVEL_CLIENT)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}