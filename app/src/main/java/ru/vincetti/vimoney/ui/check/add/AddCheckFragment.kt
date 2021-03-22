package ru.vincetti.vimoney.ui.check.add

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.core.models.Currency
import ru.vincetti.modules.core.ui.viewBinding
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentAddCheckBinding
import ru.vincetti.vimoney.extensions.top
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.transaction.main.TransactionFragmentUtils

@AndroidEntryPoint
class AddCheckFragment : Fragment(R.layout.fragment_add_check) {

    private val viewModel: AddCheckViewModel by viewModels()

    private val binding: FragmentAddCheckBinding by viewBinding(FragmentAddCheckBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { bundle ->
            viewModel.loadAccount(bundle.getInt(EXTRA_CHECK_ID))
        }

        viewInit()
        observersInit()
        insetsInit()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showUnsavedDialog()
        }
    }

    private fun viewInit() {
        binding.addCheckNavigationDeleteBtn.setOnClickListener { showDeleteDialog() }
        binding.settingNavigationBackBtn.setOnClickListener { showUnsavedDialog() }

        binding.addCheckNavigationAddBtn.setOnClickListener { save() }
        binding.addCheckContent.addCheckSaveBtn.setOnClickListener { save() }
        binding.addCheckContent.addCheckColorView.setOnClickListener { pickColor() }

        binding.addCheckContent.addCheckAllBalanceSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNeedAllBalance(isChecked)
        }
        binding.addCheckContent.addCheckShowMainSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNeedOnMain(isChecked)
        }
        binding.addCheckNavigationFromArchiveBtn.setOnClickListener { viewModel.restore() }
    }

    private fun observersInit() {
        viewModel.name.observe(viewLifecycleOwner) {
            binding.addCheckContent.addCheckName.setText(it)
        }
        viewModel.type.observe(viewLifecycleOwner) {
            typeLoad(it)
        }
        viewModel.isArchive.observe(viewLifecycleOwner) {
            if (it) {
                binding.addCheckNavigationFromArchiveBtn.isVisible = true
                binding.addCheckNavigationDeleteBtn.isGone = true
            } else {
                binding.addCheckNavigationFromArchiveBtn.isGone = true
                binding.addCheckNavigationDeleteBtn.isVisible = true
            }
        }
        viewModel.currency.observe(viewLifecycleOwner) {
            binding.addCheckContent.addCheckCurrency.text = it
        }
        viewModel.isDefault.observe(viewLifecycleOwner) {
            if (it) {
                binding.addCheckNavigationArchiveContainer.isGone = true
            } else {
                binding.addCheckNavigationArchiveContainer.isVisible = true
                binding.addCheckContent.addCheckSaveBtn.text = getString(R.string.add_btn_update)
            }
        }
        viewModel.needAllBalance.observe(viewLifecycleOwner) {
            binding.addCheckContent.addCheckAllBalanceSwitch.isChecked = it
        }
        viewModel.needOnMain.observe(viewLifecycleOwner) {
            binding.addCheckContent.addCheckShowMainSwitch.isChecked = it
        }
        viewModel.color.observe(viewLifecycleOwner) {
            binding.addCheckContent.addCheckColorView.setBackgroundColor(it)
        }

        viewModel.currencyList.observe(viewLifecycleOwner) { list ->
            binding.addCheckContent.addCheckCurrencyContainer.setOnClickListener {
                popUpCurrencyShow(list, binding.addCheckContent.addCheckCurrency)
            }
        }
        viewModel.need2NavigateBack.observe(viewLifecycleOwner) { findNavController().navigateUp() }
        viewModel.need2AllData.observe(viewLifecycleOwner) { showNoDataDialog() }
    }

    private fun pickColor() {
        ColorPickerDialogBuilder
            .with(requireContext())
            .setTitle(getString(R.string.check_add_alert_color_header))
            .initialColor(getColor(requireContext(), R.color.colorPrimary))
            .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
            .density(12)
            .setPositiveButton(R.string.check_add_alert_color_positive) { _, selectedColor, _ ->
                viewModel.setBackgroundColor(selectedColor)
            }
            .setNegativeButton(R.string.check_add_alert_color_negative) { dialog, _ ->
                dialog.dismiss()
            }
            .build()
            .show()
    }

    private fun typeEntered(): String {
        return when (binding.addCheckContent.radioGroup.checkedRadioButtonId) {
            R.id.add_check_type_debit -> Account.ACCOUNT_TYPE_DEBIT
            R.id.add_check_type_credit -> Account.ACCOUNT_TYPE_CREDIT
            else -> Account.ACCOUNT_TYPE_CASH
        }
    }

    private fun typeLoad(type: String) {
        binding.addCheckContent.radioGroup.check(
            when (type) {
                Account.ACCOUNT_TYPE_DEBIT -> R.id.add_check_type_debit
                Account.ACCOUNT_TYPE_CREDIT -> R.id.add_check_type_credit
                else -> R.id.add_check_type_cash
            }
        )
    }

    private fun popUpCurrencyShow(list: List<Currency>, view: View) {
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
        AlertDialog.Builder(requireContext(), R.style.AlertDialog)
            .setMessage(R.string.check_delete_alert_question)
            .setNegativeButton(R.string.check_delete_alert_negative) { dialogInterface, _ -> dialogInterface?.dismiss() }
            .setPositiveButton(R.string.check_delete_alert_positive) { _, _ -> viewModel.delete() }
            .create()
            .show()
    }

    private fun showUnsavedDialog() {
        TransactionFragmentUtils.hideKeyboard(requireActivity())
        AlertDialog.Builder(requireContext(), R.style.AlertDialog)
            .setMessage(R.string.check_add_alert_question)
            .setNegativeButton(R.string.check_add_alert_negative) { _, _ -> viewModel.need2NavigateBack() }
            .setPositiveButton(R.string.check_add_alert_positive) { dialogInterface, _ -> dialogInterface?.dismiss() }
            .create()
            .show()
    }

    private fun showNoDataDialog() {
        AlertDialog.Builder(requireContext(), R.style.AlertDialog)
            .setMessage(R.string.check_add_alert_no_data)
            .setPositiveButton(R.string.check_add_alert_positive) { dialogInterface, _ -> dialogInterface?.dismiss() }
            .create()
            .show()
    }

    private fun save() {
        viewModel.save(
            binding.addCheckContent.addCheckName.text.toString(),
            typeEntered()
        )
    }

    private fun insetsInit() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.checkAddToolbar) { view, insets ->
            view.updateMargin(top = insets.top())
            insets
        }
    }

    companion object {

        private const val EXTRA_CHECK_ID = "Extra_check_id"

        fun createArgs(checkId: Int): Bundle = Bundle().apply {
            putInt(EXTRA_CHECK_ID, checkId)
        }
    }
}
