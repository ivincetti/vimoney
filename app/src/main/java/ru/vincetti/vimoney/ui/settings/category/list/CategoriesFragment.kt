package ru.vincetti.vimoney.ui.settings.category.list

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_categories_list.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.adapters.AllCategoriesListRVAdapter
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.settings.category.add.AddCategoryViewModel.Companion.EXTRA_CATEGORY_ID

class CategoriesFragment : Fragment(R.layout.fragment_categories_list) {

    val viewModel: CategoriesViewModel by viewModels { viewModelFactory }

    private lateinit var viewModelFactory: CategoriesModelFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(activity).application
        val db = AppDatabase.getInstance(application)
        viewModelFactory = CategoriesModelFactory(db.categoryDao())

        insetsInit()

        categories_navigation_back_btn.setOnClickListener { viewModel.backButtonClicked() }
        categories_list_fab.setOnClickListener {
            findNavController().navigate(R.id.action_categoriesFragment_to_addCategoryFragment)
        }

        val adapter = AllCategoriesListRVAdapter {
            val bundle = Bundle()
            bundle.putInt(EXTRA_CATEGORY_ID, it)
            findNavController().navigate(R.id.action_categoriesFragment_to_addCategoryFragment, bundle)
        }
        val lineDivider = DividerItemDecoration(
            requireContext(),
            DividerItemDecoration.VERTICAL
        )
        lineDivider.setDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.light_divider
            )!!
        )
        categories_list_recycle_view.apply {
            addItemDecoration(lineDivider)
            setAdapter(adapter)
        }

        viewModel.need2Navigate2Home.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_categoriesFragment_to_settingsFragment)
            }
        }
        viewModel.categories.observe(viewLifecycleOwner) {
            it?.let { adapter.setList(it) }
        }
    }

    private fun insetsInit() {
        val fabMargin = categories_list_fab.marginBottom
        ViewCompat.setOnApplyWindowInsetsListener(categories_list_fab) { view, insets ->
            view.updateMargin(bottom = (insets.systemWindowInsetBottom + fabMargin))
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(categories_toolbar) { view, insets ->
            view.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(categories_list_recycle_view) { view, insets ->
            view.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
    }
}
