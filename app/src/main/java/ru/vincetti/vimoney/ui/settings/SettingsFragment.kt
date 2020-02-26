package ru.vincetti.vimoney.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.vincetti.vimoney.R

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        setting_navigation_back_btn.setOnClickListener {
            viewModel.homeButton()
        }

        btn_save_transactions.setOnClickListener {
            viewModel.saveJson()
        }
        btn_load_transactions.setOnClickListener {
            viewModel.loadJson()
        }

        viewModel.need2Navigate2Home.observe(viewLifecycleOwner, Observer {
            if (it) findNavController().navigate(R.id.action_settingsFragment_to_homeFragment)
        })
    }
}