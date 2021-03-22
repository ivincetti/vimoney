package ru.vincetti.vimoney.ui.check.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.core.models.Filter
import ru.vincetti.modules.core.ui.viewBinding
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentCheckBinding
import ru.vincetti.vimoney.extensions.bottom
import ru.vincetti.vimoney.extensions.top
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.check.add.AddCheckFragment
import ru.vincetti.vimoney.ui.history.HistoryFragment
import ru.vincetti.vimoney.ui.transaction.main.TransactionMainFragment
import javax.inject.Inject

@AndroidEntryPoint
class CheckFragment : Fragment(R.layout.fragment_check) {

    @Inject
    lateinit var viewModelFactory: CheckViewModel.CheckViewModelFactory

    private var checkId: Int = Account.DEFAULT_CHECK_ID

    private val viewModel: CheckViewModel by viewModels {
        CheckViewModel.provideFactory(viewModelFactory, checkId)
    }

    private val binding: FragmentCheckBinding by viewBinding(FragmentCheckBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { bundle ->
            val extraCheck = bundle.getInt(EXTRA_CHECK_ID)
            if (extraCheck > 0) checkId = extraCheck
        }

        initViews()
        observersInit()
        insetsInit()
    }

    private fun initViews() {
        binding.checkNavigationDeleteBtn.setOnClickListener { showDeleteDialog() }

        binding.checkNavigationFromArchiveBtn.setOnClickListener { viewModel.restore() }
        binding.checkNavigationUpdateBtn.setOnClickListener { viewModel.update() }

        binding.settingNavigationBackBtn.setOnClickListener { findNavController().navigateUp() }
        binding.checkNavigationEditBtn.setOnClickListener { navigateToCheckWithId(checkId) }
        binding.checkFab.setOnClickListener { navigateToTransactionWithCheckId(checkId) }
    }

    private fun observersInit() {
        viewModel.content.observe(viewLifecycleOwner) {
            when (it) {
                is CheckViewModel.State.Content -> handleContent(it)
                CheckViewModel.State.Error -> Unit
                CheckViewModel.State.Loading -> Unit
            }
        }
    }

    private fun handleContent(content: CheckViewModel.State.Content) {
        binding.checkNavigationUpdateBtn.isEnabled = content.updating
        binding.fragmentCheckContent.checkAccName.text = content.checkName
        binding.fragmentCheckContent.checkAccType.text = content.checkType
        binding.fragmentCheckContent.checkAccBalance.text = content.checkBalance
        binding.fragmentCheckContent.checkAccLabel.setBackgroundColor(Color.parseColor(content.checkLabelColor))
        binding.fragmentCheckContent.checkAccSymbol.text = content.checkSymbol
        binding.fragmentCheckContent.checkAccVisible.isGone = content.isNeedOnMain

        binding.fragmentCheckContent.checkAccArchive.isVisible = content.isArchive
        binding.fragmentCheckContent.checkAccArchive.text = getString(R.string.check_arсhive_txt)
        binding.checkNavigationFromArchiveBtn.isVisible = content.isArchive
        binding.checkNavigationDeleteBtn.isGone = content.isArchive

        showTransactionsHistory(content.checkId)
    }

    private fun showTransactionsHistory(checkId: Int) {
        val historyFragment = HistoryFragment()
        val args = Filter().apply { accountID = checkId }.createBundle()
        historyFragment.arguments = args

        childFragmentManager
            .beginTransaction()
            .replace(R.id.check_history_container, historyFragment)
            .commit()
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(requireContext(), R.style.AlertDialog)
            .setMessage(R.string.check_delete_alert_question)
            .setNegativeButton(R.string.check_delete_alert_negative) { dialog, _ -> dialog?.dismiss() }
            .setPositiveButton(R.string.check_delete_alert_positive) { _, _ -> viewModel.delete() }
            .create()
            .show()
    }

    private fun navigateToCheckWithId(checkId: Int) {
        findNavController().navigate(
            R.id.action_checkFragment_to_addCheckFragment,
            AddCheckFragment.createArgs(checkId)
        )
    }

    private fun navigateToTransactionWithCheckId(checkId: Int) {
        findNavController().navigate(
            R.id.action_global_transactionMainFragment,
            TransactionMainFragment.createArgsWithCheckId(checkId),
        )
    }

    private fun insetsInit() {
        val fabMargin = binding.checkFab.marginBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.checkFab) { view, insets ->
            view.updateMargin(bottom = (insets.bottom() + fabMargin))
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.checkToolbar) { view, insets ->
            view.updateMargin(top = insets.top())
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.fragmentCheckContent.checkHistoryContainerRoot) { view, insets ->
            view.updatePadding(bottom = insets.bottom())
            insets
        }
    }

    companion object {

        private const val EXTRA_CHECK_ID = "Extra_check_id"

        fun createArgs(checkId: Int): Bundle = Bundle().apply {
            putInt(EXTRA_CHECK_ID, checkId)
        }
    }
}
