package ru.vincetti.vimoney.data.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.AccountListModel

class AllCardsListRVAdapter(
        private val listener: (Int) -> Unit
) : RecyclerView.Adapter<AllCardsListRVAdapter.CardsViewHolder>() {
    private var data: List<AccountListModel>? = null

    inner class CardsViewHolder(
            itemView: View,
            val listener: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val accName: TextView = itemView.findViewById(R.id.acc_name)
        val accType: TextView = itemView.findViewById(R.id.acc_type)
        val accBalance: TextView = itemView.findViewById(R.id.acc_balance)
        val isArchive: TextView = itemView.findViewById(R.id.acc_archive)
        val accSymbol: TextView = itemView.findViewById(R.id.acc_symbol)
        val accLabel: View = itemView.findViewById(R.id.acc_label)

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
                .inflate(R.layout.item_all_cards_list, parent, false)
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
            if (!tmpAcc.isArchive) {
                holder.isArchive.visibility = View.INVISIBLE
            } else {
                holder.isArchive.visibility = View.VISIBLE
            }
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
