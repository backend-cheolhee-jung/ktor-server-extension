# ktor-extension

## Description

This is a simple extension for the Ktor framework that provides an easy way to use a scheduler and ShedLock.

## Dependencies

It requires JDK version 17 or higher

add the following to your build.gradle.kts file:

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.lolmageap.ktor-server-extension:scheduler:1.0.5")
    implementation("com.github.lolmageap.ktor-server-extension:exposed-shedlock:1.0.5")
    implementation("com.github.lolmageap.ktor-server-extension:redis-shedlock:1.0.5")
    implementation("com.github.lolmageap.ktor-server-extension:redis-cache:1.0.5")
}
```

## Usage ShedLock & Scheduler

By combining the scheduler with ShedLock, you can ensure safe execution in a scale-out environment.
ShedLock guarantees that scheduled tasks will run only once across multiple instances, preventing race conditions or
duplicate executions in distributed setups.

```kotlin
fun Application.module() {
    schedule("0 0 0 * * *") {
        shedlock("shedlock", 5.minutes) {
            println("Hello, world!")
        }
    }
}
```

### Scheduler

This extension provides a simple way to schedule tasks within the Ktor framework.

```kotlin
fun Application.module() {
    schedule("0 0 0 * * *") {
        println("Hello, world!")
    }
}
```

or

```kotlin
import kotlin.time.Duration.Companion.days

fun Application.module() {
    schedule(fixedRate = 1.days) {
        println("Hello, world!")
    }
}
```

or

```kotlin
import kotlin.time.Duration.Companion.days

fun Application.module() {
    schedule(fixedDelay = 1.days) {
        println("Hello, world!")
    }
}
```

### Exposed Shedlock

This extension offers an easy way to integrate ShedLock for managing distributed locks in the Ktor framework.

#### Configuration

Before using ShedLock, you need to create the necessary schema:

```kotlin
fun Application.module() {
    transaction { SchemaUtils.create(Shedlocks) }
}
```

#### Usage

To use ShedLock, specify a lock name and a duration for how long the lock should be held.

```kotlin
import kotlin.time.Duration.Companion.seconds

fun Application.module() {
    shedlock(name = "shedlock", lockAtMostFor = 5.minutes) {
        println("Hello, world!")
    }
}
```

or

```kotlin
fun Application.module() {
    shedlock(name = "shedlock", lockAtMostFor = Duration.ofMinutes(5)) {
        println("Hello, world!")
    }
}
```

Additionally, there is an option called resetLockUntilAfterComplete that determines whether lockUntil should be reset
after the lock is released.
The default value for this option is true.

```kotlin
import kotlin.time.Duration.Companion.seconds

fun Application.module() {
    shedlock(name = "shedlock", lockAtMostFor = 5.minutes, resetLockUntilAfterComplete = false) {
        println("Hello, world!")
    }
}
```

## Redis

### Configuration

Before using Redis, you need to add the following dependencies to your build.gradle.kts file:

```kotlin
dependencies {
    implementation("org.redisson:redisson:$redissonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
}
```

Before using ShedLock, you need to initialize the Redisson client:

```kotlin
fun Application.module() {
    RedissonClientHolder.redissonClient = Redisson.create()
}
```

You can configure serialization before you set up your Redis cache:

```kotlin
fun Application.module() {
    RedisObjectMapper.objectMapper.apply {
        enable(SerializationFeature.INDENT_OUTPUT)
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
}
```

## Usage Redis Shedlock

```kotlin
fun Application.module() {
    shedlock(name = "shedlock", lockAtMostFor = 5.minutes) {
        println("Hello, world!")
    }
}
```

## Redis Cache

### Usage Redis Cache

cacheable stores data in the cache and retrieves the value from the cache if it exists. 
If the cache is empty, the function executes, and the final value is stored in the cache.

```kotlin
fun Database.findAll(): List<String> {
    println("Fetching data from the database...")
    return listOf("value1", "value2", "value3")
}


fun Application.module() {
    val values = cacheable("cacheable", 5.minutes) {
        Database.findAll()
    }
    // console output: Fetching data from the database...
    
    
    val values2 = cacheable("cacheable", 5.minutes) {
        Database.findAll()
    }
    // console output: (no output, as the value is retrieved from the cache)
}
```

### Usage Redis Cache Lock

cacheLocking is used to solve the cache stampede problem. 
It utilizes a distributed lock to wait until the cache is populated and then retrieves the value from the cache once it is ready.

```kotlin
fun Application.module() {
    val values = cacheLocking("cacheLocking", 5.minutes) {
        Database.findAll()
    }
    
    println(values)
}
```

### Usage Redis Distributed Lock

to use distributed lock, you need to specify the name of the lock, the waiting time, and the lease time.  
The waiting time is the maximum time the lock will wait to acquire the lock.
The lease time is the maximum time the lock will be held.

```kotlin
import kotlin.time.Duration.Companion.seconds

fun Application.module() {
    distributedLock(name = "distributedLock", waitTime = 5.seconds, leaseTime = 1.seconds) {
        println("Hello, world!")
    }
}
```

### Usage Redis Rate Limiter

to use rate limiter, you need to specify the name of the rate limiter, the limit, and the duration.
The limit is the maximum number of requests allowed within the duration.

```kotlin
import kotlin.time.Duration.Companion.seconds

fun Application.module() {
    rateLimiter(name = "rateLimiter", limit = 10_000, duration = 10.seconds) {
        println("Hello, world!")
    }
}
```