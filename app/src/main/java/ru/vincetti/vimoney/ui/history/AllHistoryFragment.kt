package ru.vincetti.vimoney.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentHistoryBinding

class AllHistoryFragment : Fragment() {

    lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHistoryBinding.inflate(inflater)
        viewInit()
        showTransactionsHistory()
        return binding.root
    }

    private fun viewInit() {
        binding.settingNavigationBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_allHistoryFragment_to_homeFragment)
        }
    }

    // Show historyFragment
    private fun showTransactionsHistory() {
        val historyFragment = HistoryFragment()
        childFragmentManager.beginTransaction()
                .replace(R.id.history_main_container, historyFragment)
                .commit()
    }
}
