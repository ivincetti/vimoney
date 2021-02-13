package ru.vincetti.modules.core.models

data class Account(
    var id: Int = 0,
    var name: String? = "",
    var type: String? = ACCOUNT_TYPE_CASH,
    var sum: Int = 0,
    var currency: Int = DEFAULT_CURRENCY,
    var extraKey: String = "",
    var extraValue: String = "",
    var color: String = DEFAULT_COLOR,
    var isArchive: Boolean = false,
    var needAllBalance: Boolean = true,
    var needOnMain: Boolean = true
) {

    companion object {
        const val ACCOUNT_TYPE_CASH = "cash"
        const val ACCOUNT_TYPE_DEBIT = "debit"
        const val ACCOUNT_TYPE_CREDIT = "credit"

        const val DEFAULT_COLOR = "#164fc6"
        const val DEFAULT_CURRENCY = 812
    }
}
