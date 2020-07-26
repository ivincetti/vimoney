package ru.vincetti.vimoney.ui.transaction.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.CategoryModel

class CategoryListAdapter(
        private val data: List<CategoryModel>,
        private val action: (Int) -> Unit
) : RecyclerView.Adapter<CategoryListAdapter.CategoryListViewHolder>() {

    inner class CategoryListViewHolder(
            itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val icon: TextView = itemView.findViewById(R.id.item_category_symbol)
        private val name: TextView = itemView.findViewById(R.id.item_category_name)

        fun bind(category: CategoryModel) {
            icon.text = category.symbol
            name.text = category.name
            itemView.setOnClickListener {
                action(category.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        return CategoryListViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.item_category,
                        parent,
                        false
                )
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        holder.bind(data[position])
    }

}
