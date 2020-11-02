package ru.vincetti.vimoney.ui.login

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_login.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.repository.PrefsRepo
import ru.vincetti.vimoney.extensions.updateMargin

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels { viewModelFactory }
    private val sharedViewModel: SharedPinViewModel by activityViewModels()
    private lateinit var viewModelFactory: LoginViewModelFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefsRepo = PrefsRepo(requireContext())
        viewModelFactory = LoginViewModelFactory(prefsRepo)

        observersInit()
        insetsInit()
    }

    private fun observersInit() {
        viewModel.need2Navigate2Home.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                viewModel.navigated2Home()
            }
        }
        viewModel.need2ReenterPin.observe(viewLifecycleOwner){
            if (it) {
                sharedViewModel.need2Reenter()
                viewModel.need2ReenterPinProcessed()
            }
        }
        sharedViewModel.pin.observe(viewLifecycleOwner) {
            viewModel.checkPin(it)
        }
    }

    private fun insetsInit() {
        ViewCompat.setOnApplyWindowInsetsListener(login_fragment_container) { _, insets ->
            login_fragment_container.updateMargin(bottom = insets.systemWindowInsetBottom)
            insets
        }
    }
}
