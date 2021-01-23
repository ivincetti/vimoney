package ru.vincetti.vimoney.ui.check

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.ui.check.view.CardViewHolder

class AllCardsAdapter(
    private val actions: CardViewHolder.Actions
) : RecyclerView.Adapter<CardViewHolder>() {

    private var data: List<AccountListModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder.create(parent, actions)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        data?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount() = data?.size ?: 0

    fun setList(accList: List<AccountListModel>?) {
        accList?.let {
            data = accList
            notifyDataSetChanged()
        }
    }
}
