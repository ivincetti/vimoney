package ru.vincetti.vimoney.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class CurrencyModel(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    val code: Int,

    val name: String?,

    val symbol: String
) {

    @Ignore
    constructor(
        newCode: Int,
        newName: String,
        newSymbol: String
    ) : this(code = newCode, name = newName, symbol = newSymbol)

    override fun toString(): String {
        return symbol
    }
}
