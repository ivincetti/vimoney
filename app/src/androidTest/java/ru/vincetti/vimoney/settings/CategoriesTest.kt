package ru.vincetti.vimoney.settings

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.vincetti.vimoney.MainActivity
import ru.vincetti.vimoney.screens.CategoriesScreen
import ru.vincetti.vimoney.screens.SettingsScreen
import ru.vincetti.vimoney.utils.TestUtils

@RunWith(AndroidJUnit4::class)
class CategoriesTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.simple {
        beforeEachTest {
            TestUtils.openCategories()
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
            CategoriesScreen {
                toolbarBackButton.isDisplayed()
                fab.isDisplayed()
            }
        }
    }

    @Test
    fun checkBackButton() {
        run {
            step("Click back") {
                CategoriesScreen {
                    toolbarBackButton.click()
                }
            }
            step("Check settings screen") {
                SettingsScreen {
                    categoriesButton.isDisplayed()
                }
            }
        }
    }

    @Test
    fun checkDefaultCategory() {
        run {
            CategoriesScreen {
                list.childAt<CategoriesScreen.CategoryListItem>(0) {
                    label.hasText("?")
                }
            }
        }
    }
}
