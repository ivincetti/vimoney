package ru.vincetti.vimoney.data.models

import androidx.room.ColumnInfo

data class AccountListModel(
        val id: Int,
        val name: String?,
        val type: String?,
        val sum: Int,
        @ColumnInfo(name = "account_symbol") val curSymbol: String?,
        val color: String?,
        @ColumnInfo(name = "archive") val isArchive: Boolean
)



