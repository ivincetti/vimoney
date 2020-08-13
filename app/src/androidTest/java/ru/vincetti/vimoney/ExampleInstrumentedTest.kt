package ru.vincetti.vimoney;

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.vincetti.vimoney.screens.AddTransactionScreen
import ru.vincetti.vimoney.screens.HomeScreen
import ru.vincetti.vimoney.screens.IncomeTransactionScreen

/* . */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest : TestCase() {

    @get:Rule
    val activityTestRule = ActivityTestRule(
        MainActivity::class.java,
        true,
        false
    )

    @Test
    fun checkMainScreenButtons() {
        run {
            step("1. Launch"){
                activityTestRule.launchActivity(null)
            }
            step("2. ClickFab") {
                HomeScreen {
                    homeFab {
                        isVisible()
                        click()
                    }
                }
            }
            step("3. Check delete toolbar button") {
                AddTransactionScreen {
                    deleteToolbarButton {
                        isInvisible()
                    }
                }
            }
            step("4. Click on save button"){
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
