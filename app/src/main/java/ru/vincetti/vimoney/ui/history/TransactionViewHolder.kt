package ru.vincetti.vimoney.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.modules.database.sqlite.models.TransactionListModel
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.ItemTransactionsListBinding
import java.text.DateFormat
import java.text.DecimalFormat

class TransactionViewHolder private constructor(
    private val binding: ItemTransactionsListBinding,
    private val actions: Actions
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: TransactionListModel) {
        binding.cardTransactionImage.text = item.symbol
        binding.cardTransactionName.text = item.description
        binding.cardTransactionDate.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(item.date)
        binding.cardTransactionSum.text = binding.root.context.getString(
            R.string.history_simple_2_string,
            item.getTypeString(),
            DecimalFormat("#.##").format(item.sum),
        )
        binding.cardTransactionAccount.text = item.accountName
        binding.cardTransactionCurrency.text = item.curSymbol

        binding.cardTransactionRoot.setOnClickListener {
            actions.onTransactionClicked(item.id)
        }
    }

    companion object {

        fun create(parent: ViewGroup, actions: Actions): TransactionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return TransactionViewHolder(
                ItemTransactionsListBinding.inflate(layoutInflater, parent, false),
                actions,
            )
        }
    }

    fun interface Actions {

        fun onTransactionClicked(id: Int)
    }
}
