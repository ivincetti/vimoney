package ru.vincetti.vimoney.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountModel(
        @PrimaryKey(autoGenerate = true) var id: Int?,
        var name: String = "",
        var type: String = ACCOUNT_TYPE_CASH,
        var sum: Int = 0,
        var currency: Int,
        @ColumnInfo(name = "extra_key") var extraKey: String = "",
        @ColumnInfo(name = "extra_value") var extraValue: String = "",
        var color: String = "",
        @ColumnInfo(name = "archive") var isArchive: Boolean = false
) {

    companion object {
        const val ACCOUNT_TYPE_CASH = "cash"
        const val ACCOUNT_TYPE_DEBIT = "debit"
        const val ACCOUNT_TYPE_CREDIT = "credit"
    }
}
