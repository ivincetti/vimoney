package ru.vincetti.modules.network.models

import com.google.gson.annotations.SerializedName

data class ConfigFile(

    @SerializedName("accounts")
    val accounts: List<AccountsItem>,

    @SerializedName("user")
    val user: User,

    @SerializedName("date_edit")
    val dateEdit: Long,

    @SerializedName("currency")
    val currency: List<CurrencyItem>,

    @SerializedName("transactions")
    val transactions: List<TransactionsItem>,

    @SerializedName("categories")
    val categories: List<CategoriesItem>
)
