package ru.vincetti.vimoney.ui.check.list

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
import kotlinx.android.synthetic.main.fragment_checks_list.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.adapters.AllCardsListRVAdapter
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.check.EXTRA_CHECK_ID

@AndroidEntryPoint
class ChecksListFragment : Fragment(R.layout.fragment_checks_list) {

    private val viewModel: CheckListViewModel by viewModels()

    private lateinit var recyclerAdapter: AllCardsListRVAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewsInit()
        insetsInit()

        viewModel.accList.observe(viewLifecycleOwner) {
            it?.let { recyclerAdapter.setList(it) }
        }
    }

    private fun viewsInit() {
        setting_navigation_back_btn.setOnClickListener {
            findNavController().navigateUp()
        }
        check_list_fab.setOnClickListener {
            findNavController().navigate(R.id.action_checksListFragment_to_addCheckFragment)
        }
        recyclerInit()
    }

    private fun recyclerInit() {
        recyclerAdapter = AllCardsListRVAdapter {
            val bundle = Bundle()
            bundle.putInt(EXTRA_CHECK_ID, it)
            findNavController().navigate(R.id.action_checksListFragment_to_checkFragment, bundle)
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
        check_list_recycle_view.apply {
            addItemDecoration(lineDivider)
            adapter = recyclerAdapter
        }
    }

    private fun insetsInit() {
        val fabMargin = check_list_fab.marginBottom
        ViewCompat.setOnApplyWindowInsetsListener(check_list_fab) { view, insets ->
            view.updateMargin(bottom = (insets.systemWindowInsetBottom + fabMargin))
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(check_list_toolbar) { view, insets ->
            view.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(check_list_recycle_view) { view, insets ->
            view.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
    }
}
