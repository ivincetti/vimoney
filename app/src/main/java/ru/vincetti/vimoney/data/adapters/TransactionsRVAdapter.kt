package ru.vincetti.vimoney.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.TransactionListModel

class TransactionsRVAdapter(
    private val mListener: (Int) -> Unit
) : PagedListAdapter<TransactionListModel, TransactionsViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transactions_list, parent, false)
        return TransactionsViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<TransactionListModel>() {

                override fun areItemsTheSame(
                    old: TransactionListModel,
                    new: TransactionListModel
                ) = old.id == new.id

                override fun areContentsTheSame(
                    old: TransactionListModel,
                    new: TransactionListModel
                ) = old.id == new.id
            }
    }
}
