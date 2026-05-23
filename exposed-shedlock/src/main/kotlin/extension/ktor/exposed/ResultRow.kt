package extension.ktor.exposed

import org.jetbrains.exposed.v1.core.ResultRow

internal fun ResultRow.toShedLock() = Shedlock.of(this)