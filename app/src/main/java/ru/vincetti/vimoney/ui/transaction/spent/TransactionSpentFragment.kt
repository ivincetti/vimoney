package ru.vincetti.vimoney.ui.transaction.spent

import kotlinx.android.synthetic.main.fragment_add_all.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.ui.transaction.main.TransactionFFFragment

class TransactionSpentFragment : TransactionFFFragment(R.layout.fragment_add_spent) {

    override fun initFragmentPlus() {
        add_btn.setOnClickListener {
            save(TransactionModel.TRANSACTION_TYPE_SPENT)
        }
    }
}