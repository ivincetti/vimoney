package ru.vincetti.vimoney.ui.transaction.income

import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_add_all.*
import kotlinx.android.synthetic.main.fragment_add_spent.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.ui.transaction.main.TransactionFFFragment

class TransactionIncomeFragment : TransactionFFFragment(R.layout.fragment_add_spent) {

    override fun initFragmentPlus() {
        add_acc_category_block.setOnClickListener { showCategoryDialog() }
        add_btn.setOnClickListener { save(TransactionModel.TRANSACTION_TYPE_INCOME) }

        viewModel.category.observe(viewLifecycleOwner, Observer {
            it?.let {
                add_acc_category_icon.text = it.symbol
                add_acc_category_name.text = it.name
            }
        })
        viewModel.categoriesList.observe(viewLifecycleOwner, Observer {
            it?.let {
                dialogFrag.setTargetFragment(this, 1)
                dialogFrag.setList(it)
            }
        })
    }
}
