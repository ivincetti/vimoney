package ru.vincetti.vimoney.ui.check.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_check.*
import kotlinx.android.synthetic.main.fragment_check_content.view.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.ui.check.EXTRA_CHECK_ID
import ru.vincetti.vimoney.ui.history.HistoryFragment
import ru.vincetti.vimoney.ui.transaction.TransactionConst

class CheckFragment : Fragment(R.layout.fragment_check) {

    private val viewModel: CheckViewModel by viewModels { viewModelFactory }

    private var checkId: Int = CheckViewModel.DEFAULT_CHECK_ID
    private lateinit var viewModelFactory: CheckViewModelFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(activity).application
        val db = AppDatabase.getInstance(application)
        arguments?.let { bundle ->
            val extraCheck = bundle.getInt(CheckViewModel.EXTRA_CHECK_ID)
            if (extraCheck > 0) checkId = extraCheck
        }
        viewModelFactory = CheckViewModelFactory(db.accountDao(), checkId)
        initViews()
    }

    private fun initViews() {
        loadAccount()

        check_navigation_delete_btn.setOnClickListener {
            showDeleteDialog()
        }
        check_navigation_from_archive_btn.setOnClickListener {
            viewModel.restore()
        }
        check_navigation_edit_btn.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(EXTRA_CHECK_ID, checkId)
            findNavController().navigate(R.id.action_checkFragment_to_addCheckFragment, bundle)
        }
        setting_navigation_back_btn.setOnClickListener {
            findNavController().navigateUp()
        }
        check_fab.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(TransactionConst.EXTRA_ACCOUNT_ID, checkId)
            findNavController().navigate(R.id.action_global_transactionMainFragment, bundle)
        }
    }

    /** Account data to UI. */
    private fun loadAccount() {
        viewModel.model.observe(viewLifecycleOwner, Observer {
            it?.let {
                fragment_check_content.check_acc_name.text = it.name
                fragment_check_content.check_acc_type.text = it.type
                fragment_check_content.check_acc_balance.text = it.sum.toString()
                fragment_check_content.check_acc_container
                        .setBackgroundColor(Color.parseColor(it.color))

                if (it.isArchive) {
                    fragment_check_content.check_acc_archive.visibility = View.VISIBLE
                    fragment_check_content.check_acc_archive.text = getString(R.string.check_arсhive_txt)
                    check_navigation_from_archive_btn.visibility = View.VISIBLE
                    check_navigation_delete_btn.visibility = View.GONE
                } else {
                    fragment_check_content.check_acc_archive.visibility = View.INVISIBLE
                    check_navigation_from_archive_btn.visibility = View.GONE
                    check_navigation_delete_btn.visibility = View.VISIBLE

                }
                fragment_check_content.check_acc_symbol.text = it.curSymbol
                showTransactionsHistory(it.id)
            }
        })
    }

    /** Show transaction for this account. */
    private fun showTransactionsHistory(checkId: Int) {
        val historyFragment = HistoryFragment()
        val args = Bundle()
        args.putInt(HistoryFragment.BUNDLE_TRANS_COUNT_NAME, CheckViewModel.DEFAULT_CHECK_COUNT)
        args.putInt(HistoryFragment.BUNDLE_TRANS_CHECK_ID_NAME, checkId)
        historyFragment.arguments = args

        childFragmentManager
                .beginTransaction()
                .replace(R.id.check_history_container, historyFragment)
                .commit()
    }

    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(requireContext())
                .setMessage(R.string.check_delete_alert_question)
                .setNegativeButton(R.string.check_delete_alert_negative) { dialog, _ ->
                    dialog?.dismiss()
                }
                .setPositiveButton(R.string.check_delete_alert_positive) { _, _ ->
                    viewModel.delete()
                }
        builder.create().show()
    }
}
