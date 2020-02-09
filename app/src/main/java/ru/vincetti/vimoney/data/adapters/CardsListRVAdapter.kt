package ru.vincetti.vimoney.data.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.AccountListModel

class CardsListRVAdapter(
        private val listener: (Int) -> Unit
) : RecyclerView.Adapter<CardsListRVAdapter.CardsViewHolder>() {

    private var data: List<AccountListModel>? = null

    inner class CardsViewHolder(
            itemView: View,
            val listener: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val accName: TextView = itemView.findViewById(R.id.home_acc_name)
        val accType: TextView = itemView.findViewById(R.id.home_acc_type)
        val accBalance: TextView = itemView.findViewById(R.id.home_acc_balance)
        val accSymbol: TextView = itemView.findViewById(R.id.home_acc_symbol)
        val accLabel: View = itemView.findViewById(R.id.home_acc_label)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            data?.let {
                listener(it[adapterPosition].id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cards_list, parent, false)
        return CardsViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        data?.let {
            val tmpAcc = it[position]
            holder.accName.text = tmpAcc.name
            holder.accType.text = tmpAcc.type
            holder.accBalance.text = tmpAcc.sum.toString()
            holder.accSymbol.text = tmpAcc.curSymbol
            holder.accLabel.setBackgroundColor(Color.parseColor(tmpAcc.color))
        }
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    fun setList(accList: List<AccountListModel>) {
        data = accList
        notifyDataSetChanged()
    }

}
