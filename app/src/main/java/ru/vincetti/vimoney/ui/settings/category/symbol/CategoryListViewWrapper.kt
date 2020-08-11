package ru.vincetti.vimoney.ui.settings.category.symbol

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.vincetti.vimoney.R

class CategoryListViewWrapper(val itemView: View) {

    var actions: Actions? = null

    private val icon: TextView = itemView.findViewById(R.id.item_category_symbol)

    init {
        itemView.setOnClickListener { actions?.iconChosen() }
    }

    fun bind(symbol: String) {
        icon.text = symbol
    }

    companion object {
        fun create(parent: ViewGroup): CategoryListViewWrapper {
            return CategoryListViewWrapper(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_category_icon,
                    parent,
                    false
                )
            )
        }
    }

    interface Actions {

        fun iconChosen()
    }
}
