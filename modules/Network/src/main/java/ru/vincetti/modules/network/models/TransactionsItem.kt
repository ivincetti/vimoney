package ru.vincetti.modules.network.models

import com.google.gson.annotations.SerializedName

data class TransactionsItem(

    @SerializedName("date")
    val date: Long,

    @SerializedName("accountId")
    val accountId: Int,

    @SerializedName("description")
    val description: String,

    @SerializedName("sum")
    val sum: Int,

    @SerializedName("type")
    val type: Int
)
