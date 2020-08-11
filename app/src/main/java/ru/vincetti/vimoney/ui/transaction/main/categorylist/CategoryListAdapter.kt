package ru.vincetti.vimoney.ui.transaction.main.categorylist

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.vimoney.data.models.CategoryModel

class CategoryListAdapter(
    private val data: List<CategoryModel>,
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
