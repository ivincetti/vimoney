package ru.vincetti.vimoney.ui.settings.category.symbol

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CategorySymbolListAdapter(
    private val data: List<String>,
    private val action: (Int) -> Unit
) : RecyclerView.Adapter<CategoryListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        return CategoryListViewHolder.create(parent, action)
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size
}
