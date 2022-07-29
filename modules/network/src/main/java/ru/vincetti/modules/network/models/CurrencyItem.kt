package ru.vincetti.modules.network.models

import com.google.gson.annotations.SerializedName

data class CurrencyItem(

    @SerializedName("code")
    val code: Int,

    @SerializedName("name")
    val name: String,

    val symbol: String
)
