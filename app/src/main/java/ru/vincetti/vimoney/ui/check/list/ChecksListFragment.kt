package ru.vincetti.vimoney.ui.check.list

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
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentChecksListBinding
import ru.vincetti.vimoney.extensions.bottom
import ru.vincetti.vimoney.extensions.top
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.check.AllCardsAdapter
import ru.vincetti.vimoney.ui.check.view.CardViewHolder

@AndroidEntryPoint
class ChecksListFragment : Fragment(R.layout.fragment_checks_list) {

    private val viewModel: CheckListViewModel by viewModels()

    private val binding: FragmentChecksListBinding by viewBinding(FragmentChecksListBinding::bind)

    private lateinit var recyclerAdapter: AllCardsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewsInit()
        insetsInit()

        viewModel.accList.observe(viewLifecycleOwner) {
            recyclerAdapter.setList(it)
        }
        viewModel.needNavigate2Check.observe(viewLifecycleOwner) {
            go2Check(it)
        }
    }

    private fun viewsInit() {
        binding.settingNavigationBackBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.checkListFab.setOnClickListener {
            findNavController().navigate(R.id.action_checksListFragment_to_addCheckFragment)
        }
        recyclerInit()
    }

    private fun recyclerInit() {
        recyclerAdapter = AllCardsAdapter(
            object : CardViewHolder.Actions {
                override fun onCardClicked(id: Int) {
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
        binding.checkListRecycleView.apply {
            addItemDecoration(lineDivider)
            adapter = recyclerAdapter
        }
    }

    private fun insetsInit() {
        val fabMargin = binding.checkListFab.marginBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.checkListFab) { view, insets ->
            view.updateMargin(bottom = (insets.bottom() + fabMargin))
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.checkListToolbar) { view, insets ->
            view.updateMargin(top = insets.top())
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.checkListRecycleView) { view, insets ->
            view.updatePadding(bottom = insets.bottom())
            insets
        }
    }

    private fun go2Check(id: Int) {
        val bundle = bundleOf("checkID" to id)
        findNavController().navigate(R.id.action_checksListFragment_to_checkFragment, bundle)
    }
}
