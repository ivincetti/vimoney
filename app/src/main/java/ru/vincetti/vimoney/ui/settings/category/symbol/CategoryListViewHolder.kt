package ru.vincetti.vimoney.ui.settings.category.symbol

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CategoryListViewHolder(
        private val viewWrapper: CategoryListViewWrapper,
        action: (Int) -> Unit
) : RecyclerView.ViewHolder(viewWrapper.itemView) {
    
    init {
        viewWrapper.actions = object : CategoryListViewWrapper.Actions{
            override fun iconChosen() {
                val adapterPosition = adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION){
                    action(adapterPosition)
                }
            }
        }
    }
    
    fun bind(symbol: String){
        viewWrapper.bind(symbol)
    }

    companion object {

        fun create(parent: ViewGroup, action: (Int) -> Unit): CategoryListViewHolder {
            return CategoryListViewHolder(
                    CategoryListViewWrapper.create(parent),
                    action
            )
        }
    }
}