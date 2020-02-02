package ru.vincetti.vimoney.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.TransactionListModel
import java.text.DateFormat

class TransactionsRVAdapter(private val mListener: OnTransactionClickListener
) : RecyclerView.Adapter<TransactionsRVAdapter.ViewHolder>() {

    private var data: List<TransactionListModel>? = null

    inner class ViewHolder(
            itemView: View, val listener: OnTransactionClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val name: TextView = itemView.findViewById(R.id.home_transactions_name)
        val date: TextView = itemView.findViewById(R.id.home_transactions_date)
        val acc: TextView = itemView.findViewById(R.id.home_transactions_account)
        val sum: TextView = itemView.findViewById(R.id.home_transactions_balance)
        val cur: TextView = itemView.findViewById(R.id.home_transactions_currency)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            data?.let {
                listener.onTrClick(it[adapterPosition].id)
            }
        }

        fun bind(item: TransactionListModel) {
            name.text = item.description
            date.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(item.date)
            sum.text = "${item.getTypeString()} ${item.sum}"
            acc.text = item.accountName
            cur.text = item.curSymbol
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transactions_list
                , parent, false)
        return ViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        data?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    interface OnTransactionClickListener {
        fun onTrClick(itemId: Int)
    }

    fun setTransaction(transList: List<TransactionListModel>) {
        data = transList
        notifyDataSetChanged()
    }
}
