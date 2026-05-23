package extension.ktor.exposed

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import java.time.LocalDateTime
import kotlin.time.toJavaDuration

suspend fun <T> shedlock(
    name: String,
    lockAtMostFor: kotlin.time.Duration,
    resetLockUntilAfterComplete: Boolean = true,
    block: suspend () -> T,
) {
    shedlock(name, lockAtMostFor.toJavaDuration(), resetLockUntilAfterComplete, block)
}

suspend fun <T> shedlock(
    name: String,
    lockAtMostFor: java.time.Duration,
    resetLockUntilAfterComplete: Boolean = true,
    block: suspend () -> T,
) {
    reactiveTransaction {
        val resultRow = Shedlocks.selectAll()
            .where { Shedlocks.name eq name }
            .forUpdate()
            .singleOrNull()

        val now = LocalDateTime.now()

        if (resultRow == null) {
            insertNewShedlock(name, now, lockAtMostFor)
        } else {
            val shedLock = resultRow.toShedLock()

            if (now between shedLock.lockedAt..shedLock.lockUntil) {
                throw AlreadyLockedException()
            }

            Shedlocks.update({ Shedlocks.name eq name }) {
                it[lockedAt] = now
                it[lockUntil] = now + lockAtMostFor
            }
        }
    }

    block.invoke()

    if (resetLockUntilAfterComplete) {
        reactiveTransaction {
            Shedlocks.update({ Shedlocks.name eq name }) {
                it[lockUntil] = LocalDateTime.now()
            }
        }
    }
}

private fun insertNewShedlock(
    name: String,
    now: LocalDateTime,
    lockAtMostFor: java.time.Duration,
): ResultRow {
    Shedlocks.insert {
        it[Shedlocks.name] = name
        it[lockedAt] = now
        it[lockUntil] = now + lockAtMostFor
    }

    return Shedlocks.selectAll()
        .where { Shedlocks.name eq name }
        .forUpdate()
        .single()
}