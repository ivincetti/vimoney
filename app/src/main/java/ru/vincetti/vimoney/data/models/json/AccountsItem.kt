package ru.vincetti.vimoney.data.models.json

import com.google.gson.annotations.SerializedName

data class AccountsItem(

        @SerializedName("static_id")
        val staticId: String,

        @SerializedName("created")
        val created: String,

        @SerializedName("date_limit_interval")
        val dateLimitInterval: String,

        @SerializedName("instrument")
        val instrument: Int,

        @SerializedName("sum")
        val sum: String,

        @SerializedName("payoff_type")
        val payoffType: String,

        @SerializedName("type")
        val type: String,

        @SerializedName("title")
        val title: String,

        @SerializedName("percent")
        val percent: String,

        @SerializedName("date_limit")
        val dateLimit: String,

        @SerializedName("balance")
        val balance: Int,

        @SerializedName("id")
        val id: Int,

        @SerializedName("payoff_period")
        val payoffPeriod: String
)