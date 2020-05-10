package ru.vincetti.vimoney.ui.settings.category.symbol

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.vimoney.R

class CategorySymbolListAdapter(
        private val data: List<String>,
        private val action: (Int) -> Unit
) : RecyclerView.Adapter<CategorySymbolListAdapter.CategoryListViewHolder>() {

    inner class CategoryListViewHolder(
            itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val icon: TextView = itemView.findViewById(R.id.item_category_symbol)

        fun bind(symbol: String) {
            icon.text = symbol
            itemView.setOnClickListener {
                action(adapterPosition)
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
