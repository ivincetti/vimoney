package ru.vincetti.vimoney.ui.transaction.main.categorylist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.CategoryModel

class CategoryListViewWrapper private constructor(val itemView: View) {

    var actions: Actions? = null
    private val icon: TextView = itemView.findViewById(R.id.item_category_symbol)
    private val name: TextView = itemView.findViewById(R.id.item_category_name)

    init {
        itemView.setOnClickListener { actions?.categoryChosen() }
    }

    fun bind(category: CategoryModel) {
        icon.text = category.symbol
        name.text = category.name
    }

    companion object {
        fun create(parent: ViewGroup): CategoryListViewWrapper {
            return CategoryListViewWrapper(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_category,
                    parent,
                    false
                )
            )
        }
    }

    interface Actions {
        fun categoryChosen()
    }
}
