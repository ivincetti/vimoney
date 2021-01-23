package ru.vincetti.vimoney.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentHistoryBinding
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.history.filter.FilterDialog

class AllHistoryFragment : Fragment() {

    private val dialogFrag = FilterDialog()
    private val historyFragment = HistoryFragment()

    private var _binding: FragmentHistoryBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun viewsInit() {
        binding.recordsNavigationBackBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.allHistoryFab.setOnClickListener {
            findNavController().navigate(R.id.action_global_transactionMainFragment)
        }
        binding.recordsFilterBtn.setOnClickListener {
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
        val fabMargin = binding.allHistoryFab.marginBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.allHistoryFab) { view, insets ->
            view.updateMargin(bottom = (insets.systemWindowInsetBottom + fabMargin))
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.historyTopToolbar) { view, insets ->
            view.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.historyContainer) { view, insets ->
            view.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
    }
}
