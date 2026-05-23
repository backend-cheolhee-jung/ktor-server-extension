package extension.ktor.exposed

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

suspend fun <T> reactiveTransaction(
    block: () -> T,
) = withContext(Dispatchers.IO) { transaction { block() } }
