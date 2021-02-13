package ru.vincetti.vimoney.ui.history.filter

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.vincetti.modules.core.models.Filter
import ru.vincetti.vimoney.databinding.DialogTransactionsFilterBinding
import java.text.DateFormat
import java.util.*

@AndroidEntryPoint
class FilterDialog : BottomSheetDialogFragment() {

    private val viewModel: FilterViewModel by viewModels()

    private var dateFrom: Date? = null
    private var dateTo: Date? = null

    private var _binding: DialogTransactionsFilterBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogTransactionsFilterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViewModel() {
        viewModel.account.observe(viewLifecycleOwner) {
            it?.let { binding.fragmentFilterAccName.text = it.name }
        }
        viewModel.category.observe(viewLifecycleOwner) {
            it?.let {
                binding.fragmentFilterCategoryIcon.text = it.symbol
                binding.fragmentFilterCategoryName.text = it.name
            }
        }
        viewModel.sumReset.observe(viewLifecycleOwner) {
            if (it) {
                binding.fragmentFilterSumName.setText("")
                viewModel.sumDeleted()
            }
        }
        viewModel.descriptionReset.observe(viewLifecycleOwner) {
            if (it) {
                binding.fragmentFilterDescName.setText("")
                viewModel.descriptionDeleted()
            }
        }
        viewModel.dateFrom.observe(viewLifecycleOwner) {
            binding.fragmentFilterDateFromName.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(it)
            dateFrom = it
        }
        viewModel.dateFromReset.observe(viewLifecycleOwner) {
            if (it) {
                binding.fragmentFilterDateFromName.text = ""
                viewModel.dateFromDeleted()
            }
        }
        viewModel.dateTo.observe(viewLifecycleOwner) {
            binding.fragmentFilterDateToName.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(it)
            dateTo = it
        }
        viewModel.dateToReset.observe(viewLifecycleOwner) {
            if (it) {
                binding.fragmentFilterDateToName.text = ""
                viewModel.dateToDeleted()
            }
        }
    }

    private fun initViews() {
        binding.fragmentFilterAccContainer.setOnClickListener { viewModel.showAccounts() }
        binding.fragmentFilterAccReset.setOnClickListener { viewModel.resetAccount() }

        binding.fragmentFilterCategoryContainer.setOnClickListener { viewModel.showCategories() }
        binding.fragmentFilterCategoryReset.setOnClickListener { viewModel.resetCategory() }

        binding.fragmentFilterSumReset.setOnClickListener { viewModel.resetSum() }
        binding.fragmentFilterDescReset.setOnClickListener { viewModel.resetDescription() }

        binding.fragmentFilterDateFromContainer.setOnClickListener { showDateDialog(viewModel::setDateFrom) }
        binding.fragmentFilterDateFromReset.setOnClickListener { viewModel.resetDateFrom() }

        binding.fragmentFilterDateToContainer.setOnClickListener { showDateDialog(viewModel::setDateTo) }
        binding.fragmentFilterDateToReset.setOnClickListener { viewModel.resetDateTo() }

        binding.fragmentFilterBtn.setOnClickListener { closeDialog(createFilter()) }
        binding.fragmentResetBtn.setOnClickListener {
            resetAllFields()
            closeDialog(Filter())
        }
    }

    private fun showDateDialog(save: (newDate: Date) -> Unit) {
        val calendar = GregorianCalendar()

        DatePickerDialog(
            requireContext(),
            { _, year, month, day -> save(GregorianCalendar(year, month, day).time) },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun createFilter(): Filter {
        val filter = Filter()
        binding.fragmentFilterDescName.text?.let {
            if (it.isNotBlank()) filter.comment = it.toString()
        }
        binding.fragmentFilterSumName.text?.let {
            if (it.isNotBlank()) filter.sumFrom = it.toString().toInt()
        }
        dateFrom?.let { filter.dateFrom = it }
        dateTo?.let { filter.dateTo = it }
        return filter
    }

    private fun resetAllFields() {
        binding.fragmentFilterSumName.setText("")
        binding.fragmentFilterDescName.setText("")
        binding.fragmentFilterDateFromName.text = ""
        binding.fragmentFilterDateToName.text = ""
    }

    private fun closeDialog(filter: Filter) {
        targetFragment?.onActivityResult(
            targetRequestCode,
            1,
            filter.createIntent()
        )
        dismiss()
    }
}
