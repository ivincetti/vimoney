package ru.vincetti.vimoney.data.models.json

import com.google.gson.annotations.SerializedName

data class ConfigFile(

        @SerializedName("accounts")
        val accounts: List<AccountsItem>,

        @SerializedName("user")
        val user: User,

        @SerializedName("date_edit")
        val date_edit: Long,

        @SerializedName("currency")
        val currency: List<CurrencyItem>,

        @SerializedName("transactions")
        val transactions: List<TransactionsItem>,

        @SerializedName("categories")
        val categories: List<CategoriesItem>
)
