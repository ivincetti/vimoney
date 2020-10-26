package ru.vincetti.vimoney.utils

import ru.vincetti.vimoney.screens.HomeScreen
import ru.vincetti.vimoney.screens.SettingsScreen

object TestUtils {

    fun openAllHistory() {
        HomeScreen {
            allHistoryButton.click()
        }
    }

    fun openDashboard() {
        HomeScreen {
            dashboardButton.click()
        }
    }

    fun openSettings() {
        HomeScreen {
            settingsButton.click()
        }
    }

    fun openCategories() {
        HomeScreen {
            settingsButton.click()
        }
        SettingsScreen {
            categoriesButton.click()
        }
    }
}
