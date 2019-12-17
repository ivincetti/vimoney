package ru.vincetti.vimoney.ui.check.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_add_check_content.view.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.AccountModel
import ru.vincetti.vimoney.data.models.CurrencyModel
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.databinding.FragmentAddCheckBinding
import ru.vincetti.vimoney.ui.check.AccountConst
import ru.vincetti.vimoney.utils.TransactionViewModelUtils
import ru.vincetti.vimoney.utils.UpdateAndroidViewModel

class AddCheckFragment : Fragment() {

    private lateinit var binding: FragmentAddCheckBinding
    private lateinit var viewmodel: AddCheckViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddCheckBinding.inflate(inflater)

        val application = requireNotNull(activity).application
        val db = AppDatabase.getInstance(application)
        val viewModelFactory = AddCheckModelFactory(db.accountDao(), db.currentDao(), application)

        viewmodel = ViewModelProviders.of(this, viewModelFactory).get(AddCheckViewModel::class.java)
        arguments?.let { bundle ->
            bundle.getInt(AccountConst.EXTRA_CHECK_ID)?.let {
                viewmodel.loadAccount(it)
            }
        }

        viewInit()
        return binding.root
    }

    private fun viewInit() {
        binding.settingNavigationBackBtn.setOnClickListener {
            goBack()
        }
        binding.addCheckNavigationDeleteBtn.setOnClickListener {
            showDeleteDialog()
        }
        binding.addCheckNavigationFromArchiveBtn.setOnClickListener {
            viewmodel.restore()
        }
        binding.addCheckContent.addCheckSaveBtn.setOnClickListener {
            save()
        }
        binding.addCheckNavigationAddBtn.setOnClickListener {
            save()
        }
        binding.addCheckContent.addCheckColorView.setOnClickListener {
            pickColor()
        }
        binding.settingNavigationBackBtn.setOnClickListener {
            showUnsavedDialog()
        }
        viewmodel.isDefault.observe(this, Observer {
            if (!it) {
                binding.addCheckContent.addCheckSaveBtn.text = getString(R.string.add_btn_update)
            }
        })
        viewmodel.need2Navigate.observe(this, Observer {
            if (it) goBack()
        })
        viewmodel.need2AllData.observe(this, Observer {
            if (it) showNoDataDialog()
        })
        viewmodel.need2UpdateViewModel.observe(this, Observer {
            if(it) updateTransactionsViewModel(viewmodel)
        })
        viewmodel.color.observe(this, Observer {
            it?.let {
                binding.addCheckContent.addCheckColorView.setBackgroundColor(it)
            }
        })
        viewmodel.check.observe(this, Observer {
            binding.addCheckContent.addCheckName.setText(it.name)
            typeLoad(it.type)

            if (it.isArhive) {
                binding.addCheckNavigationFromArchiveBtn.visibility = View.VISIBLE
            } else {
                binding.addCheckNavigationDeleteBtn.visibility = View.VISIBLE
            }
        })
        viewmodel.currency.observe(this, Observer {
            it?.let {
                currencyEntered(it)
            }
        })
        spinnerInit()
    }

    private fun pickColor() {
        ColorPickerDialogBuilder
                .with(requireContext())
                .setTitle(getString(R.string.check_add_alert_color_header))
                .initialColor(getColor(requireContext(), R.color.colorPrimary))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setPositiveButton(R.string.check_add_alert_color_positive
                ) { _, selectedColor, _ ->
                    viewmodel.setBackgroundColor(selectedColor)
                }
                .setNegativeButton(R.string.check_add_alert_color_negative
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .build().show()
    }

    // radioButton clicked option selected
    private fun typeEntered(): String {
        return when (binding.container.radioGroup.checkedRadioButtonId) {
            R.id.add_check_type_debit -> AccountModel.ACCOUNT_TYPE_DEBIT
            R.id.add_check_type_credit -> AccountModel.ACCOUNT_TYPE_CREDIT
            else -> AccountModel.ACCOUNT_TYPE_CASH
        }
    }

    // radioButton option load
    private fun typeLoad(type: String) {
        val typeRadioGroup = binding.container.radioGroup
        when (type) {
            AccountModel.ACCOUNT_TYPE_CASH -> typeRadioGroup.check(R.id.add_check_type_cash)
            AccountModel.ACCOUNT_TYPE_DEBIT -> typeRadioGroup.check(R.id.add_check_type_debit)
            AccountModel.ACCOUNT_TYPE_CREDIT -> typeRadioGroup.check(R.id.add_check_type_credit)
            else -> typeRadioGroup.check(R.id.add_check_type_cash)
        }
    }

    private fun spinnerInit() {
        val curSpinner = binding.addCheckContent.addCheckCurrencySpinner
        viewmodel.currencyList.observe(this, Observer {
            it?.let {
                // adapter init
                val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, it)
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                curSpinner.adapter = adapter
            }
        })

        curSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //do nothing
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewmodel.setCurrency((parent?.selectedItem as CurrencyModel).code)
            }
        }

    }

    // spinner load option selected
    private fun currencyEntered(currency: CurrencyModel) {
        val pos = getIndex(currency)
        binding.addCheckContent.addCheckCurrencySpinner.setSelection(pos)

    }

    // get index in spinner
    private fun getIndex(tmpAcc: CurrencyModel): Int {
        val spinner = binding.addCheckContent.addCheckCurrencySpinner
        for (i in 0..spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == tmpAcc.symbol) {
                return i
            }
        }
        return 0
    }

    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(requireContext())
                .setMessage(R.string.check_delete_alert_question)
                .setNegativeButton(R.string.check_delete_alert_negative)
                { dialogInterface, _ ->
                    dialogInterface?.dismiss()
                }
                .setPositiveButton(R.string.check_delete_alert_positive) { _, _ ->
                    viewmodel.delete()
                }
        builder.create().show()
    }

    private fun updateTransactionsViewModel(viewModel: UpdateAndroidViewModel){
        TransactionViewModelUtils.updateTransactionsViewModel(activity!!, viewModel)
    }

    // not saved transaction cancel dialog
    private fun showUnsavedDialog() {
        val builder = AlertDialog.Builder(requireContext())
                .setMessage(R.string.check_add_alert_question)
                .setNegativeButton(R.string.check_add_alert_negative)
                { _, _ ->
                    goBack()
                }
                .setPositiveButton(R.string.check_add_alert_positive)
                { dialogInterface, _ ->
                    dialogInterface?.dismiss()
                }
        builder.create().show()
    }

    // not saved transaction cancel dialog
    private fun showNoDataDialog() {
        val builder = AlertDialog.Builder(requireContext())
                .setMessage(R.string.check_add_alert_no_data)
                .setPositiveButton(R.string.check_add_alert_positive)
                { dialogInterface, _ ->
                    // todo криво надо подумать
                    viewmodel.need2AllData.value = false
                    dialogInterface?.dismiss()
                }
        builder.create().show()
    }

    private fun goBack() {
        findNavController().navigateUp()
    }

    private fun save() {
        viewmodel.save(
                binding.addCheckContent.addCheckName.text.toString(),
                typeEntered()
        )
    }

//TODO вернуть к работе
//
//    fun onBackPressed()
//    {
//        showUnsavedDialog();
//    }
}
