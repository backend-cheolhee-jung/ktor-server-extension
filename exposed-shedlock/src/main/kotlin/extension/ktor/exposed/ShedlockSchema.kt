package extension.ktor.exposed

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.datetime
import java.time.LocalDateTime

object Shedlocks : Table("shedlock") {
    val name: Column<String> = varchar("name", 255).uniqueIndex()
    val lockedAt = datetime("locked_at").default(LocalDateTime.now())
    val lockUntil = datetime("lock_until")

    override val primaryKey = PrimaryKey(name)
}

data class Shedlock(
    val name: String,
    val lockedAt: LocalDateTime,
    val lockUntil: LocalDateTime,
) {
    companion object {
        fun of(
            resultRow: ResultRow,
        ) =
            Shedlock(
                name = resultRow[Shedlocks.name],
                lockedAt = resultRow[Shedlocks.lockedAt],
                lockUntil = resultRow[Shedlocks.lockUntil],
            )
    }
}