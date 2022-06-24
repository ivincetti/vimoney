package ru.vincetti.vimoney.screens

import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.text.KButton
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.ui.history.filter.FilterDialog

object HistoryFilterScreen : KScreen<HistoryFilterScreen>() {

    override val layoutId: Int? = R.layout.dialog_transactions_filter
    override val viewClass: Class<*>? = FilterDialog::class.java

    val filterButton = KButton { withId(R.id.fragment_filter_btn) }
    val resetAll = KButton { withId(R.id.fragment_reset_btn) }
}
