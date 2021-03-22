package ru.vincetti.modules.core.models

import java.util.*

@Suppress("DataClassShouldBeImmutable")
data class Transaction(
    var id: Int = DEFAULT_ID,
    var accountId: Int = DEFAULT_ID,
    var description: String? = "",
    var date: Date = Date(),
    var updatedAt: Date = Date(),
    var type: Int = TRANSACTION_TYPE_SPENT,
    var sum: Float = 0f,
    var categoryId: Int = DEFAULT_CATEGORY,
    var extraKey: String = "",
    var extraValue: String = "",
    var system: Boolean = false,
    var deleted: Boolean = false
) {

    constructor(
        newDate: Date,
        newAccountId: Int,
        newDescription: String,
        newType: Int,
        newSum: Float
    ) : this(
        date = newDate,
        accountId = newAccountId,
        description = newDescription,
        type = newType,
        sum = newSum
    )

    val isTransfer: Boolean
        get() = type == TRANSACTION_TYPE_TRANSFER

    companion object {
        const val TRANSACTION_TYPE_INCOME = 1
        const val TRANSACTION_TYPE_SPENT = 2
        const val TRANSACTION_TYPE_TRANSFER = 3
        const val TRANSACTION_TYPE_DEBT = 4
        const val TRANSACTION_TYPE_INCOME_TAB = 0
        const val TRANSACTION_TYPE_SPENT_TAB = 1
        const val TRANSACTION_TYPE_TRANSFER_TAB = 2
        const val TRANSACTION_TYPE_DEBT_TAB = 3
        const val TRANSACTION_TYPE_TRANSFER_KEY = "transfer_transaction_id"
        const val DEFAULT_ID = 0
        const val DEFAULT_CATEGORY = 1
    }
}
