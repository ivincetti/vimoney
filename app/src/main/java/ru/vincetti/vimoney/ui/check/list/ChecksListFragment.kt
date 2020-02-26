package ru.vincetti.vimoney.ui.check.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_checks_list.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.adapters.AllCardsListRVAdapter
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.ui.check.view.CheckViewModel

class ChecksListFragment : Fragment(R.layout.fragment_checks_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        val application = requireNotNull(activity).application
        val db = AppDatabase.getInstance(application)
        val viewModelFactory = CheckListModelFactory(db.accountDao())
        val viewModel = ViewModelProvider(this, viewModelFactory)
                .get(CheckListViewModel::class.java)

        // список карт/счетов
        val adapter = AllCardsListRVAdapter {
            val bundle = Bundle()
            bundle.putInt(CheckViewModel.EXTRA_CHECK_ID, it)
            findNavController().navigate(R.id.action_checksListFragment_to_checkFragment, bundle)
        }
        val cardsLayoutManager = LinearLayoutManager(requireContext(),
                RecyclerView.VERTICAL, false)
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
