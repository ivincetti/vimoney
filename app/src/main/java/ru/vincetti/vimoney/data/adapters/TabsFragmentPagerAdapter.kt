package ru.vincetti.vimoney.data.adapters

import android.os.Bundle

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.ui.transaction.income.TransactionIncomeFragment
import ru.vincetti.vimoney.ui.transaction.spent.TransactionSpentFragment
import ru.vincetti.vimoney.ui.transaction.transfer.TransactionTransferFragment

class TabsFragmentPagerAdapter(
        fm: FragmentManager, private val fragmentBundle: Bundle
) : FragmentPagerAdapter(fm) {

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            TransactionModel.TRANSACTION_TYPE_SPENT_TAB -> "-"
            TransactionModel.TRANSACTION_TYPE_INCOME_TAB -> "+"
            TransactionModel.TRANSACTION_TYPE_TRANSFER_TAB -> "="
//            TransactionModel.TRANSACTION_TYPE_DEBT_TAB->"|"
            else -> "Error"
        }
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> {
                val incomeFragment = TransactionIncomeFragment()
                incomeFragment.arguments = this.fragmentBundle
                incomeFragment
            }
            2 -> {
                val transferFragment = TransactionTransferFragment()
                transferFragment.arguments = this.fragmentBundle
                return transferFragment
            }
//            case 3:
//                return new TransactionDebtFragment();
            else -> {
                val spentFragment = TransactionSpentFragment()
                spentFragment.arguments = this.fragmentBundle
                spentFragment
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }
}
