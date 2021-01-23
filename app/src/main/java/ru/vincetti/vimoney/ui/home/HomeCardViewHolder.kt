package ru.vincetti.vimoney.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.databinding.ItemCardsListBinding

class HomeCardViewHolder private constructor(
    private val binding: ItemCardsListBinding,
    private val actions: Actions
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(account: AccountListModel) {
        binding.homeAccName.text = account.name
        binding.homeAccType.text = account.type
        binding.homeAccBalance.text = account.sum.toString()
        binding.homeAccSymbol.text = account.curSymbol
        binding.homeAccLabel.setBackgroundColor(Color.parseColor(account.color))

        binding.homeAccContainer.setOnClickListener {
            actions.onCardClicked(account.id)
        }
    }

    companion object {

        fun create(
            parent: ViewGroup,
            actions: Actions
        ) = HomeCardViewHolder(
            ItemCardsListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            actions
        )
    }

    interface Actions {

        fun onCardClicked(id: Int)
    }
}
