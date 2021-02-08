package ru.vincetti.vimoney.ui.settings.category.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.modules.core.models.Category
import ru.vincetti.vimoney.databinding.ItemCategoriesListBinding

class CategoryViewHolder private constructor(
    private val binding: ItemCategoriesListBinding,
    private val actions: Actions
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(category: Category) {
        binding.itemCategoryText.text = category.name
        binding.itemCategoryIcon.text = category.symbol

        binding.itemCategoryContainer.setOnClickListener {
            actions.onCategoryClicked(category.id)
        }
    }

    companion object {

        fun create(
            parent: ViewGroup,
            actions: Actions
        ) = CategoryViewHolder(
            ItemCategoriesListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            actions
        )
    }

    interface Actions {

        fun onCategoryClicked(id: Int)
    }
}
