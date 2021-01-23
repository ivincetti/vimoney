package ru.vincetti.vimoney.ui.settings.category

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.vimoney.data.models.CategoryModel
import ru.vincetti.vimoney.ui.settings.category.list.CategoryViewHolder

class AllCategoriesAdapter(
    private val actions: CategoryViewHolder.Actions
) : RecyclerView.Adapter<CategoryViewHolder>() {

    private var data: List<CategoryModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.create(parent, actions)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        data?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount() = data?.size ?: 0

    fun setList(categories: List<CategoryModel>?) {
        categories?.let {
            data = it
            notifyDataSetChanged()
        }
    }
}
