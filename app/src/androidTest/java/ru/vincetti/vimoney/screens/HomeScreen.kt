package ru.vincetti.vimoney.screens

import com.agoda.kakao.text.KButton
import com.kaspersky.kaspresso.screens.KScreen
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.ui.home.HomeFragment

object HomeScreen : KScreen<HomeScreen>() {

    override val layoutId: Int? = R.layout.fragment_home
    override val viewClass: Class<*>? = HomeFragment::class.java

    val homeFab = KButton { withId(R.id.home_fab) }
}