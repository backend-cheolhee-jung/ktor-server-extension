package extension.ktor.protection

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.ZonedDateTime

private val rateLimiterMap = mutableMapOf<String, RateLimiter>()
private val mutex = Mutex()

private data class RateLimiter(
    val startUpTime: ZonedDateTime,
    val count: Long,
)

suspend fun <T> rateLimiter(
    key: String,
    limit: Long,
    period: kotlin.time.Duration,
    block: suspend () -> T,
) {
    val now = ZonedDateTime.now()

    mutex.withLock(RATE_LIMITER_PREFIX + key) {
        if (rateLimiterMap hasNot key) rateLimiterMap[key] = RateLimiter(now, 0)

        val isExpired = now - rateLimiterMap.getValue(key).startUpTime > period.inWholeMilliseconds
        if (isExpired) rateLimiterMap[key] = RateLimiter(now, 0)

        rateLimiterMap[key] = rateLimiterMap.getValue(key).copy(count = rateLimiterMap.getValue(key).count + 1)

        if (rateLimiterMap.getValue(key).count > limit) throw RateLimitExceededException()
    }

    block()
}

suspend fun <T> rateLimiter(
    key: String,
    limit: Long,
    period: java.time.Duration,
    block: suspend () -> T,
) {
    val now = ZonedDateTime.now()

    mutex.withLock(RATE_LIMITER_PREFIX + key) {
        if (rateLimiterMap hasNot key) rateLimiterMap[key] = RateLimiter(now, 0)

        val isExpired = now - rateLimiterMap.getValue(key).startUpTime > period.toMillis()
        if (isExpired) rateLimiterMap[key] = RateLimiter(now, 0)

        rateLimiterMap[key] = rateLimiterMap.getValue(key).copy(count = rateLimiterMap.getValue(key).count + 1)

        if (rateLimiterMap.getValue(key).count > limit) throw RateLimitExceededException()
    }

    block()
}

private const val RATE_LIMITER_PREFIX = "rate_limiter:"

private infix fun <T, R> Map<T, R>.hasNot(
    key: T,
) = !containsKey(key)

private operator fun ZonedDateTime.minus(
    time: ZonedDateTime,
) = this.toInstant().toEpochMilli() - time.toInstant().toEpochMilli()