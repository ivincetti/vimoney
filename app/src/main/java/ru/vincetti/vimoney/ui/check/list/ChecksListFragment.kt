package ru.vincetti.vimoney.ui.check.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.ui.check.view.CheckViewModel
import ru.vincetti.vimoney.data.adapters.AllCardsListRVAdapter
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.databinding.FragmentChecksListBinding

class ChecksListFragment : Fragment() {
    private lateinit var binding: FragmentChecksListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChecksListBinding.inflate(inflater)
        initViews()

        val application = requireNotNull(activity).application
        val db = AppDatabase.getInstance(application)
        val viewModelFactory = CheckListModelFactory(db.accountDao())
        val viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(CheckListViewModel::class.java)

        // список карт/счетов
        val adapter = AllCardsListRVAdapter {
            val bundle = Bundle()
            bundle.putInt(CheckViewModel.EXTRA_CHECK_ID, it)
            findNavController().navigate(R.id.action_checksListFragment_to_checkFragment, bundle)
        }
        val cardsLayoutManager = LinearLayoutManager(requireContext(),
                RecyclerView.VERTICAL, false)
        binding.checkListRecycleView.apply {
            layoutManager = cardsLayoutManager
            setAdapter(adapter)
        }
        viewModel.accList.observe(this, Observer {
            it?.let {
                adapter.setList(it)
            }
        })

        return binding.root
    }

    private fun initViews() {
        binding.settingNavigationBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_checksListFragment_to_homeFragment)
        }
        binding.checkListFab.setOnClickListener {
            findNavController().navigate(R.id.action_checksListFragment_to_addCheckFragment)
        }
    }
}
