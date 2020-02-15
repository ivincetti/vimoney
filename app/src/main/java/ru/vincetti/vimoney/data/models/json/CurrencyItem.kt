package ru.vincetti.vimoney.data.models.json

import com.google.gson.annotations.SerializedName

data class CurrencyItem(

        @SerializedName("code")
        val code: Int,

        @SerializedName("name")
        val name: String,

        val symbol: String
)