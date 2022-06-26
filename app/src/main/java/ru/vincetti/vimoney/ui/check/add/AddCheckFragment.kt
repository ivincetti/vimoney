package ru.vincetti.vimoney.ui.check.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.core.models.Currency
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentAddCheckBinding
import ru.vincetti.vimoney.extensions.updateMargin

@AndroidEntryPoint
class AddCheckFragment : Fragment() {

    private val viewModel: AddCheckViewModel by viewModels()

    private var _binding: FragmentAddCheckBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCheckBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewInit()
        observersInit()
        insetsInit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showUnsavedDialog()
        }
    }

    private fun viewInit() {
        binding.addCheckNavigationDeleteBtn.setOnClickListener { showDeleteDialog() }
        binding.addCheckNavigationFromArchiveBtn.setOnClickListener { viewModel.restore() }
        binding.addCheckNavigationAddBtn.setOnClickListener { save() }
        binding.settingNavigationBackBtn.setOnClickListener { showUnsavedDialog() }

        binding.addCheckContent.addCheckSaveBtn.setOnClickListener { save() }
        binding.addCheckContent.addCheckColorView.setOnClickListener { pickColor() }
        binding.addCheckContent.addCheckAllBalanceSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNeedAllBalance(isChecked)
        }
        binding.addCheckContent.addCheckShowMainSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNeedOnMain(isChecked)
        }
    }

    private fun observersInit() {
        viewModel.isDefault.observe(viewLifecycleOwner) {
            if (!it) binding.addCheckContent.addCheckSaveBtn.text = getString(R.string.add_btn_update)
        }
        viewModel.needAllBalance.observe(viewLifecycleOwner) {
            binding.addCheckContent.addCheckAllBalanceSwitch.isChecked = it
        }
        viewModel.needOnMain.observe(viewLifecycleOwner) {
            binding.addCheckContent.addCheckShowMainSwitch.isChecked = it
        }
        viewModel.need2NavigateBack.observe(viewLifecycleOwner) {
            if (it) findNavController().navigateUp()
        }
        viewModel.need2AllData.observe(viewLifecycleOwner) {
            if (it) showNoDataDialog()
        }
        viewModel.color.observe(viewLifecycleOwner) {
            it?.let { binding.addCheckContent.addCheckColorView.setBackgroundColor(it) }
        }
        viewModel.check.observe(viewLifecycleOwner) {
            it.type?.let { type ->
                binding.addCheckContent.addCheckName.setText(it.name)
                typeLoad(type)

                if (it.isArchive) {
                    binding.addCheckNavigationFromArchiveBtn.visibility = View.VISIBLE
                    binding.addCheckNavigationDeleteBtn.visibility = View.GONE
                } else {
                    binding.addCheckNavigationFromArchiveBtn.visibility = View.GONE
                    binding.addCheckNavigationDeleteBtn.visibility = View.VISIBLE
                }
            }
        }
        viewModel.currency.observe(viewLifecycleOwner) {
            it?.let { binding.addCheckContent.addCheckCurrency.text = it.symbol }
        }
        viewModel.currencyList.observe(viewLifecycleOwner) {
            it?.let { list ->
                binding.addCheckContent.addCheckCurrencyContainer.setOnClickListener {
                    popUpCurrencyShow(list, binding.addCheckContent.addCheckCurrency)
                }
            }
        }
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
            .setNegativeButton(R.string.check_delete_alert_negative) { dialogInterface, _ ->
                dialogInterface?.dismiss()
            }
            .setPositiveButton(R.string.check_delete_alert_positive) { _, _ ->
                viewModel.delete()
            }
            .create()
            .show()
    }

    private fun showUnsavedDialog() {
        AlertDialog.Builder(requireContext(), R.style.AlertDialog)
            .setMessage(R.string.check_add_alert_question)
            .setNegativeButton(R.string.check_add_alert_negative) { _, _ ->
                viewModel.need2NavigateBack()
            }
            .setPositiveButton(R.string.check_add_alert_positive) { dialogInterface, _ ->
                dialogInterface?.dismiss()
            }
            .create()
            .show()
    }

    private fun showNoDataDialog() {
        AlertDialog.Builder(requireContext(), R.style.AlertDialog)
            .setMessage(R.string.check_add_alert_no_data)
            .setPositiveButton(R.string.check_add_alert_positive) { dialogInterface, _ ->
                viewModel.noDataDialogClosed()
                dialogInterface?.dismiss()
            }
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
            view.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
    }
}
