package ru.vincetti.vimoney.ui.settings.category.add

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_category.*
import kotlinx.android.synthetic.main.fragment_add_category_content.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.repository.CategoryRepo
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.settings.category.add.AddCategoryViewModel.Companion.EXTRA_CATEGORY_ID
import ru.vincetti.vimoney.ui.settings.category.symbol.CategorySymbolListDialog
import javax.inject.Inject

@AndroidEntryPoint
class AddCategoryFragment : Fragment(R.layout.fragment_add_category) {

    @Inject
    lateinit var categoryRepo: CategoryRepo

    private val viewModel: AddCategoryViewModel by viewModels { viewModelFactory }

    private lateinit var viewModelFactory: AddCategoryViewModelFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelFactory = AddCategoryViewModelFactory(categoryRepo)

        arguments?.let { bundle ->
            val extraCategory = bundle.getInt(EXTRA_CATEGORY_ID)
            if (extraCategory > 0) viewModel.loadCategory(extraCategory)
        }

        viewsInit()
        observersInit()
        insetsInit()
    }

    private fun viewsInit() {
        category_add_navigation_add_btn.setOnClickListener { save() }
        category_add_navigation_back_btn.setOnClickListener { showUnsavedDialog() }
        add_category_save_btn.setOnClickListener { save() }
        add_category_symbol.setOnClickListener {
            val dialogFrag = CategorySymbolListDialog()
            dialogFrag.setTargetFragment(this, 1)
            dialogFrag.show(parentFragmentManager, "Icons")
        }
    }

    private fun observersInit() {
        viewModel.isDefault.observe(viewLifecycleOwner) {
            if (!it) add_category_save_btn.text = getString(R.string.add_btn_update)
        }
        viewModel.need2NavigateBack.observe(viewLifecycleOwner) {
            if (it) findNavController().navigateUp()
        }
        viewModel.need2AllData.observe(viewLifecycleOwner) {
            if (it) showNoDataDialog()
        }
        viewModel.categoryName.observe(viewLifecycleOwner) {
            add_category_name.setText(it)
        }
        viewModel.categorySymbol.observe(viewLifecycleOwner) {
            add_category_symbol.text = it
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showUnsavedDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) setCategorySymbol(resultCode)
    }

    private fun showUnsavedDialog() {
        AlertDialog.Builder(requireContext(), R.style.AlertDialog)
            .setMessage(R.string.check_add_alert_question)
            .setNegativeButton(R.string.check_add_alert_negative) { _, _ ->
                viewModel.need2navigateBack()
            }
            .setPositiveButton(R.string.check_add_alert_positive) { dialogInterface, _ ->
                dialogInterface?.dismiss()
            }
            .create()
            .show()
    }

    private fun showNoDataDialog() {
        Toast.makeText(
            requireContext(),
            R.string.check_add_alert_no_data,
            Toast.LENGTH_SHORT
        ).show()
        viewModel.noDataDialogClosed()
    }

    private fun setCategorySymbol(position: Int) {
        viewModel.setCategorySymbol(position)
    }

    private fun save() {
        viewModel.save(
            add_category_name.text.toString(),
            add_category_symbol.text.toString()
        )
    }

    private fun insetsInit() {
        ViewCompat.setOnApplyWindowInsetsListener(add_category_toolbar) { view, insets ->
            view.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
    }
}
