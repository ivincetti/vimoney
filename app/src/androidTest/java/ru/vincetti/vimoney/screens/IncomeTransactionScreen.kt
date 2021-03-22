package ru.vincetti.vimoney.screens

import com.agoda.kakao.text.KButton
import com.kaspersky.kaspresso.screens.KScreen
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.ui.transaction.income.TransactionIncomeFragment

object IncomeTransactionScreen : KScreen<IncomeTransactionScreen>() {

    override val layoutId: Int = R.layout.fragment_add_spent
    override val viewClass: Class<*> = TransactionIncomeFragment::class.java

    val saveButton = KButton {
        withId(R.id.add_btn)
        isDisplayed()
    }
}
