package ru.vincetti.vimoney.ui.settings

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.repository.PrefsRepo
import ru.vincetti.vimoney.extensions.updateMargin

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModels { viewModelFactory }
    private lateinit var viewModelFactory: SettingsViewModelFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefsRepo = PrefsRepo(requireContext())
        viewModelFactory = SettingsViewModelFactory(prefsRepo)

        viewsInit()
        observersInit()
        insetsInit()
    }

    override fun onResume() {
        super.onResume()

        viewModel.checkNonObservablesState()
    }

    private fun viewsInit() {
        setting_navigation_back_btn.setOnClickListener { viewModel.backButtonClicked() }
        btn_settings_categories.setOnClickListener { viewModel.categoriesButtonClicked() }
        btn_save_transactions.setOnClickListener { viewModel.saveJson() }
        btn_load_transactions.setOnClickListener { viewModel.loadJson() }
        switch_settings_security.setOnClickListener { viewModel.securitySwitchClicked() }
    }

    private fun observersInit() {
        viewModel.need2Navigate2Home.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigateUp()
                viewModel.navigated2Home()
            }
        }
        viewModel.need2Navigate2Categories.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_settingsFragment_to_categoriesFragment)
                viewModel.navigated2Categories()
            }
        }
        viewModel.need2Navigate2Pin.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_settingsFragment_to_setPinFragment)
                viewModel.navigated2Pin()
            }
        }
        viewModel.pinSwitchState.observe(viewLifecycleOwner) {
            switch_settings_security.isChecked = it
        }
        viewModel.exportButtonState.observe(viewLifecycleOwner) {
            btn_save_transactions.isEnabled = it
        }
        viewModel.importButtonState.observe(viewLifecycleOwner) {
            btn_load_transactions.isEnabled = it
        }
    }

    private fun insetsInit() {
        ViewCompat.setOnApplyWindowInsetsListener(settings_toolbar) { _, insets ->
            settings_toolbar.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
    }
}
