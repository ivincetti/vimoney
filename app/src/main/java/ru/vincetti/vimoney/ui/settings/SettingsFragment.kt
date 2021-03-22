package ru.vincetti.vimoney.ui.settings

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.vincetti.modules.core.ui.viewBinding
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentSettingsBinding
import ru.vincetti.vimoney.extensions.top
import ru.vincetti.vimoney.extensions.updateMargin

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModels()

    private val binding: FragmentSettingsBinding by viewBinding(FragmentSettingsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewsInit()
        observersInit()
        insetsInit()
    }

    private fun viewsInit() {
        binding.settingNavigationBackBtn.setOnClickListener { viewModel.backButtonClicked() }
        binding.settingsCategoriesBtn.setOnClickListener { viewModel.categoriesButtonClicked() }
        binding.saveTransactionsBtn.setOnClickListener { viewModel.saveJson() }
        binding.loadTransactionsBtn.setOnClickListener { viewModel.loadJson() }
    }

    private fun observersInit() {
        viewModel.buttonsState.observe(viewLifecycleOwner) {
            binding.saveTransactionsBtn.isEnabled = it
            binding.loadTransactionsBtn.isEnabled = it
        }

        viewModel.need2Navigate2Home.observe(viewLifecycleOwner) { findNavController().navigateUp() }
        viewModel.need2Navigate2Categories.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_settingsFragment_to_categoriesFragment)
        }
    }

    private fun insetsInit() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.settingsToolbar) { view, insets ->
            view.updateMargin(top = insets.top())
            insets
        }
    }
}
