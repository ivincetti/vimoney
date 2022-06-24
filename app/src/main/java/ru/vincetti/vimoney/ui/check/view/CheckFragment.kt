package ru.vincetti.vimoney.ui.check.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.vincetti.modules.core.models.Filter
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentCheckBinding
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.history.HistoryFragment
import ru.vincetti.vimoney.ui.transaction.TransactionConst
import javax.inject.Inject

@AndroidEntryPoint
class CheckFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: CheckViewModel.AssistedFactory

    private val viewModel: CheckViewModel by viewModels {
        CheckViewModel.provideFactory(viewModelFactory, arguments.getCheck())
    }

    private var _binding: FragmentCheckBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        observersInit()
        insetsInit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() {
        binding.checkNavigationDeleteBtn.setOnClickListener { showDeleteDialog() }
        binding.checkNavigationFromArchiveBtn.setOnClickListener { viewModel.restore() }
        binding.checkNavigationUpdateBtn.setOnClickListener { viewModel.update() }
        binding.settingNavigationBackBtn.setOnClickListener { findNavController().navigateUp() }
        binding.checkNavigationEditBtn.setOnClickListener { viewModel.onEditClicked() }
        binding.checkFab.setOnClickListener { viewModel.onAddClicked() }
    }

    private fun observersInit() {
        viewModel.account.observe(viewLifecycleOwner) {
            it?.let {
                binding.fragmentCheckContent.checkAccName.text = it.name
                binding.fragmentCheckContent.checkAccType.text = it.type
                binding.fragmentCheckContent.checkAccBalance.text = it.sum.toString()
                binding.fragmentCheckContent.checkAccLabel
                    .setBackgroundColor(Color.parseColor(it.color))
                binding.fragmentCheckContent.checkAccSymbol.text = it.curSymbol
                showTransactionsHistory(it.id)
            }
        }
        viewModel.isArchive.observe(viewLifecycleOwner) {
            if (it) {
                binding.fragmentCheckContent.checkAccArchive.visibility = View.VISIBLE
                binding.fragmentCheckContent.checkAccArchive.text = getString(R.string.check_arсhive_txt)
                binding.checkNavigationFromArchiveBtn.visibility = View.VISIBLE
                binding.checkNavigationDeleteBtn.visibility = View.GONE
            } else {
                binding.fragmentCheckContent.checkAccArchive.visibility = View.INVISIBLE
                binding.checkNavigationFromArchiveBtn.visibility = View.GONE
                binding.checkNavigationDeleteBtn.visibility = View.VISIBLE
            }
        }
        viewModel.isNeedOnMain.observe(viewLifecycleOwner) {
            if (!it) binding.fragmentCheckContent.checkAccVisible.visibility = View.VISIBLE
        }
        viewModel.updateButtonEnable.observe(viewLifecycleOwner) {
            binding.checkNavigationUpdateBtn.isEnabled = it
        }
        viewModel.navigate2Edit.observe(viewLifecycleOwner) {
            navigate2Edit(it)
        }
        viewModel.navigate2Add.observe(viewLifecycleOwner) {
            navigate2Add(it)
        }
    }

    private fun showTransactionsHistory(checkId: Int) {
        val historyFragment = HistoryFragment()
        val args = Filter().apply {
            accountID = checkId
        }.createBundle()
        historyFragment.arguments = args

        childFragmentManager
            .beginTransaction()
            .replace(R.id.check_history_container, historyFragment)
            .commit()
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(requireContext(), R.style.AlertDialog)
            .setMessage(R.string.check_delete_alert_question)
            .setNegativeButton(R.string.check_delete_alert_negative) { dialog, _ ->
                dialog?.dismiss()
            }
            .setPositiveButton(R.string.check_delete_alert_positive) { _, _ ->
                viewModel.delete()
            }
            .create()
            .show()
    }

    private fun insetsInit() {
        val fabMargin = binding.checkFab.marginBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.checkFab) { view, insets ->
            view.updateMargin(bottom = (insets.systemWindowInsetBottom + fabMargin))
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.checkToolbar) { view, insets ->
            view.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(
            binding.fragmentCheckContent.checkHistoryContainerRoot
        ) { view, insets ->
            view.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
    }

    private fun navigate2Edit(checkID: Int) {
        val bundle = bundleOf(TransactionConst.EXTRA_ACCOUNT_ID to checkID)
        findNavController().navigate(R.id.action_checkFragment_to_addCheckFragment, bundle)
    }

    private fun navigate2Add(checkID: Int) {
        val bundle = bundleOf(TransactionConst.EXTRA_ACCOUNT_ID to checkID)
        findNavController().navigate(R.id.action_global_transactionMainFragment, bundle)
    }

    private fun Bundle?.getCheck(): Int {
        val args = requireNotNull(this)
        return args.getInt("checkID")
    }
}
