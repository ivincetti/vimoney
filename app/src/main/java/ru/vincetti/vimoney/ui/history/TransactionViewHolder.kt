package ru.vincetti.vimoney.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.vimoney.data.models.TransactionListModel
import ru.vincetti.vimoney.databinding.ItemTransactionsListBinding
import java.text.DateFormat

class TransactionViewHolder private constructor(
    private val binding: ItemTransactionsListBinding,
    private val actions: Actions
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: TransactionListModel) {
        binding.cardTransactionImage.text = item.symbol
        binding.cardTransactionName.text = item.description
        binding.cardTransactionDate.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(item.date)
        binding.cardTransactionSum.text = "${item.getTypeString()} ${item.sum}"
        binding.cardTransactionAccount.text = item.accountName
        binding.cardTransactionCurrency.text = item.curSymbol

        binding.cardTransactionRoot.setOnClickListener {
            actions.onTransactionClicked(item.id)
        }
    }

    companion object {

        fun create(
            parent: ViewGroup,
            actions: Actions
        ) = TransactionViewHolder(
            ItemTransactionsListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            actions
        )
    }

    interface Actions {

        fun onTransactionClicked(id: Int)
    }
}
