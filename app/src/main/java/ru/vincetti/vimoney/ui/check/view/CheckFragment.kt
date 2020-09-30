package ru.vincetti.vimoney.ui.check.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_check.*
import kotlinx.android.synthetic.main.fragment_check_content.*
import kotlinx.android.synthetic.main.fragment_check_content.view.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.check.DEFAULT_CHECK_ID
import ru.vincetti.vimoney.ui.check.EXTRA_CHECK_ID
import ru.vincetti.vimoney.ui.history.HistoryFragment
import ru.vincetti.vimoney.ui.history.filter.Filter
import ru.vincetti.vimoney.ui.transaction.TransactionConst

class CheckFragment : Fragment(R.layout.fragment_check) {

    private val viewModel: CheckViewModel by viewModels { viewModelFactory }

    private var checkId: Int = DEFAULT_CHECK_ID
    private lateinit var viewModelFactory: CheckViewModelFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(activity).application
        val db = AppDatabase.getInstance(application)
        arguments?.let { bundle ->
            val extraCheck = bundle.getInt(EXTRA_CHECK_ID)
            if (extraCheck > 0) checkId = extraCheck
        }
        viewModelFactory = CheckViewModelFactory(db, checkId)
        initViews()
        observersInit()
        insetsInit()
    }

    private fun initViews() {
        check_navigation_delete_btn.setOnClickListener { showDeleteDialog() }
        check_navigation_from_archive_btn.setOnClickListener { viewModel.restore() }
        check_navigation_update_btn.setOnClickListener { viewModel.update() }
        setting_navigation_back_btn.setOnClickListener { findNavController().navigateUp() }
        check_navigation_edit_btn.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(EXTRA_CHECK_ID, checkId)
            findNavController().navigate(R.id.action_checkFragment_to_addCheckFragment, bundle)
        }
        check_fab.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(TransactionConst.EXTRA_ACCOUNT_ID, checkId)
            findNavController().navigate(R.id.action_global_transactionMainFragment, bundle)
        }
    }

    private fun observersInit() {
        viewModel.account.observe(viewLifecycleOwner) {
            it?.let {
                fragment_check_content.check_acc_name.text = it.name
                fragment_check_content.check_acc_type.text = it.type
                fragment_check_content.check_acc_balance.text = it.sum.toString()
                fragment_check_content.check_acc_label
                    .setBackgroundColor(Color.parseColor(it.color))
                fragment_check_content.check_acc_symbol.text = it.curSymbol
                showTransactionsHistory(it.id)
            }
        }
        viewModel.isArchive.observe(viewLifecycleOwner) {
            if (it) {
                fragment_check_content.check_acc_archive.visibility = View.VISIBLE
                fragment_check_content.check_acc_archive.text = getString(R.string.check_arсhive_txt)
                check_navigation_from_archive_btn.visibility = View.VISIBLE
                check_navigation_delete_btn.visibility = View.GONE
            } else {
                fragment_check_content.check_acc_archive.visibility = View.INVISIBLE
                check_navigation_from_archive_btn.visibility = View.GONE
                check_navigation_delete_btn.visibility = View.VISIBLE
            }
        }
        viewModel.isNeedOnMain.observe(viewLifecycleOwner) {
            if (!it) fragment_check_content.check_acc_visible.visibility = View.VISIBLE
        }
        viewModel.updateButtonEnable.observe(viewLifecycleOwner) {
            check_navigation_update_btn.isEnabled = it
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
        val fabMargin = check_fab.marginBottom
        ViewCompat.setOnApplyWindowInsetsListener(check_fab) { _, insets ->
            check_fab.updateMargin(bottom = (insets.systemWindowInsetBottom + fabMargin))
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(check_toolbar) { _, insets ->
            check_toolbar.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(check_history_container_root) { _, insets ->
            check_history_container_root.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
    }
}
