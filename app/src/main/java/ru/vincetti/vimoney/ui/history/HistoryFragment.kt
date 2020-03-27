package ru.vincetti.vimoney.ui.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_history_content.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.adapters.TransactionsRVAdapter
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.ui.transaction.TransactionConst

class HistoryFragment : Fragment(R.layout.fragment_history_content) {

    private val viewModel: HistoryViewModel by viewModels { viewModelFactory }

    private lateinit var viewModelFactory: HistoryViewModelFactory

    companion object {
        const val BUNDLE_TRANS_COUNT_NAME = "ru.vincetti.vimoney.transhistory_count"
        const val BUNDLE_TRANS_CHECK_ID_NAME = "ru.vincetti.vimoney.transhistory_check_id"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(activity).application
        val db = AppDatabase.getInstance(application)
        var trCount = HistoryViewModel.DEFAULT_TRANSACTIONS
        val args = arguments
        if (args !== null) {
            // количество элементов
            if (args.containsKey(BUNDLE_TRANS_COUNT_NAME)) {
                trCount = args.getInt(BUNDLE_TRANS_COUNT_NAME, HistoryViewModel.DEFAULT_TRANSACTIONS)
            }
            // уточнение счета
            viewModelFactory = if (args.containsKey(BUNDLE_TRANS_CHECK_ID_NAME)) {
                HistoryViewModelFactory(
                        db.transactionDao(),
                        trCount,
                        args.getInt(BUNDLE_TRANS_CHECK_ID_NAME))
            } else {
                HistoryViewModelFactory(
                        db.transactionDao(),
                        trCount,
                        null)
            }
        } else {
            //TODO no need I think
            viewModelFactory = HistoryViewModelFactory(
                    db.transactionDao(),
                    trCount,
                    null)
        }

        // список транзакций
        val transactionsRVAdapter = TransactionsRVAdapter { itemId ->
            val bundle = Bundle()
            bundle.putInt(TransactionConst.EXTRA_TRANS_ID, itemId)
            findNavController().navigate(
                    R.id.action_global_transactionMainFragment,
                    bundle)
        }
        home_transactions_recycle_view.setHasFixedSize(true)
        val trLayoutManager = LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL, false)
        home_transactions_recycle_view.layoutManager = trLayoutManager
        home_transactions_recycle_view.adapter = transactionsRVAdapter

        viewModel.transList.observe(viewLifecycleOwner, Observer { trList ->
            trList?.let {
                transactionsRVAdapter.setTransaction(trList)
            }
        })
    }
}
