package exposedshedlock

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import extension.ktor.exposed.Shedlocks
import extension.ktor.redis.shedlock
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

class ExposedShedlockTest : StringSpec({
    beforeTest {
        val dataSource = HikariDataSource(
            HikariConfig().also {
                it.jdbcUrl = "jdbc:postgresql://localhost:5432/cherhy"
                it.username = "postgres"
                it.password = "postgres"
                it.validate()
            }
        )

        Database.connect(dataSource)
        transaction { SchemaUtils.create(Shedlocks) }
    }

    afterEach {
        transaction { SchemaUtils.drop(Shedlocks) }
        transaction { SchemaUtils.create(Shedlocks) }
    }

    afterTest {
        transaction { SchemaUtils.drop(Shedlocks) }
    }

    "Verify that shedlock ensures the logic is executed only once when multiple applications start simultaneously with no existing data in the database" {
        val name = "test"
        val duration = 1.toMinutes()
        val count = AtomicInteger(0)

        coroutineScope {
            repeat(50) { launch { runCatching { shedlock(name, duration) { count.getAndIncrement() } } } }
            repeat(50) { launch { runCatching { shedlock(name, duration) { count.getAndIncrement() } } } }
            repeat(50) { launch { runCatching { shedlock(name, duration) { count.getAndIncrement() } } } }
            repeat(50) { launch { runCatching { shedlock(name, duration) { count.getAndIncrement() } } } }
        }

        count.get() shouldBe 1
    }

    "Verify that shedlock ensures the logic is executed only once when multiple applications start simultaneously with existing data in the database" {
        val name = "test"
        val duration = 1.toMinutes()
        val count = AtomicInteger(0)

        shedlock(name, 100.toMilliSeconds()) { count.getAndIncrement() }
        delay(100)

        coroutineScope {
            repeat(50) { launch { runCatching { shedlock(name, duration) { count.getAndIncrement() } } } }
            repeat(50) { launch { runCatching { shedlock(name, duration) { count.getAndIncrement() } } } }
            repeat(50) { launch { runCatching { shedlock(name, duration) { count.getAndIncrement() } } } }
            repeat(50) { launch { runCatching { shedlock(name, duration) { count.getAndIncrement() } } } }
        }

        count.get() shouldBe 2
    }
})

fun Int.toMinutes(): Duration = Duration.ofMinutes(this.toLong())
fun Int.toMilliSeconds(): Duration = Duration.ofMillis(this.toLong())