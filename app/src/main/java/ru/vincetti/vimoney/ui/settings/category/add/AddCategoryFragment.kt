package ru.vincetti.vimoney.ui.settings.category.add

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.vincetti.vimoney.R
import ru.vincetti.modules.database.repository.CategoryRepo
import ru.vincetti.vimoney.databinding.FragmentAddCategoryBinding
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.settings.category.add.AddCategoryViewModel.Companion.EXTRA_CATEGORY_ID
import ru.vincetti.vimoney.ui.settings.category.symbol.CategorySymbolListDialog
import javax.inject.Inject

@AndroidEntryPoint
class AddCategoryFragment : Fragment() {

    @Inject
    lateinit var categoryRepo: CategoryRepo

    private val viewModel: AddCategoryViewModel by viewModels()

    private var _binding: FragmentAddCategoryBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAddCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { bundle ->
            val extraCategory = bundle.getInt(EXTRA_CATEGORY_ID)
            if (extraCategory > 0) viewModel.loadCategory(extraCategory)
        }

        viewsInit()
        observersInit()
        insetsInit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun viewsInit() {
        binding.categoryAddNavigationAddBtn.setOnClickListener { save() }
        binding.categoryAddNavigationBackBtn.setOnClickListener { showUnsavedDialog() }
        binding.categoryAddContent.addCategorySaveBtn.setOnClickListener { save() }
        binding.categoryAddContent.addCategorySymbol.setOnClickListener {
            val dialogFrag = CategorySymbolListDialog()
            dialogFrag.setTargetFragment(this, 1)
            dialogFrag.show(parentFragmentManager, "Icons")
        }
    }

    private fun observersInit() {
        viewModel.isDefault.observe(viewLifecycleOwner) {
            if (!it) binding.categoryAddContent.addCategorySaveBtn.text = getString(R.string.add_btn_update)
        }
        viewModel.need2NavigateBack.observe(viewLifecycleOwner) {
            if (it) findNavController().navigateUp()
        }
        viewModel.need2AllData.observe(viewLifecycleOwner) {
            if (it) showNoDataDialog()
        }
        viewModel.categoryName.observe(viewLifecycleOwner) {
            binding.categoryAddContent.addCategoryName.setText(it)
        }
        viewModel.categorySymbol.observe(viewLifecycleOwner) {
            binding.categoryAddContent.addCategorySymbol.text = it
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
            binding.categoryAddContent.addCategoryName.text.toString(),
            binding.categoryAddContent.addCategorySymbol.text.toString()
        )
    }

    private fun insetsInit() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.addCategoryToolbar) { view, insets ->
            view.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
    }
}
