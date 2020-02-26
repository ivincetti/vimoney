package ru.vincetti.vimoney.ui.transaction.spent

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_add_all.*
import kotlinx.android.synthetic.main.fragment_add_all.view.*
import kotlinx.android.synthetic.main.fragment_add_spent.*
import ru.vincetti.vimoney.MainViewModel
import ru.vincetti.vimoney.MainViewModelFactory
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.ui.transaction.main.TransactionMainViewModel
import ru.vincetti.vimoney.ui.transaction.main.TransactionMainViewModelFactory
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class TransactionSpentFragment : Fragment() {

    private lateinit var viewModel: TransactionMainViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var date: Date

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_spent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val application = requireNotNull(activity).application
        val mDb = AppDatabase.getInstance(application)
        val transactionMainViewModelFactory =
                TransactionMainViewModelFactory(mDb.transactionDao(), mDb.accountDao())
        viewModel = ViewModelProvider(requireNotNull(parentFragment!!.viewModelStore),
                transactionMainViewModelFactory).get(TransactionMainViewModel::class.java)
        val mainViewModelFactory = MainViewModelFactory(mDb.accountDao())
        mainViewModel = ViewModelProvider(requireActivity(), mainViewModelFactory)
                .get(MainViewModel::class.java)
        viewModel.saveAction = TransactionModel.TRANSACTION_TYPE_SPENT
        initFragmentViews()
    }

    override fun onResume() {
        super.onResume()

        add_sum.isFocusableInTouchMode = true
        add_sum.requestFocus()

        // Show Keyboard
        val imm: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun initFragmentViews() {
        viewModel.needSum.observe(viewLifecycleOwner, Observer {
            if (it) showNoSumToast()
        })
        viewModel.needAccount.observe(viewLifecycleOwner, Observer {
            if (it) showNoAccountToast()
        })
        viewModel.needToUpdate.observe(viewLifecycleOwner, Observer {
            if (it) add_btn.text = getString(R.string.add_btn_update)
        })
        viewModel.transaction.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.sum > 0) add_sum.setText(it.sum.toString())
                add_acc_name.setText(
                        mainViewModel.loadFromAccountNotArchiveNames(it.accountId),
                        false
                )
                add_acc_cur.text = mainViewModel.loadFromCurSymbols(it.accountId)
                add_date_txt.text = DateFormat
                        .getDateInstance(DateFormat.MEDIUM).format(it.date)
                fragment_add_content.add_desc.setText(it.description)
            }
        })
        mainViewModel.accountNotArchiveNames.observe(viewLifecycleOwner, Observer {
            it?.let {
                val accountsArray = ArrayList<String>()
                for (entry in it.entries) {
                    accountsArray.add(entry.value)
                }
                val adapter = ArrayAdapter<String>(
                        requireContext(),
                        R.layout.dropdown_menu_popup_item,
                        accountsArray
                )
                //Применяем адаптер к элементу spinner
                add_acc_name.setAdapter(adapter)
                add_acc_name.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, st: Int, c: Int, a: Int) {
                    }
                    override fun onTextChanged(s: CharSequence, st: Int, b: Int, c: Int) {
                    }
                    override fun afterTextChanged(s: Editable) {
                        for (entry in it.entries) {
                            if (s.toString() == entry.value) {
                                viewModel.setAccount(entry.key)
                            }
                        }
                    }
                })
            }
        })

        viewModel.date.observe(viewLifecycleOwner, Observer {
            it?.let {
                date = it
            }
        })

        viewModel.needToNavigate.observe(viewLifecycleOwner, Observer {
            if (it) navigateUp()
        })

        add_btn.setOnClickListener {
            save()
        }

        add_date_block.setOnClickListener {
            showDateDialog()
        }

        add_sum.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { //do nothing
            }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {
                //do nothing
            }
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {
                s?.let {
                    if (s.isNotEmpty()) viewModel.changeSumAdd(s)
                }
            }
        })
    }

    private fun showNoSumToast() {
        Toast.makeText(requireActivity(),
                getString(R.string.add_check_no_sum_warning),
                Toast.LENGTH_SHORT)
                .show()
    }

    private fun showNoAccountToast() {
        Toast.makeText(requireActivity(),
                getString(R.string.add_check_no_account_warning),
                Toast.LENGTH_SHORT)
                .show()
    }

    private fun showDateDialog() {
        val calendar = GregorianCalendar()
        calendar.time = date

        DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    viewModel.setDate(GregorianCalendar(year, month, day).time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // save transaction logic
    private fun save() {
        viewModel.saveTransaction(
                fragment_add_content.add_desc.text.toString(),
                add_sum.text.toString()
        )
    }

    private fun navigateUp() {
        findNavController().navigateUp()
    }
}