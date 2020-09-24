package ru.vincetti.vimoney.data.models

import androidx.room.ColumnInfo
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
            TransactionModel.TRANSACTION_TYPE_INCOME -> "+"
            TransactionModel.TRANSACTION_TYPE_TRANSFER -> "="
            TransactionModel.TRANSACTION_TYPE_DEBT -> return "|"
            else -> "-"
        }
    }
}
