package ru.vincetti.vimoney.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_history_content.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.adapters.TransactionsRVAdapter
import ru.vincetti.vimoney.ui.history.filter.Filter
import ru.vincetti.vimoney.ui.transaction.TransactionConst
import javax.inject.Inject

@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_history_content) {

    @Inject
    lateinit var viewModelFactory: HistoryViewModel.AssistedFactory

    private var filter = Filter()

    private val viewModel: HistoryViewModel by viewModels {
        HistoryViewModel.provideFactory(viewModelFactory, filter)
    }

    companion object {
        const val DEFAULT_CHECK_COUNT = 20
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filter = arguments?.let { Filter.createFromBundle(it) } ?: Filter()

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
