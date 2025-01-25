package redisratelimiter

import extension.ktor.redis.RateLimitExceededException
import extension.ktor.redis.RedissonClientHolder
import extension.ktor.redis.rateLimiter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.async.RedisAsyncCommands
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import org.redisson.Redisson
import org.redisson.config.Config
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

class RedisRateLimiterTest : StringSpec({
    lateinit var redisClient: RedisAsyncCommands<String, String>

    beforeTest {
        redisClient =
            RedisClient.create()
                .connect(
                    RedisURI.create("redis://localhost:6379")
                )
                .async()

        RedissonClientHolder.redissonClient =
            Redisson.create(
                Config().apply {
                    useSingleServer().address = "redis://localhost:6379"
                }
            )
    }

    afterEach {
        redisClient.flushall()
        RedissonClientHolder.redissonClient.keys.flushall()
    }

    "rate limiter test" {
        val key = "rate-limiter"
        val number = AtomicInteger(0)

        (1..50).map {
            async {
                runCatching {
                    rateLimiter(key, 10, 1.seconds) {
                        number.incrementAndGet()
                    }
                }
            }
        }.awaitAll()

        number.get() shouldBe 10
    }

    "rate limiter throws RateLimitExceededException" {
        val key = "rate-limiter-with-exception"
        val number = AtomicInteger(0)

        (1..10).map {
            async {
                rateLimiter(key, 10, 1.seconds) {
                    number.incrementAndGet()
                }
            }
        }.awaitAll()

        number.get() shouldBe 10

        shouldThrow<RateLimitExceededException> {
            rateLimiter(key, 10, 1.seconds) {
                number.incrementAndGet()
            }
        }

        number.get() shouldBe 10
    }

    "rate limiter test with different key" {
        val lockedKey = "locked-key"
        val unlockedKey = "unlocked-key"

        val number = AtomicInteger(0)

        (1..50).map {
            async {
                runCatching {
                    rateLimiter(lockedKey, 10, 1.seconds) {
                        number.incrementAndGet()
                    }
                }
            }
        }.awaitAll()

        number.get() shouldBe 10

        rateLimiter(unlockedKey, 10, 1.seconds) {
            number.incrementAndGet()
        }

        number.get() shouldBe 11
    }

    "rate limiter test with end of period" {
        val key = "rate-limiter-end-of-period"
        val number = AtomicInteger(0)

        (1..50).map {
            async {
                runCatching {
                    rateLimiter(key, 10, 1.seconds) {
                        number.incrementAndGet()
                    }
                }
            }
        }.awaitAll()

        number.get() shouldBe 10

        shouldThrow<RateLimitExceededException> {
            rateLimiter(key, 10, 1.seconds) {
                number.incrementAndGet()
            }
        }

        delay(1.seconds)

        rateLimiter(key, 10, 1.seconds) {
            number.incrementAndGet()
        }

        number.get() shouldBe 11
    }
})