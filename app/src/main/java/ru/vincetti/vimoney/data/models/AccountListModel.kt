package ru.vincetti.vimoney.data.models

import androidx.room.ColumnInfo

data class AccountListModel(

    val id: Int,

    val name: String,

    val type: String,

    val sum: Int,

    // TODO подумать над сохранением/ресурсами
    @ColumnInfo(name = "symbol")
    val curSymbol: String = "\u20BD",

    val color: String,

    @ColumnInfo(name = "archive")
    val isArchive: Boolean,

    var currency: Int = 812,

    @ColumnInfo(name = "extra_key")
    var extraKey: String = "",

    @ColumnInfo(name = "extra_value")
    var extraValue: String = "",

    @ColumnInfo(name = "need_all_balance")
    var needAllBalance: Boolean = true,

    @ColumnInfo(name = "need_on_main_screen")
    var needOnMain: Boolean = true
)
