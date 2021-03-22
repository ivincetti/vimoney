package ru.vincetti.vimoney.screens

import com.agoda.kakao.text.KButton
import com.kaspersky.kaspresso.screens.KScreen
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.ui.settings.SettingsFragment

object SettingsScreen : KScreen<SettingsScreen>() {

    override val layoutId: Int = R.layout.fragment_settings
    override val viewClass: Class<*> = SettingsFragment::class.java

    val toolbarBackButton = KButton {
        withId(R.id.setting_navigation_back_btn)
    }
    val categoriesButton = KButton {
        withId(R.id.settings_categories_btn)
    }
    val saveJSONButton = KButton {
        withId(R.id.save_transactions_btn)
    }
    val loadJSONButton = KButton {
        withId(R.id.load_transactions_btn)
    }
}
