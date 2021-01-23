package ru.vincetti.vimoney.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentSettingsBinding
import ru.vincetti.vimoney.extensions.updateMargin

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewsInit()
        observersInit()
        insetsInit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun viewsInit() {
        binding.settingNavigationBackBtn.setOnClickListener { viewModel.backButtonClicked() }
        binding.settingsCategoriesBtn.setOnClickListener { viewModel.categoriesButtonClicked() }
        binding.saveTransactionsBtn.setOnClickListener { viewModel.saveJson() }
        binding.loadTransactionsBtn.setOnClickListener { viewModel.loadJson() }
    }

    private fun observersInit() {
        viewModel.need2Navigate2Home.observe(viewLifecycleOwner) {
            if (it) findNavController().navigateUp()
        }
        viewModel.need2Navigate2Categories.observe(viewLifecycleOwner) {
            if (it) findNavController().navigate(R.id.action_settingsFragment_to_categoriesFragment)
        }
        viewModel.buttonsState.observe(viewLifecycleOwner) {
            binding.saveTransactionsBtn.isEnabled = it
            binding.loadTransactionsBtn.isEnabled = it
        }
    }

    private fun insetsInit() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.settingsToolbar) { view, insets ->
            view.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
    }
}
