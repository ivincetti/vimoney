package ru.vincetti.vimoney.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.vimoney.databinding.ItemCardsListBinding

class HomeCardViewHolder private constructor(
    private val binding: ItemCardsListBinding,
    private val actions: Actions,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(account: AccountList) {
        binding.apply {
            homeAccName.text = account.name
            homeAccType.text = account.type
            homeAccBalance.text = account.sum.toString()
            homeAccSymbol.text = account.curSymbol
            homeAccLabel.setBackgroundColor(Color.parseColor(account.color))

            homeAccContainer.setOnClickListener {
                actions.onCardClicked(account.id)
            }
        }
    }

    companion object {

        fun create(parent: ViewGroup, actions: Actions): HomeCardViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)

            return HomeCardViewHolder(
                ItemCardsListBinding.inflate(layoutInflater, parent, false),
                actions,
            )
        }
    }

    fun interface Actions {
        fun onCardClicked(id: Int)
    }
}
