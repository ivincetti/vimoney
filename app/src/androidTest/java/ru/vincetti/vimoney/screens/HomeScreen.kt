package ru.vincetti.vimoney.screens

import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.text.KButton
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.ui.home.HomeFragment

object HomeScreen : KScreen<HomeScreen>() {

    override val layoutId: Int = R.layout.fragment_home
    override val viewClass: Class<*> = HomeFragment::class.java

    val settingsButton = KButton { withId(R.id.home_menu_settings) }
    val dashboardButton = KButton { withId(R.id.home_stat_link) }
    val allHistoryButton = KButton { withId(R.id.home_transactions_link) }
    val homeFab = KButton { withId(R.id.home_fab) }
}
