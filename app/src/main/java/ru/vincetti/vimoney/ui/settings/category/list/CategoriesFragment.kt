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
import dagger.hilt.android.AndroidEntryPoint
import ru.vincetti.modules.core.ui.viewBinding
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentCategoriesListBinding
import ru.vincetti.vimoney.extensions.bottom
import ru.vincetti.vimoney.extensions.top
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.settings.category.AllCategoriesAdapter
import ru.vincetti.vimoney.ui.settings.category.add.AddCategoryFragment

@AndroidEntryPoint
class CategoriesFragment : Fragment(R.layout.fragment_categories_list) {

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
        viewModel.needNavigate2Check.observe(viewLifecycleOwner) { id -> go2Category(id) }
        viewModel.needNavigate2Home.observe(viewLifecycleOwner) { findNavController().navigateUp() }
        viewModel.needNavigate2AddCategory.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_categoriesFragment_to_addCategoryFragment)
        }
    }

    private fun recyclerInit() {
        recyclerAdapter = AllCategoriesAdapter { id -> viewModel.clickOnElement(id) }
        val lineDivider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        lineDivider.setDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.light_divider)!!
        )
        binding.categoriesListRecycleView.apply {
            addItemDecoration(lineDivider)
            adapter = recyclerAdapter
        }

        viewModel.categories.observe(viewLifecycleOwner) { recyclerAdapter.setList(it) }
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
        findNavController().navigate(
            R.id.action_categoriesFragment_to_addCategoryFragment,
            AddCategoryFragment.createArgs(id)
        )
    }
}
