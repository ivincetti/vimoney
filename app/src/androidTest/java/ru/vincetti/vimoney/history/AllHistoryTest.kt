package ru.vincetti.vimoney.history

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.vincetti.vimoney.MainActivity
import ru.vincetti.vimoney.screens.AllHistoryScreen
import ru.vincetti.vimoney.screens.HistoryFilterScreen
import ru.vincetti.vimoney.screens.HomeScreen
import ru.vincetti.vimoney.screens.IncomeTransactionScreen
import ru.vincetti.vimoney.utils.TestUtils

@RunWith(AndroidJUnit4::class)
class AllHistoryTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.simple {
        beforeEachTest {
            TestUtils.openAllHistory()
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
            AllHistoryScreen {
                toolbarBackButton.isDisplayed()
                filterButton.isDisplayed()
                fab.isDisplayed()
            }
        }
    }

    @Test
    fun checkBackButton() {
        run {
            step("Click back") {
                AllHistoryScreen {
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
    fun addTransactionClick() {
        run {
            step("Add Click") {
                AllHistoryScreen {
                    fab.click()
                }
            }
            step("Add transaction opened") {
                IncomeTransactionScreen {
                    saveButton.isDisplayed()
                }
            }
        }
    }

    @Test
    fun checkFilterTest() {
        run {
            step("Filter Click") {
                AllHistoryScreen {
                    filterButton.click()
                }
            }
            step("Filter opened") {
                HistoryFilterScreen {
                    filterButton.isDisplayed()
                }
            }
        }
    }
}
