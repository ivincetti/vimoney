package ru.vincetti.vimoney.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.vincetti.vimoney.R

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.need2Navigate2Home.observe(viewLifecycleOwner, Observer {
            if (it) findNavController().navigate(R.id.action_settingsFragment_to_homeFragment)
        })
        viewModel.importButtonState.observe(viewLifecycleOwner, Observer {
            btn_save_transactions.isEnabled = it
        })
        viewModel.exportButtonState.observe(viewLifecycleOwner, Observer {
            btn_load_transactions.isEnabled = it
        })

        setting_navigation_back_btn.setOnClickListener {
            viewModel.homeButton()
        }
        btn_save_transactions.setOnClickListener {
            btn_save_transactions.isEnabled = false
            viewModel.saveJson()
        }
        btn_load_transactions.setOnClickListener {
            btn_load_transactions.isEnabled = false
            viewModel.loadJson()
        }
    }
}