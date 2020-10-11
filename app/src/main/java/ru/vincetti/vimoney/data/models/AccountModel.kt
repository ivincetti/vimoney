package ru.vincetti.vimoney.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountModel(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    var name: String? = "",

    var type: String? = ACCOUNT_TYPE_CASH,

    var sum: Int = 0,

    var currency: Int = DEFAULT_CURRENCY,

    @ColumnInfo(name = "extra_key")
    var extraKey: String = "",

    @ColumnInfo(name = "extra_value")
    var extraValue: String = "",

    var color: String = DEFAULT_COLOR,

    @ColumnInfo(name = "archive")
    var isArchive: Boolean = false,

    @ColumnInfo(name = "need_all_balance")
    var needAllBalance: Boolean = true,

    @ColumnInfo(name = "need_on_main_screen")
    var needOnMain: Boolean = true
) {

    companion object {
        const val ACCOUNT_TYPE_CASH = "cash"
        const val ACCOUNT_TYPE_DEBIT = "debit"
        const val ACCOUNT_TYPE_CREDIT = "credit"

        const val DEFAULT_COLOR = "#164fc6"
        const val DEFAULT_CURRENCY = 812
    }
}
