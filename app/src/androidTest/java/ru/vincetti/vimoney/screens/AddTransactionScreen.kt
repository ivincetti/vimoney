package ru.vincetti.vimoney.screens

import com.agoda.kakao.text.KButton
import com.kaspersky.kaspresso.screens.KScreen
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.ui.transaction.main.TransactionMainFragment

object AddTransactionScreen : KScreen<AddTransactionScreen>() {

    override val layoutId: Int = R.layout.fragment_transaction_main
    override val viewClass: Class<*> = TransactionMainFragment::class.java

    val deleteToolbarButton = KButton { withId(R.id.transaction_navigation_delete_btn) }
}
