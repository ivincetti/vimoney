package ru.vincetti.vimoney.transaction.transfer

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_add_all.*
import kotlinx.android.synthetic.main.fragment_add_all.view.*
import kotlinx.android.synthetic.main.fragment_add_transfer.*
import ru.vincetti.vimoney.MainViewModel
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.transaction.TransactionConst
import ru.vincetti.vimoney.transaction.main.TransactionMainViewModel
import ru.vincetti.vimoney.transaction.main.TransactionMainViewModelFactory
import java.text.DateFormat
import java.util.*

class TransactionTransferFragment : Fragment() {

    private lateinit var viewModel: TransactionMainViewModel
    private lateinit var mainViewModel: MainViewModel
    private var date = Date()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_transfer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val application = requireNotNull(activity).application
        val mDb = AppDatabase.getInstance(application)
        val transactionMainViewModelFactory =
                TransactionMainViewModelFactory(mDb.transactionDao(), mDb.accountDao(), application)
        viewModel = ViewModelProvider(requireNotNull(parentFragment!!.viewModelStore),
                transactionMainViewModelFactory).get(TransactionMainViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.saveAction = TransactionModel.TRANSACTION_TYPE_TRANSFER
        initFragmentViews()

        arguments?.let {
            if (it.getInt(TransactionConst.EXTRA_TRANS_ID) > 0) {
                fragment_container.visibility = View.INVISIBLE
                fragment_progress_bar.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()

        add_sum.isFocusableInTouchMode = true
        add_sum.requestFocus()

        // Show Keyboard
        val imm: InputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun initFragmentViews() {
        viewModel.needSum.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) showNoSumToast()
        })
        viewModel.needAccount.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) showNoAccountToast()
        })
        viewModel.needToUpdate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) add_btn.text = getString(R.string.add_btn_update)
        })

        mainViewModel.accountNotArchiveNames.observe(this, androidx.lifecycle.Observer {
            spinnerInit(add_acc_list, it, viewModel::setAccount)
            spinnerInit(add_acc_list_to, it, viewModel::setAccountTo)
        })

        viewModel.transaction.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                add_sum.setText(it.sum.toString())
                add_acc_name.text = mainViewModel.loadFromAccountNotArchiveNames(it.accountId)
                add_acc_cur.text = mainViewModel.loadFromCurSymbols(it.accountId)
                add_date_txt.text = DateFormat
                        .getDateInstance(DateFormat.MEDIUM).format(it.date)
                fragment_add_content.add_desc.setText(it.description)
            }
        })

        viewModel.nestedTransaction.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                add_sum_to.setText(it.sum.toString())
                add_acc_name_to.text = mainViewModel.loadFromAccountNotArchiveNames(it.accountId)
                add_acc_cur_to.text = mainViewModel.loadFromCurSymbols(it.accountId)
            }
        })

        viewModel.date.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                date = it
            }
        })

        add_acc_name_to.setOnClickListener {
            add_acc_list_to.performClick()
        }

        add_btn.setOnClickListener {
            save()
        }
        add_acc_name.setOnClickListener {
            add_acc_list.performClick()
        }

        add_date_block.setOnClickListener {
            showDateDialog()
        }

        add_sum.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.changeSumAdd(s)
            }
        })
    }

    private fun spinnerInit(accSpinner: Spinner, notArchiveAccountNames: HashMap<Int, String>?, viewM: (Int) -> Unit) {
        val accountsArray = ArrayList<String>()
        accountsArray.add(getString(R.string.add_no_account_text))
        notArchiveAccountNames?.let {
            for (entry in it.entries) {
                accountsArray.add(entry.value)
            }
            val adapter =
                    ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, accountsArray)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            //Применяем адаптер к элементу spinner
            accSpinner.adapter = adapter
            accSpinner.isSelected = false
            accSpinner.setSelection(TransactionModel.DEFAULT_ID, false)
            accSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // do nothing
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position != TransactionModel.DEFAULT_ID) {
                        var mId: Int = TransactionModel.DEFAULT_ID
                        // TODO могут быть не только архивные
                        for (entry in it.entries) {
                            if (adapter.getItem(position).equals(entry.value)) {
                                mId = entry.key
                            }
                        }
                        if (mId != TransactionModel.DEFAULT_ID) {
                            viewM(mId)
                            accSpinner.tag = position
                        }
                    }
                }
            }
        }
    }

    private fun showNoSumToast() {
        Toast.makeText(activity,
                getString(R.string.add_check_no_sum_warning),
                Toast.LENGTH_SHORT)
                .show()
    }

    private fun showNoAccountToast() {
        Toast.makeText(activity,
                getString(R.string.add_check_no_account_warning),
                Toast.LENGTH_SHORT)
                .show()
    }

    private fun showDateDialog() {
        val calendar = GregorianCalendar()
        calendar.time = date

        val datePickerDialog = DatePickerDialog(activity!!,
                { _, year, month, day ->
                    viewModel.setDate(GregorianCalendar(year, month, day).time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    // save transaction logic
    private fun save() {
        viewModel.saveTransaction(
                fragment_add_content.add_desc.text.toString(),
                add_sum.text.toString(),
                add_sum_to.text.toString()
        )
        findNavController().navigateUp()
    }
}

