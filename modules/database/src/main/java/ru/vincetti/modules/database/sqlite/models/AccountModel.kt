package ru.vincetti.modules.database.sqlite.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.vincetti.modules.core.models.Account

@Entity(tableName = "accounts")
data class AccountModel(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    var name: String? = "",

    var type: String? = Account.ACCOUNT_TYPE_CASH,

    var sum: Int = 0,

    var currency: Int = Account.DEFAULT_CURRENCY,

    @ColumnInfo(name = "extra_key")
    var extraKey: String = "",

    @ColumnInfo(name = "extra_value")
    var extraValue: String = "",

    var color: String = Account.DEFAULT_COLOR,

    @ColumnInfo(name = "archive")
    var isArchive: Boolean = false,

    @ColumnInfo(name = "need_all_balance")
    var needAllBalance: Boolean = true,

    @ColumnInfo(name = "need_on_main_screen")
    var needOnMain: Boolean = true
) {

    fun toAccount(): Account {
        return Account(
            id, name, type, sum, currency, extraKey, extraValue, color, isArchive, needAllBalance, needOnMain
        )
    }

    companion object {
        fun from(account: Account): AccountModel {
            return AccountModel(
                account.id,
                account.name,
                account.type,
                account.sum,
                account.currency,
                account.extraKey,
                account.extraValue,
                account.color,
                account.isArchive,
                account.needAllBalance,
                account.needOnMain
            )
        }
    }
}
