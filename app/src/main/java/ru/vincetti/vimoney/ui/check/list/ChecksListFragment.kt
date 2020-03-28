package ru.vincetti.vimoney.ui.check.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_checks_list.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.adapters.AllCardsListRVAdapter
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.ui.check.EXTRA_CHECK_ID

class ChecksListFragment : Fragment(R.layout.fragment_checks_list) {

    private val viewModel: CheckListViewModel by viewModels { viewModelFactory }

    private lateinit var viewModelFactory: CheckListModelFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        val application = requireNotNull(activity).application
        val db = AppDatabase.getInstance(application)
        viewModelFactory = CheckListModelFactory(db.accountDao())

        // список карт/счетов
        val adapter = AllCardsListRVAdapter {
            val bundle = Bundle()
            bundle.putInt(EXTRA_CHECK_ID, it)
            findNavController().navigate(R.id.action_checksListFragment_to_checkFragment, bundle)
        }
        val cardsLayoutManager = LinearLayoutManager(requireContext())
        check_list_recycle_view.apply {
            layoutManager = cardsLayoutManager
            setAdapter(adapter)
        }
        viewModel.accList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.setList(it)
            }
        })
    }

    private fun initViews() {
        setting_navigation_back_btn.setOnClickListener {
            findNavController().navigate(R.id.action_checksListFragment_to_homeFragment)
        }
        check_list_fab.setOnClickListener {
            findNavController().navigate(R.id.action_checksListFragment_to_addCheckFragment)
        }
    }
}
