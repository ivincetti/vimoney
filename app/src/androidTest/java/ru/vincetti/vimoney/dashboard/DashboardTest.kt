package ru.vincetti.vimoney.dashboard

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.vincetti.vimoney.MainActivity
import ru.vincetti.vimoney.screens.DashboardScreen
import ru.vincetti.vimoney.screens.HomeScreen
import ru.vincetti.vimoney.utils.TestUtils

@RunWith(AndroidJUnit4::class)
class DashboardTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.simple {
        beforeEachTest {
            TestUtils.openDashboard()
        }
    }
) {

    @get:Rule
    val activityTestRule = ActivityTestRule(
        MainActivity::class.java,
        true,
        true
    )

    @Test
    fun checkButtons() {
        run {
            DashboardScreen {
                toolbarBackButton.isDisplayed()
                yearLabel.isDisplayed()
                monthLabel.isDisplayed()
            }
        }
    }

    @Test
    fun checkBackButton() {
        run {
            step("Click back") {
                DashboardScreen {
                    toolbarBackButton.click()
                }
            }
            step("Check home screen") {
                HomeScreen {
                    homeFab.isDisplayed()
                }
            }
        }

    }

    @Test
    fun checkFilterTest() {
        run {
            DashboardScreen {
                monthLabel.hasAnyText()
            }
            DashboardScreen {
                previousMonthButton.click()
            }
        }
    }
}
