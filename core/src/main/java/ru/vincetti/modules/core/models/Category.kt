package ru.vincetti.modules.core.models

data class Category(
    var id: Int = 0,
    val name: String,
    val symbol: String,
    val isForExpense: Boolean,
    val isForIncome: Boolean,
    val isArchive: Boolean,
)
