package ru.vincetti.vimoney.home

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.vincetti.vimoney.MainActivity
import ru.vincetti.vimoney.screens.AddTransactionScreen
import ru.vincetti.vimoney.screens.HomeScreen
import ru.vincetti.vimoney.screens.IncomeTransactionScreen

/* . */
@RunWith(AndroidJUnit4::class)
class SimpleHomeTest : TestCase() {

    @get:Rule
    val activityTestRule = ActivityTestRule(
        MainActivity::class.java,
        true,
        true
    )

    @Test
    fun checkButtons() {
        run {
            HomeScreen {
                settingsButton.isDisplayed()
                dashboardButton.isDisplayed()
                allHistoryButton.isDisplayed()
                homeFab.isDisplayed()
            }
        }
    }

    @Test
    fun checkAddTransactionOpen() {
        run {
            step("Add transaction click") {
                HomeScreen {
                    homeFab.click()
                }
            }
            step("Check delete toolbar button") {
                AddTransactionScreen {
                    deleteToolbarButton.isInvisible()
                }
            }
            step("Click on save button") {
                IncomeTransactionScreen {
                    saveButton {
                        isVisible()
                        click()
                    }
                }
            }
        }
    }
}
