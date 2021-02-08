package ru.vincetti.vimoney.ui.transaction.transfer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentAddTransferBinding
import ru.vincetti.vimoney.ui.transaction.main.TransactionFragmentUtils
import ru.vincetti.vimoney.ui.transaction.main.TransactionMainViewModel
import java.text.DateFormat
import java.util.*

class TransactionTransferFragment : Fragment() {

    val viewModel: TransactionMainViewModel by viewModels({ requireParentFragment() })

    private lateinit var date: Date

    private var _binding: FragmentAddTransferBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAddTransferBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        initObservers()
    }

    override fun onResume() {
        super.onResume()

        binding.addSum.apply {
            isFocusableInTouchMode = true
            requestFocus()
        }
        TransactionFragmentUtils.showKeyboard(requireActivity())
    }

    override fun onPause() {
        viewModel.setSum(binding.addSum.text.toString())
        viewModel.setDescription(binding.fragmentAddAllContent.addDesc.text.toString())

        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun save() {
        viewModel.saveTransactionTo(
            Transaction.TRANSACTION_TYPE_TRANSFER,
            binding.fragmentAddAllContent.addDesc.text.toString(),
            binding.addSum.text.toString(),
            binding.addSumTo.text.toString()
        )
    }

    private fun initViews() {
        binding.fragmentAddAllContent.addDateBlock.setOnClickListener {
            TransactionFragmentUtils.showDateDialog(
                requireContext(),
                date,
                viewModel::setDate
            )
        }
        binding.fragmentAddAllContent.addBtn.setOnClickListener { save() }
        binding.addSum.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.addSumTo.setText(s)
            }
        })
    }

    private fun initObservers() {
        viewModel.needSum.observe(viewLifecycleOwner) {
            if (it) TransactionFragmentUtils.showNoSumToast(requireContext())
        }
        viewModel.needAccount.observe(viewLifecycleOwner) {
            if (it) TransactionFragmentUtils.showNoAccountToast(requireContext())
        }
        viewModel.needToUpdate.observe(viewLifecycleOwner) {
            if (it) binding.fragmentAddAllContent.addBtn.text = getString(R.string.add_btn_update)
        }
        viewModel.account.observe(viewLifecycleOwner) {
            it?.let { binding.addAccName.text = it.name }
        }
        viewModel.currency.observe(viewLifecycleOwner) {
            it?.let { binding.addAccCur.text = it.symbol }
        }
        viewModel.sum.observe(viewLifecycleOwner) {
            if (it > 0) binding.addSum.setText(it.toString())
        }
        viewModel.date.observe(viewLifecycleOwner) {
            it?.let {
                date = it
                binding.fragmentAddAllContent.addDateTxt.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(it)
            }
        }
        viewModel.description.observe(viewLifecycleOwner) {
            binding.fragmentAddAllContent.addDesc.setText(it)
        }
        viewModel.nestedTransaction.observe(viewLifecycleOwner) {
            it?.let { if (it.sum > 0) binding.addSumTo.setText(it.sum.toString()) }
        }
        viewModel.accountTo.observe(viewLifecycleOwner) {
            it?.let { binding.addAccNameTo.text = it.name }
        }
        viewModel.currencyTo.observe(viewLifecycleOwner) {
            it?.let { binding.addAccCurTo.text = it.symbol }
        }
        viewModel.accountNotArchiveNames.observe(viewLifecycleOwner) {
            it?.let { list ->
                binding.addAccName.setOnClickListener { view ->
                    TransactionFragmentUtils.showListPopUp(
                        requireContext(),
                        view,
                        list,
                        viewModel::setAccount
                    )
                }
                binding.addAccNameTo.setOnClickListener { view ->
                    TransactionFragmentUtils.showListPopUp(
                        requireContext(),
                        view,
                        list,
                        viewModel::setAccountTo
                    )
                }
            }
        }
        viewModel.needToNavigate.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigateUp()
                viewModel.navigatedBack()
            }
        }
    }
}
