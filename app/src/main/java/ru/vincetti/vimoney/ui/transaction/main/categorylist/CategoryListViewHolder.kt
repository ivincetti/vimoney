package ru.vincetti.vimoney.ui.transaction.main.categorylist

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.vimoney.data.models.CategoryModel

class CategoryListViewHolder private constructor(
        private val viewWrapper: CategoryListViewWrapper,
        private val action: (Int) -> Unit
) : RecyclerView.ViewHolder(viewWrapper.itemView) {

    init {
        viewWrapper.actions = object : CategoryListViewWrapper.Actions {
            override fun categoryChosen() {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    action(position)
                }
            }
        }
    }

    fun bind(category: CategoryModel) {
        viewWrapper.bind(category)
    }

    companion object {
        fun create(
                parent: ViewGroup,
                action: (Int) -> Unit
        ): CategoryListViewHolder {
            return CategoryListViewHolder(
                    CategoryListViewWrapper.create(parent),
                    action
            )
        }
    }
}
