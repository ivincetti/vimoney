package ru.vincetti.vimoney.screens

import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.ui.dashboard.DashboardFragment

object DashboardScreen : KScreen<DashboardScreen>() {

    override val layoutId: Int = R.layout.fragment_dashboard
    override val viewClass: Class<*> = DashboardFragment::class.java

    val toolbarBackButton = KButton { withId(R.id.setting_navigation_back_btn) }
    val yearLabel = KTextView { withId(R.id.dashboard_year) }
    val previousMonthButton = KButton { withId(R.id.dashboard_month_previous) }
    val monthLabel = KTextView { withId(R.id.dashboard_month) }
    val nextMonthButton = KButton { withId(R.id.dashboard_month_next) }
}
