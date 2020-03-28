package ru.vincetti.vimoney.ui.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_history.*
import ru.vincetti.vimoney.R

class AllHistoryFragment : Fragment(R.layout.fragment_history) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setting_navigation_back_btn.setOnClickListener {
            findNavController().navigate(R.id.action_allHistoryFragment_to_homeFragment)
        }

        showTransactionsHistory()
    }

    /** Show historyFragment. */
    private fun showTransactionsHistory() {
        val historyFragment = HistoryFragment()
        childFragmentManager.beginTransaction()
                .replace(R.id.history_main_container, historyFragment)
                .commit()
    }
}
