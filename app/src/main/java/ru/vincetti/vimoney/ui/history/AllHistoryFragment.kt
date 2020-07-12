package ru.vincetti.vimoney.ui.history

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_history.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.extensions.updateMargin

class AllHistoryFragment : Fragment(R.layout.fragment_history) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setting_navigation_back_btn.setOnClickListener {
            findNavController().navigate(R.id.action_allHistoryFragment_to_homeFragment)
        }

        showTransactionsHistory()
        insetsInit()
    }

    /** Show historyFragment. */
    private fun showTransactionsHistory() {
        val historyFragment = HistoryFragment()
        childFragmentManager.beginTransaction()
                .replace(R.id.history_main_container, historyFragment)
                .commit()
    }

    private fun insetsInit(){
        ViewCompat.setOnApplyWindowInsetsListener(history_top_toolbar) { _, insets ->
            history_top_toolbar.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(history_container_root) { _, insets ->
            history_container_root.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
    }
}
