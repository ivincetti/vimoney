package ru.vincetti.vimoney.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentSettingsBinding.inflate(inflater)
        val viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        binding.settingNavigationBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_homeFragment)
        }

        binding.btnSaveTransactions.setOnClickListener {
            viewModel.saveJson()
        }
        binding.btnLoadTransactions.setOnClickListener {
            viewModel.loadJson()
        }
        return binding.root
    }
}