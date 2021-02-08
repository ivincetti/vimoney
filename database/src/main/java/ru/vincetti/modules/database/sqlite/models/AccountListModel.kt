package ru.vincetti.modules.database.sqlite.models

import androidx.room.ColumnInfo
import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.core.models.Currency

data class AccountListModel(

    val id: Int,

    val name: String,

    val type: String,

    val sum: Int,

    @ColumnInfo(name = "symbol")
    val curSymbol: String = Currency.DEFAULT_CURRENCY_SYMBOL,

    val color: String,

    @ColumnInfo(name = "archive")
    val isArchive: Boolean,

    var currency: Int = Account.DEFAULT_CURRENCY,

    @ColumnInfo(name = "extra_key")
    var extraKey: String = "",

    @ColumnInfo(name = "extra_value")
    var extraValue: String = "",

    @ColumnInfo(name = "need_all_balance")
    var needAllBalance: Boolean = true,

    @ColumnInfo(name = "need_on_main_screen")
    var needOnMain: Boolean = true
) {

    fun toAccountList(): AccountList {
        return AccountList(
            id, name, type, sum, curSymbol, color, isArchive, currency, extraKey, extraValue, needAllBalance, needOnMain
        )
    }

    companion object {
        fun from(account: AccountList): AccountListModel {
            return AccountListModel(
                account.id,
                account.name,
                account.type,
                account.sum,
                account.curSymbol,
                account.color,
                account.isArchive,
                account.currency,
                account.extraKey,
                account.extraValue,
                account.needAllBalance,
                account.needOnMain
            )
        }
    }
}
