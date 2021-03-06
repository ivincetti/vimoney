package ru.vincetti.vimoney.screens

import android.view.View
import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.text.KButton
import com.agoda.kakao.text.KTextView
import com.kaspersky.kaspresso.screens.KScreen
import org.hamcrest.Matcher
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.ui.settings.category.list.CategoriesFragment

object CategoriesScreen : KScreen<CategoriesScreen>() {

    override val layoutId: Int? = R.layout.fragment_categories_list
    override val viewClass: Class<*>? = CategoriesFragment::class.java

    val toolbarBackButton = KButton {
        withId(R.id.categories_navigation_back_btn)
    }
    val fab = KButton {
        withId(R.id.categories_list_fab)
    }
    val list = KRecyclerView(
        builder = {
            withId(R.id.categories_list_recycle_view)
            isDisplayed()
        },
        itemTypeBuilder = {
            itemType(::CategoryListItem)
        }
    )

    class CategoryListItem(parent: Matcher<View>) : KRecyclerItem<CategoryListItem>(parent) {
        val icon = KTextView(parent) {
            withId(R.id.item_category_icon)
            isDisplayed()
        }
        val label = KButton(parent) {
            withId(R.id.item_category_text)
            isDisplayed()
        }
    }
}
