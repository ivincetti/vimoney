package ru.vincetti.vimoney.settings

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.vincetti.vimoney.MainActivity
import ru.vincetti.vimoney.screens.HomeScreen
import ru.vincetti.vimoney.screens.SettingsScreen
import ru.vincetti.vimoney.utils.TestUtils

@RunWith(AndroidJUnit4::class)
class SettingsTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.simple {
        beforeEachTest {
            TestUtils.openSettings()
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
            SettingsScreen {
                toolbarBackButton.isDisplayed()
                categoriesButton.isDisplayed()
                saveJSONButton.isDisplayed()
                loadJSONButton.isDisplayed()
            }
        }
    }

    @Test
    fun checkBackButton() {
        run {
            step("Click back"){
                SettingsScreen {
                    toolbarBackButton.click()
                }
            }
            step("Check home screen"){
                HomeScreen {
                    homeFab.isDisplayed()
                }
            }
        }
    }
}
