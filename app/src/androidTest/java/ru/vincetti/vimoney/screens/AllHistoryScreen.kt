package ru.vincetti.vimoney.screens

import com.agoda.kakao.text.KButton
import com.kaspersky.kaspresso.screens.KScreen
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.ui.history.AllHistoryFragment

object AllHistoryScreen : KScreen<AllHistoryScreen>() {

    override val layoutId: Int? = R.layout.fragment_history
    override val viewClass: Class<*>? = AllHistoryFragment::class.java

    val toolbarBackButton = KButton {
        withId(R.id.records_navigation_back_btn)
    }
    val filterButton = KButton {
        withId(R.id.records_filter_btn)
    }
    val fab = KButton {
        withId(R.id.all_history_fab)
    }
}
