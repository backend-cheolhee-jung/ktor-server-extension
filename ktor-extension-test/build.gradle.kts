import Module.EXPOSED_SHEDLOCK
import Module.HTTP_CLIENT
import Module.REDIS_CACHE
import Module.REDIS_SHEDLOCK
import Module.SCHEDULER
import Module.SERVER_PROTECTION

plugins {
    kotlin(Plugins.JVM) version PluginVersions.JVM_VERSION
    id(Plugins.SHADOW_JAR) version PluginVersions.SHADOW_JAR_VERSION
    id(Plugins.KTOR_PLUGIN) version PluginVersions.KTOR_PLUGIN_VERSION
}

dependencies {
    implementation(project(SCHEDULER))
    implementation(project(HTTP_CLIENT))
    implementation(project(REDIS_CACHE))
    implementation(project(REDIS_SHEDLOCK))
    implementation(project(EXPOSED_SHEDLOCK))
    implementation(project(SERVER_PROTECTION))

    testImplementation(Dependencies.KTOR_CLIENT_CIO)
    testImplementation(Dependencies.KTOR_CLIENT_CONTENT_NEGOTIATION)
    testImplementation(Dependencies.REDISSON)
    testImplementation(Dependencies.LETTUCE_CORE)
    testImplementation(Dependencies.KOTLIN_COROUTINES)
    testImplementation(Dependencies.HIKARI_CP)
    testImplementation(Dependencies.POSTGRESQL)
    testImplementation(Dependencies.EXPOSED_DAO)
    testImplementation(Dependencies.EXPOSED_JDBC)
    testImplementation(Dependencies.EXPOSED_CORE)
    testImplementation(Dependencies.EXPOSED_JAVA_TIME)
    testImplementation(Dependencies.KOTLIN_TEST_JUNIT)
    testImplementation(Dependencies.TEST_MOCKK)
    testImplementation(Dependencies.KOTEST_RUNNER_JUNIT5)
    testImplementation(Dependencies.KOTEST_ASSERTIONS_CORE)
    testImplementation(Dependencies.TEST_CONTAINERS_POSTGRESQL)
    testImplementation(Dependencies.TEST_CONTAINERS_JDBC)
    testImplementation(Dependencies.TEST_CONTAINERS_JUNIT_JUPITER)
    testImplementation(Dependencies.TEST_CONTAINERS_KOTEST_EXTENSIONS)
}

tasks.test {
    useJUnitPlatform()
}