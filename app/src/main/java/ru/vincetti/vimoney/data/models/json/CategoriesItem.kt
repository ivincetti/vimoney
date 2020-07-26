package ru.vincetti.vimoney.data.models.json

import com.google.gson.annotations.SerializedName

data class CategoriesItem(

        @SerializedName("categoryId")
        val categoryId: Int,

        @SerializedName("name")
        val name: String,

        @SerializedName("symbol")
        val symbol: String
)
