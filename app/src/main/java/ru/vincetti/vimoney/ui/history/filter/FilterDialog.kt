package ru.vincetti.vimoney.ui.history.filter

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_transactions_filter.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.repository.AccountRepo
import ru.vincetti.vimoney.data.repository.CategoryRepo
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class FilterDialog : BottomSheetDialogFragment() {

    @Inject
    lateinit var accountRepo: AccountRepo

    @Inject
    lateinit var categoryRepo: CategoryRepo

    private val viewModel: FilterViewModel by viewModels { viewModelFactory }
    private lateinit var viewModelFactory: FilterViewModelFactory

    private var dateFrom: Date? = null
    private var dateTo: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_transactions_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initViews()
    }

    private fun initViewModel() {
        viewModelFactory = FilterViewModelFactory(accountRepo, categoryRepo)

        viewModel.account.observe(viewLifecycleOwner) {
            it?.let { fragment_filter_acc_name.text = it.name }
        }
        viewModel.category.observe(viewLifecycleOwner) {
            it?.let {
                fragment_filter_category_icon.text = it.symbol
                fragment_filter_category_name.text = it.name
            }
        }
        viewModel.sumReset.observe(viewLifecycleOwner) {
            if (it) {
                fragment_filter_sum_name.setText("")
                viewModel.sumDeleted()
            }
        }
        viewModel.descriptionReset.observe(viewLifecycleOwner) {
            if (it) {
                fragment_filter_desc_name.setText("")
                viewModel.descriptionDeleted()
            }
        }
        viewModel.dateFrom.observe(viewLifecycleOwner) {
            fragment_filter_date_from_name.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(it)
            dateFrom = it
        }
        viewModel.dateFromReset.observe(viewLifecycleOwner) {
            if (it) {
                fragment_filter_date_from_name.text = ""
                viewModel.dateFromDeleted()
            }
        }
        viewModel.dateTo.observe(viewLifecycleOwner) {
            fragment_filter_date_to_name.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(it)
            dateTo = it
        }
        viewModel.dateToReset.observe(viewLifecycleOwner) {
            if (it) {
                fragment_filter_date_to_name.text = ""
                viewModel.dateToDeleted()
            }
        }
    }

    private fun initViews() {
        fragment_filter_acc_container.setOnClickListener { viewModel.showAccounts() }
        fragment_filter_acc_reset.setOnClickListener { viewModel.resetAccount() }

        fragment_filter_category_container.setOnClickListener { viewModel.showCategories() }
        fragment_filter_category_reset.setOnClickListener { viewModel.resetCategory() }

        fragment_filter_sum_reset.setOnClickListener { viewModel.resetSum() }
        fragment_filter_desc_reset.setOnClickListener { viewModel.resetDescription() }

        fragment_filter_date_from_container.setOnClickListener { showDateDialog(viewModel::setDateFrom) }
        fragment_filter_date_from_reset.setOnClickListener { viewModel.resetDateFrom() }

        fragment_filter_date_to_reset.setOnClickListener { viewModel.resetDateTo() }
        fragment_filter_date_to_container.setOnClickListener { showDateDialog(viewModel::setDateTo) }

        fragment_filter_btn.setOnClickListener { closeDialog(createFilter()) }
        fragment_reset_btn.setOnClickListener {
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
        fragment_filter_desc_name.text?.let {
            if (it.isNotBlank()) filter.comment = it.toString()
        }
        fragment_filter_sum_name.text?.let {
            if (it.isNotBlank()) filter.sumFrom = it.toString().toInt()
        }
        dateFrom?.let { filter.dateFrom = it }
        dateTo?.let { filter.dateTo = it }
        return filter
    }

    private fun resetAllFields() {
        fragment_filter_sum_name.setText("")
        fragment_filter_desc_name.setText("")
        fragment_filter_date_from_name.text = ""
        fragment_filter_date_to_name.text = ""
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
