package ru.vincetti.vimoney.data.adapters

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.TransactionListModel
import java.text.DateFormat

class TransactionsViewHolder(
    itemView: View,
    val listener: (Int) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val container: View = itemView.findViewById(R.id.card_transactions_root)
    private val icon: TextView = itemView.findViewById(R.id.card_transactions_image)
    private val name: TextView = itemView.findViewById(R.id.home_transactions_name)
    private val date: TextView = itemView.findViewById(R.id.home_transactions_date)
    private val acc: TextView = itemView.findViewById(R.id.home_transactions_account)
    private val sum: TextView = itemView.findViewById(R.id.home_transactions_balance)
    private val cur: TextView = itemView.findViewById(R.id.home_transactions_currency)

    fun bind(item: TransactionListModel) {
        icon.text = item.symbol
        name.text = item.description
        date.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(item.date)
        sum.text = "${item.getTypeString()} ${item.sum}"
        acc.text = item.accountName
        cur.text = item.curSymbol

        container.setOnClickListener {
            listener(item.id)
        }
    }
}
