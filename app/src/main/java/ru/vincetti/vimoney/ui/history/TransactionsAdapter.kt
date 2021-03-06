package ru.vincetti.vimoney.ui.history

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import ru.vincetti.modules.database.sqlite.models.TransactionListModel

class TransactionsAdapter(
    private val actions: TransactionViewHolder.Actions
) : PagedListAdapter<TransactionListModel, TransactionViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder.create(parent, actions)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
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
                ): Boolean {
                    return (
                        old.accountName == new.accountName &&
                            old.type == new.type &&
                            old.date == new.date &&
                            old.description == new.description &&
                            old.sum == new.sum &&
                            old.symbol == new.symbol &&
                            old.curSymbol == new.curSymbol
                        )
                }
            }
    }
}
