package ru.vincetti.modules.database.sqlite.models

import androidx.room.ColumnInfo
import ru.vincetti.modules.core.models.Transaction
import java.util.*

data class TransactionListModel(
    val id: Int = 0,

    @ColumnInfo(name = "account_name")
    val accountName: String,

    val description: String,

    @ColumnInfo(name = "category_icon")
    val symbol: String,

    val date: Date,

    val type: Int,

    val sum: Float,

    @ColumnInfo(name = "account_symbol")
    val curSymbol: String,

    @ColumnInfo(name = "extra_key")
    val extraKey: String,

    @ColumnInfo(name = "extra_value")
    val extraValue: String
) {

    fun getTypeString(): String {
        return when (type) {
            Transaction.TRANSACTION_TYPE_INCOME -> "+"
            Transaction.TRANSACTION_TYPE_TRANSFER -> "="
            Transaction.TRANSACTION_TYPE_DEBT -> return "|"
            else -> "-"
        }
    }
}
