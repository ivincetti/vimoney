package ru.vincetti.vimoney.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_history.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.history.filter.FilterDialog

class AllHistoryFragment : Fragment(R.layout.fragment_history) {

    private val dialogFrag = FilterDialog()
    private val historyFragment = HistoryFragment()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewsInit()
        insetsInit()
        showTransactionsHistory()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            data?.let {
                historyFragment.setHistoryIntent(it)
            }
        }
    }

    private fun viewsInit() {
        records_navigation_back_btn.setOnClickListener {
            findNavController().navigateUp()
        }

        all_history_fab.setOnClickListener {
            findNavController().navigate(R.id.action_global_transactionMainFragment)
        }

        records_filter_btn.setOnClickListener {
            dialogFrag.setTargetFragment(this, 1)
            dialogFrag.show(parentFragmentManager, "Filter")
        }
    }

    private fun showTransactionsHistory() {
        childFragmentManager.beginTransaction()
            .replace(R.id.history_container, historyFragment)
            .commit()
    }

    private fun insetsInit() {
        val fabMargin = all_history_fab.marginBottom
        ViewCompat.setOnApplyWindowInsetsListener(all_history_fab) { view, insets ->
            view.updateMargin(bottom = (insets.systemWindowInsetBottom + fabMargin))
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(history_top_toolbar) { view, insets ->
            view.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(history_container) { view, insets ->
            view.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
    }
}
