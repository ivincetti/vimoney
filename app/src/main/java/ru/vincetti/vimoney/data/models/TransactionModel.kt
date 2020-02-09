package ru.vincetti.vimoney.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "transactions")
data class TransactionModel(
        @PrimaryKey(autoGenerate = true) var id: Int = DEFAULT_ID,
        @ColumnInfo(name = "account_id") var accountId: Int = DEFAULT_ID,
        var description: String = "",
        var date: Date = Date(),
        @ColumnInfo(name = "updated_at") var updatedAt: Date = Date(),
        var type: Int = TRANSACTION_TYPE_SPENT,
        var sum: Float = 0f,
        @ColumnInfo(name = "extra_key") var extraKey: String = "",
        @ColumnInfo(name = "extra_value") var extraValue: String = "",
        var system: Boolean = false,
        var deleted: Boolean = false
) {

    @Ignore
    constructor(
            _date: Date,
            _accountId: Int,
            _description: String,
            _type: Int,
            _sum: Float
    ) : this(
            date = _date,
            accountId = _accountId,
            description = _description,
            type = _type,
            sum = _sum
    )

    companion object {
        const val TRANSACTION_TYPE_INCOME = 1
        const val TRANSACTION_TYPE_SPENT = 2
        const val TRANSACTION_TYPE_TRANSFER = 3
        const val TRANSACTION_TYPE_DEBT = 4
        const val TRANSACTION_TYPE_SPENT_TAB = 0
        const val TRANSACTION_TYPE_INCOME_TAB = 1
        const val TRANSACTION_TYPE_TRANSFER_TAB = 2
        const val TRANSACTION_TYPE_DEBT_TAB = 3
        const val TRANSACTION_TYPE_TRANSFER_KEY = "transfer_transaction_id"
        const val DEFAULT_ID = 0
    }
}
