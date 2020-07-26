package ru.vincetti.vimoney.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.CategoryModel

class AllCategoriesListRVAdapter(
        private val listener: (Int) -> Unit
) : RecyclerView.Adapter<AllCategoriesListRVAdapter.CardsViewHolder>() {

    private var data: List<CategoryModel>? = null

    inner class CardsViewHolder(
            itemView: View,
            val listener: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        val categorySymbol: TextView = itemView.findViewById(R.id.item_category_icon)
        val categoryName: TextView = itemView.findViewById(R.id.item_category_text)

        init {
            itemView.setOnClickListener {
                data?.let {
                    if (it[adapterPosition].id > 1) listener(it[adapterPosition].id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_categories_list, parent, false)
        return CardsViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        data?.let {
            val tmpAcc = it[position]
            holder.categoryName.text = tmpAcc.name
            holder.categorySymbol.text = tmpAcc.symbol
        }
    }

    override fun getItemCount() = data?.size ?: 0

    fun setList(accList: List<CategoryModel>) {
        data = accList
        notifyDataSetChanged()
    }

}
