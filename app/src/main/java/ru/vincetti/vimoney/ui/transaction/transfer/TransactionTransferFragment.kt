package ru.vincetti.vimoney.ui.transaction.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
//        binding.addSum.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) = Unit
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                binding.addSumTo.setText(s)
//            }
//        })
    }

    private fun initObservers() {
        viewModel.needToUpdate.observe(viewLifecycleOwner) {
            if (it) binding.fragmentAddAllContent.addBtn.text = getString(R.string.add_btn_update)
        }

        viewModel.accountName.observe(viewLifecycleOwner) { binding.addAccName.text = it }
        viewModel.currency.observe(viewLifecycleOwner) { binding.addAccCur.text = it }
        viewModel.sum.observe(viewLifecycleOwner) {
            if (it > 0) binding.addSum.setText(it.toString())
        }
        viewModel.date.observe(viewLifecycleOwner) {
            date = it
            binding.fragmentAddAllContent.addDateTxt.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(it)
        }
        viewModel.description.observe(viewLifecycleOwner) { binding.fragmentAddAllContent.addDesc.setText(it) }
        viewModel.nestedTransaction.observe(viewLifecycleOwner) {
            if (it.sum > 0) binding.addSumTo.setText(it.sum.toString())
        }
        viewModel.accountToName.observe(viewLifecycleOwner) { binding.addAccNameTo.text = it }
        viewModel.currencyTo.observe(viewLifecycleOwner) { binding.addAccCurTo.text = it }
        viewModel.accountNotArchiveNames.observe(viewLifecycleOwner) {
            binding.addAccName.setOnClickListener { view ->
                TransactionFragmentUtils.showListPopUp(
                    requireContext(),
                    view,
                    it,
                    viewModel::setAccount
                )
            }
            binding.addAccNameTo.setOnClickListener { view ->
                TransactionFragmentUtils.showListPopUp(
                    requireContext(),
                    view,
                    it,
                    viewModel::setAccountTo
                )
            }
        }

        viewModel.needToNavigateBack.observe(viewLifecycleOwner) { findNavController().navigateUp() }
        viewModel.showNeedSum.observe(viewLifecycleOwner) { TransactionFragmentUtils.showNoSumToast(requireContext()) }
        viewModel.showNeedAccount.observe(viewLifecycleOwner) {
            TransactionFragmentUtils.showNoAccountToast(requireContext())
        }
    }
}
