package ru.vincetti.vimoney.ui.check

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.vimoney.ui.check.view.CardViewHolder

class AllCardsAdapter(
    private val actions: CardViewHolder.Actions
) : RecyclerView.Adapter<CardViewHolder>() {

    private var data: List<AccountList>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder.create(parent, actions)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        data?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount() = data?.size ?: 0

    fun setList(accList: List<AccountList>?) {
        accList?.let {
            data = accList
            notifyDataSetChanged()
        }
    }
}
