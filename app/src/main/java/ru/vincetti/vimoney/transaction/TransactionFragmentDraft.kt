package ru.vincetti.vimoney.transaction

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

open class TransactionFragmentDraft : Fragment() {
    lateinit var mDb: AppDatabase
    lateinit var mTrans: TransactionModel
    lateinit var curSymbolsId: HashMap<Int, String>
    lateinit var accountNames: HashMap<Int, String>
    lateinit var notArchiveAccountNames: HashMap<Int, String>
    lateinit var viewModel: TransactionViewModel

    var typeAction = TransactionModel.DEFAULT_ID
    var accOld = TransactionModel.DEFAULT_ID
    var mDate = Date()

    lateinit var txtName: TextView
    lateinit var txtSum: TextView
    lateinit var txtDate: TextView
    lateinit var txtCurrency: TextView
    lateinit var txtAccount: TextView
    lateinit var accSpinner: Spinner
    lateinit var btnSave: Button
    lateinit var btnDate: Button
    lateinit var container: LinearLayout
    lateinit var progressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDb = AppDatabase.getInstance(requireContext())

        initViews(view)
        mTrans = TransactionModel()
        viewModel = ViewModelProviders.of(activity!!).get(TransactionViewModel::class.java)

        arguments?.let {
            if (it.getInt(TransactionConst.EXTRA_TRANS_ID) > 0) {
                container.visibility = View.INVISIBLE
                progressBar.visibility = View.VISIBLE
            }
        }

        initFragmentViews(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setTypeAction();
    }

    override fun onResume() {
        super.onResume()

        txtSum.isFocusableInTouchMode = true
        txtSum.requestFocus()

        // Show Keyboard
        val imm: InputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun initViews(view: View) {
        txtDate.text = DateFormat
                .getDateInstance(DateFormat.MEDIUM).format(mDate)
        btnDate.setOnClickListener {
            showDateDialog()
        }
        btnSave.setOnClickListener {
            save(typeAction)
        }
        txtAccount.setOnClickListener {
            accSpinner.performClick()
        }
    }

    open fun initFragmentViews(view: View) {}

    open fun initFragmentLogic() {}

    open fun setTypeAction() {}

    fun spinnerInit(view: View) {
        val accountsArray = ArrayList<String>()
        accountsArray.add(getString(R.string.add_no_account_text))
        for (entry in notArchiveAccountNames.entries) {
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
                    for (entry in accountNames.entries) {
                        if (adapter.getItem(position).equals(entry.value)) {
                            mId = entry.key
                        }
                    }
                    if (mId != TransactionModel.DEFAULT_ID) {
                        mTrans.accountId = mId
                        txtCurrency.text = curSymbolsId[mId]
                        txtAccount.text = accountNames[mId]
                        accSpinner.tag = position
                    }
                }
            }
        }
    }

    open fun spinner2Init(view: View) {}

    // save transaction logic
    open fun save(typeAction: Int) {
        if (mTrans.accountId != TransactionModel.DEFAULT_ID) {
            if (txtSum.text.toString() == "") {
                Toast.makeText(activity, resources
                        .getString(R.string.add_check_no_sum_warning),
                        Toast.LENGTH_SHORT)
                        .show()
            } else {
                mTrans.description = txtName.text.toString()
                mTrans.date = mDate
                mTrans.type = typeAction
                mTrans.sum = txtSum.text.toString().toFloat()

                if (mTrans.id != TransactionModel.DEFAULT_ID) {
                    // update logic
                    // TODO go to Kotlin
//                                mDb.transactionDao().updateTransaction(mTrans);
                    //LogicMath.accountBalanceUpdateById(mDb, accOld);
                } else {
                    // new transaction
                    // TODO go to Kotlin
                    //                                mDb.transactionDao().insertTransaction(mTrans);
                }
                // update balance for current (accId) account
                // TODO go to Kotlin
//                LogicMath.accountBalanceUpdateById(mDb, mTrans.getAccountId());
                //TODO go back
                //getActivity().finish();
            }
        } else {
            Toast.makeText(activity,
                    getString(R.string.add_check_no_account_warning),
                    Toast.LENGTH_SHORT)
                    .show()
        }
    }

    fun showDateDialog() {
        val calendar = GregorianCalendar()
        calendar.time = mDate

        val datePickerDialog = DatePickerDialog(activity!!,
                { _, year, month, day ->
                    val tmpCal = GregorianCalendar(year, month, day)
                    mDate = tmpCal.time
                    txtDate.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(mDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }
}
