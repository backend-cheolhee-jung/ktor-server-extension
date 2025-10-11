object Dependencies {
    const val KOTLIN_COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${DependencyVersions.COROUTINES_VERSION}"
    const val KOTLIN_STD_LIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${DependencyVersions.KOTLIN_VERSION}"
    const val KOTLIN_TEST_JUNIT = "org.jetbrains.kotlin:kotlin-test-junit:${DependencyVersions.KOTLIN_VERSION}"
    const val KOTEST_RUNNER_JUNIT5 = "io.kotest:kotest-runner-junit5:${DependencyVersions.JUNIT_VERSION}"
    const val KOTEST_ASSERTIONS_CORE = "io.kotest:kotest-assertions-core:${DependencyVersions.JUNIT_VERSION}"

    const val KTOR_CLIENT_CIO = "io.ktor:ktor-client-cio-jvm"
    const val KTOR_CLIENT_CONTENT_NEGOTIATION = "io.ktor:ktor-client-content-negotiation-jvm"

    const val EXPOSED_CORE = "org.jetbrains.exposed:exposed-core:${DependencyVersions.EXPOSED_VERSION}"
    const val EXPOSED_DAO = "org.jetbrains.exposed:exposed-dao:${DependencyVersions.EXPOSED_VERSION}"
    const val EXPOSED_JDBC = "org.jetbrains.exposed:exposed-jdbc:${DependencyVersions.EXPOSED_VERSION}"
    const val EXPOSED_JAVA_TIME = "org.jetbrains.exposed:exposed-java-time:${DependencyVersions.EXPOSED_VERSION}"

    const val HIKARI_CP = "com.zaxxer:HikariCP:${DependencyVersions.HIKARI_CP_VERSION}"
    const val POSTGRESQL = "org.postgresql:postgresql:${DependencyVersions.POSTGRESQL_VERSION}"

    const val CRON_UTILS = "com.cronutils:cron-utils:${DependencyVersions.CRON_UTILS_VERSION}"

    const val LETTUCE_CORE = "io.lettuce:lettuce-core:${DependencyVersions.LETTUCE_VERSION}"
    const val REDISSON = "org.redisson:redisson:${DependencyVersions.REDISSON_VERSION}"

    const val ELASTIC_SEARCH_CLIENT = "co.elastic.clients:elasticsearch-java:${DependencyVersions.ELASTICSEARCH_CLIENT_VERSION}"
    const val ELASTIC_SEARCH_REST_HIGH_LEVEL_CLIENT = "org.elasticsearch.client:elasticsearch-rest-high-level-client:${DependencyVersions.ELASTICSEARCH_HIGH_LEVEL_CLIENT_VERSION}"

    const val TEST_CONTAINERS_POSTGRESQL = "org.testcontainers:postgresql:${DependencyVersions.TEST_CONTAINERS_VERSION}"
    const val TEST_CONTAINERS_JDBC = "org.testcontainers:jdbc:${DependencyVersions.TEST_CONTAINERS_VERSION}"
    const val TEST_CONTAINERS_JUNIT_JUPITER = "org.testcontainers:junit-jupiter:${DependencyVersions.TEST_CONTAINERS_VERSION}"
    const val TEST_CONTAINERS_KOTEST_EXTENSIONS = "io.kotest.extensions:kotest-extensions-testcontainers:${DependencyVersions.KOTEST_EXTENSIONS_VERSION}"
    const val TEST_MOCKK = "com.ninja-squad:springmockk:${DependencyVersions.TEST_MOCKK_VERSION}"
}