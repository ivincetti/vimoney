package ru.vincetti.vimoney.ui.check.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.vimoney.databinding.ItemAllCardsListBinding

class CardViewHolder private constructor(
    private val binding: ItemAllCardsListBinding,
    private val actions: Actions
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(account: AccountList) {
        binding.accName.text = account.name
        binding.accType.text = account.type
        binding.accBalance.text = account.sum.toString()
        binding.accSymbol.text = account.curSymbol
        binding.accLabel.setBackgroundColor(Color.parseColor(account.color))
        binding.accArchive.isInvisible = !account.isArchive

        binding.accListContainer.setOnClickListener {
            actions.onCardClicked(account.id)
        }
    }

    companion object {

        fun create(parent: ViewGroup, actions: Actions): CardViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return CardViewHolder(
                ItemAllCardsListBinding.inflate(layoutInflater, parent, false),
                actions,
            )
        }
    }

    fun interface Actions {

        fun onCardClicked(id: Int)
    }
}
