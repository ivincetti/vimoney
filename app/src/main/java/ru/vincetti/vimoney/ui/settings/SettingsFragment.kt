package ru.vincetti.vimoney.ui.settings

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.extensions.updateMargin

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.need2Navigate2Home.observe(viewLifecycleOwner, Observer {
            if (it) findNavController().navigate(R.id.action_settingsFragment_to_homeFragment)
        })
        viewModel.need2Navigate2Categories.observe(viewLifecycleOwner, Observer {
            if (it) findNavController().navigate(R.id.action_settingsFragment_to_categoriesFragment)
        })
        viewModel.exportButtonState.observe(viewLifecycleOwner, Observer {
            btn_save_transactions.isEnabled = it
        })
        viewModel.importButtonState.observe(viewLifecycleOwner, Observer {
            btn_load_transactions.isEnabled = it
        })

        setting_navigation_back_btn.setOnClickListener { viewModel.backButtonClicked() }
        btn_settings_categories.setOnClickListener { viewModel.categoriesButtonClicked() }
        btn_save_transactions.setOnClickListener { viewModel.saveJson() }
        btn_load_transactions.setOnClickListener { viewModel.loadJson() }

        insetsInit()
    }

    private fun insetsInit() {
        ViewCompat.setOnApplyWindowInsetsListener(settings_toolbar) { _, insets ->
            settings_toolbar.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
    }
}
