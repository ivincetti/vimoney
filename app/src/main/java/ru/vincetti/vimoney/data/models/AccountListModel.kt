package ru.vincetti.vimoney.data.models

import androidx.room.ColumnInfo

data class AccountListModel(
        val id: Int,
        val name: String?,
        val type: String?,
        val sum: Int,
        // TODO подумать над сохранением/ресурсами
        @ColumnInfo(name = "account_symbol") val curSymbol: String = "\u20BD",
        val color: String?,
        @ColumnInfo(name = "archive") val isArchive: Boolean
)



