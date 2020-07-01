package ru.vincetti.vimoney.ui.check.add

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_add_check.*
import kotlinx.android.synthetic.main.fragment_add_check.setting_navigation_back_btn
import kotlinx.android.synthetic.main.fragment_add_check_content.*
import kotlinx.android.synthetic.main.fragment_add_check_content.view.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.AccountModel
import ru.vincetti.vimoney.data.models.CurrencyModel
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.check.EXTRA_CHECK_ID

class AddCheckFragment : Fragment(R.layout.fragment_add_check) {

    private val viewModel: AddCheckViewModel by viewModels { viewModelFactory }

    private lateinit var viewModelFactory: AddCheckModelFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(activity).application
        val db = AppDatabase.getInstance(application)
        viewModelFactory = AddCheckModelFactory(db.accountDao(), db.currentDao(), application)

        arguments?.let { bundle ->
            val extraCheck = bundle.getInt(EXTRA_CHECK_ID)
            if (extraCheck > 0) viewModel.loadAccount(extraCheck)
        }

        viewInit()
        insetsInit()
    }

    private fun viewInit() {
        add_check_navigation_delete_btn.setOnClickListener {
            showDeleteDialog()
        }
        add_check_navigation_from_archive_btn.setOnClickListener {
            viewModel.restore()
        }
        add_check_navigation_add_btn.setOnClickListener {
            save()
        }
        add_check_save_btn.setOnClickListener {
            save()
        }
        add_check_content.add_check_color_view.setOnClickListener {
            pickColor()
        }
        add_check_all_balance_switch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNeed2AllData(isChecked)
        }
        setting_navigation_back_btn.setOnClickListener {
            showUnsavedDialog()
        }
        viewModel.isDefault.observe(viewLifecycleOwner, Observer {
            if (!it) add_check_save_btn.text = getString(R.string.add_btn_update)
        })
        viewModel.needAllBalance.observe(viewLifecycleOwner, Observer {
            add_check_all_balance_switch.isChecked = it
        })
        viewModel.need2Navigate.observe(viewLifecycleOwner, Observer {
            if (it) goBack()
        })
        viewModel.need2AllData.observe(viewLifecycleOwner, Observer {
            if (it) showNoDataDialog()
        })
        viewModel.color.observe(viewLifecycleOwner, Observer {
            it?.let {
                add_check_content.add_check_color_view.setBackgroundColor(it)
            }
        })
        viewModel.check.observe(viewLifecycleOwner, Observer {
            it.type?.let { type ->
                add_check_content.add_check_name.setText(it.name)
                typeLoad(type)

                if (it.isArchive) {
                    add_check_navigation_from_archive_btn.visibility = View.VISIBLE
                    add_check_navigation_delete_btn.visibility = View.GONE
                } else {
                    add_check_navigation_from_archive_btn.visibility = View.GONE
                    add_check_navigation_delete_btn.visibility = View.VISIBLE
                }
            }
        })
        viewModel.currency.observe(viewLifecycleOwner, Observer {
            it?.let {
                add_check_currency.text = it.symbol
            }
        })
        viewModel.currencyList.observe(viewLifecycleOwner, Observer {
            it?.let {
                loadCurrency(it, add_check_currency)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showUnsavedDialog()
        }
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
                    viewModel.setBackgroundColor(selectedColor)
                }
                .setNegativeButton(R.string.check_add_alert_color_negative
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .build().show()
    }

    /** RadioButton clicked option selected. */
    private fun typeEntered(): String {
        return when (add_check_content.radioGroup.checkedRadioButtonId) {
            R.id.add_check_type_debit -> AccountModel.ACCOUNT_TYPE_DEBIT
            R.id.add_check_type_credit -> AccountModel.ACCOUNT_TYPE_CREDIT
            else -> AccountModel.ACCOUNT_TYPE_CASH
        }
    }

    /** RadioButton option load. */
    private fun typeLoad(type: String) {
        add_check_content.radioGroup.apply {
            when (type) {
                AccountModel.ACCOUNT_TYPE_DEBIT -> check(R.id.add_check_type_debit)
                AccountModel.ACCOUNT_TYPE_CREDIT -> check(R.id.add_check_type_credit)
                else -> check(R.id.add_check_type_cash)
            }
        }
    }

    private fun loadCurrency(list: List<CurrencyModel>, view: View) {
        view.setOnClickListener {
            popUpCurrencyShow(list, view)
        }
    }

    private fun popUpCurrencyShow(list: List<CurrencyModel>, view: View) {
        val popUp = PopupMenu(requireContext(), view)
        for (i in list.indices) {
            popUp.menu.add(Menu.NONE, list[i].code, i, list[i].symbol)
        }
        popUp.setOnMenuItemClickListener {
            viewModel.setCurrency(it.itemId)
            true
        }
        popUp.show()
    }

    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(requireContext())
                .setMessage(R.string.check_delete_alert_question)
                .setNegativeButton(R.string.check_delete_alert_negative) { dialogInterface, _ ->
                    dialogInterface?.dismiss()
                }
                .setPositiveButton(R.string.check_delete_alert_positive) { _, _ ->
                    viewModel.delete()
                }
        builder.create().show()
    }

    /** Not saved transaction cancel dialog. */
    private fun showUnsavedDialog() {
        val builder = AlertDialog.Builder(requireContext())
                .setMessage(R.string.check_add_alert_question)
                .setNegativeButton(R.string.check_add_alert_negative) { _, _ ->
                    goBack()
                }
                .setPositiveButton(R.string.check_add_alert_positive) { dialogInterface, _ ->
                    dialogInterface?.dismiss()
                }
        builder.create().show()
    }

    /** Not saved transaction cancel dialog. */
    private fun showNoDataDialog() {
        val builder = AlertDialog.Builder(requireContext())
                .setMessage(R.string.check_add_alert_no_data)
                .setPositiveButton(R.string.check_add_alert_positive) { dialogInterface, _ ->
                    // todo криво надо подумать
                    viewModel.need2AllData.value = false
                    dialogInterface?.dismiss()
                }
        builder.create().show()
    }

    private fun save() {
        viewModel.save(
                add_check_content.add_check_name.text.toString(),
                typeEntered()
        )
    }

    private fun goBack() {
        findNavController().navigateUp()
    }

    private fun insetsInit() {
        ViewCompat.setOnApplyWindowInsetsListener(check_add_toolbar) { _, insets ->
            check_add_toolbar.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
    }
}
