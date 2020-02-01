package ru.vincetti.vimoney.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class CurrencyModel(
        @PrimaryKey(autoGenerate = true) var id: Int?,
        private val code: Int,
        var name: String,
        private val symbol: String
) {

    fun getCode(): Int {
        return code
    }

    fun getSymbol(): String {
        return symbol
    }

    override fun toString(): String {
        return symbol
    }
}
