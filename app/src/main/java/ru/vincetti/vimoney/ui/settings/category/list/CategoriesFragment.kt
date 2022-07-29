package ru.vincetti.vimoney.ui.settings.category.list

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import ru.vincetti.modules.core.ui.viewBinding
import ru.vincetti.modules.database.repository.CategoryRepo
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentCategoriesListBinding
import ru.vincetti.vimoney.extensions.bottom
import ru.vincetti.vimoney.extensions.top
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.settings.category.AllCategoriesAdapter
import ru.vincetti.vimoney.ui.settings.category.add.AddCategoryViewModel.Companion.EXTRA_CATEGORY_ID
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesFragment : Fragment(R.layout.fragment_categories_list) {

    @Inject
    lateinit var categoryRepo: CategoryRepo

    val viewModel: CategoriesViewModel by viewModels()

    private lateinit var recyclerAdapter: AllCategoriesAdapter

    private val binding: FragmentCategoriesListBinding by viewBinding(FragmentCategoriesListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewsInit()
        observersInit()
        recyclerInit()
        insetsInit()
    }

    private fun viewsInit() {
        binding.categoriesNavigationBackBtn.setOnClickListener { viewModel.backButtonClicked() }
        binding.categoriesListFab.setOnClickListener { viewModel.addCategoryClicked() }
    }

    private fun observersInit() {
        viewModel.needNavigate2Home.observe(viewLifecycleOwner) {
            if (it) findNavController().navigateUp()
        }
        viewModel.needNavigate2AddCategory.observe(viewLifecycleOwner) {
            if (it) findNavController().navigate(R.id.action_categoriesFragment_to_addCategoryFragment)
        }
        viewModel.categories.observe(viewLifecycleOwner) {
            recyclerAdapter.setList(it)
        }
        viewModel.needNavigate2Check.observe(viewLifecycleOwner) {
            go2Category(it)
        }
    }

    private fun recyclerInit() {
        recyclerAdapter = AllCategoriesAdapter(
            object : CategoryViewHolder.Actions {
                override fun onCategoryClicked(id: Int) {
                    viewModel.clickOnElement(id)
                }
            }
        )
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
        binding.categoriesListRecycleView.apply {
            addItemDecoration(lineDivider)
            adapter = recyclerAdapter
        }
    }

    private fun insetsInit() {
        val fabMargin = binding.categoriesListFab.marginBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.categoriesListFab) { view, insets ->
            view.updateMargin(bottom = (insets.bottom() + fabMargin))
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.categoriesToolbar) { view, insets ->
            view.updateMargin(top = insets.top())
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.categoriesListRecycleView) { view, insets ->
            view.updatePadding(bottom = insets.bottom())
            insets
        }
    }

    private fun go2Category(id: Int) {
        val bundle = bundleOf(EXTRA_CATEGORY_ID to id)
        findNavController().navigate(R.id.action_categoriesFragment_to_addCategoryFragment, bundle)
    }
}
