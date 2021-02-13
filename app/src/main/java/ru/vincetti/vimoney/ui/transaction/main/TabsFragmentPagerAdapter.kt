package ru.vincetti.vimoney.ui.transaction.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.vimoney.ui.transaction.income.TransactionIncomeFragment
import ru.vincetti.vimoney.ui.transaction.spent.TransactionSpentFragment
import ru.vincetti.vimoney.ui.transaction.transfer.TransactionTransferFragment

class TabsFragmentPagerAdapter(
    fm: FragmentManager,
    private val fragmentBundle: Bundle
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            Transaction.TRANSACTION_TYPE_SPENT_TAB -> "-"
            Transaction.TRANSACTION_TYPE_INCOME_TAB -> "+"
            Transaction.TRANSACTION_TYPE_TRANSFER_TAB -> "="
//            Transaction.TRANSACTION_TYPE_DEBT_TAB->"|"
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
                transferFragment
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
