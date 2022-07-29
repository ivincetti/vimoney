package ru.vincetti.modules.database.sqlite.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.vincetti.modules.core.models.Currency

@Entity(tableName = "currency")
data class CurrencyModel(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    val code: Int,

    val name: String?,

    val symbol: String
) {
    fun toCurrency(): Currency {
        return Currency(
            id, code, name, symbol
        )
    }

    companion object {
        fun from(currency: Currency): CurrencyModel {
            return CurrencyModel(
                currency.id,
                currency.code,
                currency.name,
                currency.symbol
            )
        }
    }
}
