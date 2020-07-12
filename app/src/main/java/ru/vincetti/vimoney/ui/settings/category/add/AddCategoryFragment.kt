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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_add_category.*
import kotlinx.android.synthetic.main.fragment_add_category_content.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.settings.category.add.AddCategoryViewModel.Companion.EXTRA_CATEGORY_ID
import ru.vincetti.vimoney.ui.settings.category.symbol.CategorySymbolListDialog

class AddCategoryFragment : Fragment(R.layout.fragment_add_category) {

    private val viewModel: AddCategoryViewModel by viewModels { viewModelFactory }

    private lateinit var viewModelFactory: AddCategoryViewModelFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(activity).application
        val db = AppDatabase.getInstance(application)
        viewModelFactory = AddCategoryViewModelFactory(db.categoryDao())

        arguments?.let { bundle ->
            val extraCategory = bundle.getInt(EXTRA_CATEGORY_ID)
            if (extraCategory > 0) viewModel.loadCategory(extraCategory)
        }

        insetsInit()
        viewInit()
    }

    private fun viewInit() {
        category_add_navigation_add_btn.setOnClickListener {
            save()
        }
        category_add_navigation_back_btn.setOnClickListener {
            showUnsavedDialog()
        }
        add_category_save_btn.setOnClickListener {
            save()
        }
        viewModel.isDefault.observe(viewLifecycleOwner, Observer {
            if (!it) add_category_save_btn.text = getString(R.string.add_btn_update)
        })
        viewModel.need2Navigate.observe(viewLifecycleOwner, Observer {
            if (it) goBack()
        })
        viewModel.need2AllData.observe(viewLifecycleOwner, Observer {
            if (it) showNoDataDialog()
        })
        viewModel.categoryName.observe(viewLifecycleOwner, Observer {
            add_category_name.setText(it)
        })
        viewModel.categorySymbol.observe(viewLifecycleOwner, Observer {
            add_category_symbol.text = it
        })

        add_category_symbol.setOnClickListener {
            val dialogFrag = CategorySymbolListDialog()
            dialogFrag.setTargetFragment(this, 1)
            dialogFrag.show(parentFragmentManager, "Icons")
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

    /** Not saved category cancel dialog. */
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
        Toast.makeText(
                requireContext(),
                R.string.check_add_alert_no_data,
                Toast.LENGTH_SHORT
        ).show()
        viewModel.need2AllData.value = false
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

    private fun goBack() {
        findNavController().navigateUp()
    }

    private fun insetsInit() {
        ViewCompat.setOnApplyWindowInsetsListener(add_category_toolbar) { view, insets ->
            view.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
    }
}
