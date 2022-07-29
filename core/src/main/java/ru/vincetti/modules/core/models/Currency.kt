package ru.vincetti.modules.core.models

data class Currency(
    var id: Int = 0,
    val code: Int,
    val name: String?,
    val symbol: String
) {

    constructor(
        newCode: Int,
        newName: String,
        newSymbol: String
    ) : this(
        code = newCode,
        name = newName,
        symbol = newSymbol
    )

    override fun toString(): String {
        return symbol
    }

    companion object {
        const val DEFAULT_CURRENCY_SYMBOL = "₽"
    }
}
