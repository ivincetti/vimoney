package ru.vincetti.vimoney.ui.check.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.databinding.ItemAllCardsListBinding

class CardViewHolder private constructor(
    private val binding: ItemAllCardsListBinding,
    private val actions: Actions
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(account: AccountListModel) {
        binding.accName.text = account.name
        binding.accType.text = account.type
        binding.accBalance.text = account.sum.toString()
        binding.accSymbol.text = account.curSymbol
        binding.accLabel.setBackgroundColor(Color.parseColor(account.color))
        if (!account.isArchive) {
            binding.accArchive.visibility = View.INVISIBLE
        } else {
            binding.accArchive.visibility = View.VISIBLE
        }

        binding.accListContainer.setOnClickListener {
            actions.onCardClicked(account.id)
        }
    }

    companion object {

        fun create(
            parent: ViewGroup,
            actions: Actions
        ) = CardViewHolder(
            ItemAllCardsListBinding.inflate(
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
