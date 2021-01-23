package ru.vincetti.vimoney.ui.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.vimoney.data.models.AccountListModel

class HomeCardsAdapter(
    private val actions: HomeCardViewHolder.Actions
) : RecyclerView.Adapter<HomeCardViewHolder>() {

    private var data: List<AccountListModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCardViewHolder {
        return HomeCardViewHolder.create(parent, actions)
    }

    override fun onBindViewHolder(holder: HomeCardViewHolder, position: Int) {
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
