package ru.vincetti.vimoney.ui.check.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.databinding.FragmentCheckBinding
import ru.vincetti.vimoney.transaction.TransactionConst
import ru.vincetti.vimoney.ui.check.AccountConst
import ru.vincetti.vimoney.ui.history.HistoryFragment

class CheckFragment : Fragment() {

    private lateinit var binding: FragmentCheckBinding
    private lateinit var viewModel: CheckViewModel
    private var checkId: Int = CheckViewModel.DEFAULT_CHECK_ID

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application
        val db = AppDatabase.getInstance(application)
        arguments?.let { bundle ->
            val extraCheck = bundle.getInt(CheckViewModel.EXTRA_CHECK_ID)
            if (extraCheck > 0) checkId = extraCheck
        }
        val viewModelFactory = CheckViewModelFactory(db.accountDao(), checkId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CheckViewModel::class.java)
        initViews()
        return binding.root
    }

    private fun initViews() {
        loadAccount()

        binding.checkNavigationDeleteBtn.setOnClickListener {
            showDeleteDialog()
        }
        binding.checkNavigationFromArchiveBtn.setOnClickListener {
            viewModel.restore()
        }

        binding.checkNavigationEditBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(AccountConst.EXTRA_CHECK_ID, checkId)
            findNavController().navigate(R.id.action_checkFragment_to_addCheckFragment, bundle)
        }
        binding.settingNavigationBackBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.checkFab.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(TransactionConst.EXTRA_ACCOUNT_ID, checkId)
            findNavController().navigate(R.id.action_checkFragment_to_transactionMainFragment, bundle)
        }
    }

    // account data to UI
    private fun loadAccount() {
        viewModel.model.observe(this, Observer {
            it?.let {
                binding.fragmentCheckContent.checkAccName.text = it.name
                binding.fragmentCheckContent.checkAccType.text = it.type
                binding.fragmentCheckContent.checkAccBalance.text = it.sum.toString()
                binding.fragmentCheckContent.checkAccContainer
                        .setBackgroundColor(Color.parseColor(it.color))

                if (it.isArchive) {
                    binding.fragmentCheckContent.checkAccArchive.visibility = View.VISIBLE
                    binding.fragmentCheckContent.checkAccArchive.text = getString(R.string.check_arсhive_txt)
                    binding.checkNavigationFromArchiveBtn.visibility = View.VISIBLE
                    binding.checkNavigationDeleteBtn.visibility = View.GONE
                } else {
                    binding.fragmentCheckContent.checkAccArchive.visibility = View.INVISIBLE
                    binding.checkNavigationFromArchiveBtn.visibility = View.GONE
                    binding.checkNavigationDeleteBtn.visibility = View.VISIBLE

                }
                binding.fragmentCheckContent.checkAccSymbol.text = it.curSymbol
                showTransactionsHistory(it.id)
            }
        })
    }

    // show transaction for this account
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
                .setPositiveButton(R.string.check_delete_alert_positive)
                { _, _ ->
                    viewModel.delete()
                }

        builder.create().show()
    }
}
