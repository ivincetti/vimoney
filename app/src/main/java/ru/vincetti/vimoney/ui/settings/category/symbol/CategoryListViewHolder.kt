package ru.vincetti.vimoney.ui.settings.category.symbol

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.vimoney.databinding.ItemCategoryIconBinding

class CategoryListViewHolder(
    private val binding: ItemCategoryIconBinding,
    action: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.itemCategoryContainer.setOnClickListener {
            val adapterPosition = adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                action(adapterPosition)
            }
        }
    }

    fun bind(symbol: String) {
        binding.itemCategorySymbol.text = symbol
    }

    companion object {

        fun create(parent: ViewGroup, action: (Int) -> Unit): CategoryListViewHolder {
            return CategoryListViewHolder(
                ItemCategoryIconBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                action
            )
        }
    }
}
