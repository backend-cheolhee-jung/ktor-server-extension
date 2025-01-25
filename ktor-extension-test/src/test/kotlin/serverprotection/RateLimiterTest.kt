package serverprotection

import extension.ktor.protection.RateLimitExceededException
import extension.ktor.protection.rateLimiter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

class RateLimiterTest : StringSpec({
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

        delay(1.seconds)

        rateLimiter(key, 10, 1.seconds) {
            number.incrementAndGet()
        }

        number.get() shouldBe 11
    }
})