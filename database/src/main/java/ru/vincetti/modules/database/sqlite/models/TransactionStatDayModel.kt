package ru.vincetti.modules.database.sqlite.models

import ru.vincetti.modules.core.models.TransactionStatDay

data class TransactionStatDayModel(
    val day: String,
    val sum: Int
) {

    fun toTransactionStatDay(): TransactionStatDay {
        return TransactionStatDay(day, sum)
    }
}
