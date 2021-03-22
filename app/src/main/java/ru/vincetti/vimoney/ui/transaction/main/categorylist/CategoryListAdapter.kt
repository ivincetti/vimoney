package ru.vincetti.vimoney.ui.transaction.main.categorylist

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.modules.core.models.Category

class CategoryListAdapter(
    private var data: List<Category>,
    private val action: (Int) -> Unit
) : RecyclerView.Adapter<CategoryListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        return CategoryListViewHolder.create(parent, action)
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        val category = data[position]
        holder.bind(category)
    }

    fun setItems(newList: List<Category>) {
        data = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size
}
