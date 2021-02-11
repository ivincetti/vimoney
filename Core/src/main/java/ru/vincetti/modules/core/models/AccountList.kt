package ru.vincetti.modules.core.models

data class AccountList(
    val id: Int,
    val name: String,
    val type: String,
    val sum: Int,
    val curSymbol: String = Currency.DEFAULT_CURRENCY_SYMBOL,
    val color: String,
    val isArchive: Boolean,
    var currency: Int = Account.DEFAULT_CURRENCY,
    var extraKey: String = "",
    var extraValue: String = "",
    var needAllBalance: Boolean = true,
    var needOnMain: Boolean = true
)
