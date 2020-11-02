package ru.vincetti.vimoney.ui.login

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_set_pin.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.repository.PrefsRepo
import ru.vincetti.vimoney.extensions.updateMargin

class SetPinFragment : Fragment(R.layout.fragment_set_pin) {

    private val viewModel: SetPinViewModel by viewModels { viewModelFactory }
    private val sharedViewModel: SharedPinViewModel by activityViewModels()
    private lateinit var viewModelFactory: SetPinViewModelFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefsRepo = PrefsRepo(requireContext())
        viewModelFactory = SetPinViewModelFactory(prefsRepo)

        viewsInit()
        observersInit()
        insetsInit()
    }

    private fun viewsInit() {
        set_pin_navigation_back_btn.setOnClickListener { viewModel.backButtonClicked() }
    }

    private fun observersInit() {
        viewModel.need2Navigate2Settings.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigateUp()
                viewModel.navigated2Settings()
            }
        }
        viewModel.need2CheckPin.observe(viewLifecycleOwner) {
            if (it) {
                sharedViewModel.need2check()
                viewModel.needCheckProcessed()
            }
        }
        viewModel.need2ReenterPin.observe(viewLifecycleOwner) {
            if (it) {
                sharedViewModel.need2Reenter()
                viewModel.reenterPinProcessed()
            }
        }
        sharedViewModel.pin.observe(viewLifecycleOwner) {
            viewModel.setPinFromPad(it)
        }
    }

    private fun insetsInit() {
        ViewCompat.setOnApplyWindowInsetsListener(set_pin_toolbar) { _, insets ->
            set_pin_toolbar.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(set_pin_fragment_container) { _, insets ->
            set_pin_fragment_container.updateMargin(bottom = insets.systemWindowInsetBottom)
            insets
        }
    }
}
