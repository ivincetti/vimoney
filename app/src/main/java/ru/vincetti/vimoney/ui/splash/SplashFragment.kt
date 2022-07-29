package ru.vincetti.vimoney.ui.splash

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.ui.splash.SplashViewModel.Action

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observersInit()
    }

    private fun observersInit() {
        viewModel.action.observe(viewLifecycleOwner, ::handleAction)
    }

    private fun handleAction(action: Action) {
        when (action) {
            Action.Error -> alertNetworkDialogShow()
            Action.NavigateMain -> findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            Action.NavigateSelf -> findNavController().navigate(R.id.action_splashFragment_self)
        }
    }

    private fun alertNetworkDialogShow() {
        AlertDialog.Builder(requireContext(), R.style.AlertDialog)
            .setMessage(resources.getString(R.string.splash_nonetwork_string))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.splash_nonetwork_positive)) { _, _ ->
                viewModel.resetNetworkStatus()
            }
            .setNegativeButton(resources.getString(R.string.splash_nonetwork_negative)) { _, _ ->
                requireActivity().finish()
            }
            .create()
            .show()
    }
}
