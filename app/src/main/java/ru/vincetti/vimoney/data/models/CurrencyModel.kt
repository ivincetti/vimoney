package ru.vincetti.vimoney.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class CurrencyModel(

        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,

        val code: Int,

        var name: String,

        val symbol: String
) {

    @Ignore
    constructor(_code: Int, _name: String, _symbol: String)
            : this(code = _code, name = _name, symbol = _symbol)

    override fun toString(): String {
        return symbol
    }
}
