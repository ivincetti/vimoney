package ru.vincetti.modules.database.sqlite.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.vincetti.modules.core.models.Transaction
import java.util.*

@Entity(tableName = "transactions")
@Suppress("DataClassShouldBeImmutable")
data class TransactionModel(

    @PrimaryKey(autoGenerate = true)
    var id: Int = Transaction.DEFAULT_ID,

    @ColumnInfo(name = "account_id")
    var accountId: Int = Transaction.DEFAULT_ID,

    var description: String? = "",

    var date: Date = Date(),

    @ColumnInfo(name = "updated_at")
    var updatedAt: Date = Date(),

    var type: Int = Transaction.TRANSACTION_TYPE_SPENT,

    var sum: Float = 0f,

    @ColumnInfo(name = "category_id")
    var categoryId: Int = Transaction.DEFAULT_CATEGORY,

    @ColumnInfo(name = "extra_key")
    var extraKey: String = "",

    @ColumnInfo(name = "extra_value")
    var extraValue: String = "",

    var system: Boolean = false,

    var deleted: Boolean = false
) {

    fun toTransaction(): Transaction {
        return Transaction(
            id, accountId, description, date, updatedAt, type, sum, categoryId, extraKey, extraValue, system, deleted
        )
    }

    companion object {
        fun from(transaction: Transaction): TransactionModel {
            return TransactionModel(
                transaction.id,
                transaction.accountId,
                transaction.description,
                transaction.date,
                transaction.updatedAt,
                transaction.type,
                transaction.sum,
                transaction.categoryId,
                transaction.extraKey,
                transaction.extraValue,
                transaction.system,
                transaction.deleted
            )
        }
    }
}
