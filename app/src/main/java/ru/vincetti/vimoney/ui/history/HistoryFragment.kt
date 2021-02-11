package ru.vincetti.vimoney.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentHistoryContentBinding
import ru.vincetti.modules.core.models.Filter
import ru.vincetti.vimoney.ui.transaction.TransactionConst
import javax.inject.Inject

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: HistoryViewModel.AssistedFactory

    private var filter = Filter()

    private val viewModel: HistoryViewModel by viewModels {
        HistoryViewModel.provideFactory(viewModelFactory, filter)
    }

    companion object {
        const val DEFAULT_CHECK_COUNT = 20
    }

    private var _binding: FragmentHistoryContentBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHistoryContentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filter = arguments?.let { Filter.createFromBundle(it) } ?: Filter()

        transactionsListInit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setHistoryIntent(intent: Intent) {
        viewModel.filter(Filter.createFromIntent(intent))
    }

    private fun transactionsListInit() {
        val transactionsRVAdapter = TransactionsAdapter(
            object : TransactionViewHolder.Actions {
                override fun onTransactionClicked(id: Int) {
                    viewModel.clickOnElement(id)
                }
            }
        )

        binding.homeTransactionsRecycleView.apply {
            addItemDecoration(createDivider())
            adapter = transactionsRVAdapter
        }

        viewModel.transList.observe(viewLifecycleOwner) { list ->
            transactionsRVAdapter.submitList(list)
        }
        viewModel.needNavigate2Transaction.observe(viewLifecycleOwner) {
            go2Transaction(it)
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

    private fun go2Transaction(id: Int) {
        val bundle = Bundle()
        bundle.putInt(TransactionConst.EXTRA_TRANS_ID, id)
        findNavController().navigate(
            R.id.action_global_transactionMainFragment,
            bundle
        )
    }
}
