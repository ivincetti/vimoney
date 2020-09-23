package ru.vincetti.vimoney.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_history_content.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.adapters.TransactionsRVAdapter
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.ui.history.filter.Filter
import ru.vincetti.vimoney.ui.transaction.TransactionConst

class HistoryFragment : Fragment(R.layout.fragment_history_content) {

    private val viewModel: HistoryViewModel by viewModels { viewModelFactory }

    private lateinit var viewModelFactory: HistoryViewModelFactory

    companion object {
        const val DEFAULT_CHECK_COUNT = 20
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getInstance(requireNotNull(activity).application)
        val filter = arguments?.let { Filter.createFromBundle(it) } ?: Filter()
        viewModelFactory = HistoryViewModelFactory(db, filter)

        transactionsListInit()
    }

    fun setHistoryIntent(intent: Intent) {
        viewModel.filter(Filter.createFromIntent(intent))
    }

    private fun transactionsListInit() {
        val transactionsRVAdapter = TransactionsRVAdapter { itemId ->
            val bundle = Bundle()
            bundle.putInt(TransactionConst.EXTRA_TRANS_ID, itemId)
            findNavController().navigate(
                R.id.action_global_transactionMainFragment,
                bundle
            )
        }

        home_transactions_recycle_view.apply {
            addItemDecoration(createDivider())
            adapter = transactionsRVAdapter
        }

        viewModel.transList.observe(viewLifecycleOwner) { list ->
            transactionsRVAdapter.submitList(list)
        }
    }

    private fun createDivider(): DividerItemDecoration {
        val lineDivider = DividerItemDecoration(
            requireContext(),
            DividerItemDecoration.VERTICAL
        )
        lineDivider.setDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.light_divider
            )!!
        )
        return lineDivider
    }
}
