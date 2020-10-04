package ru.vincetti.vimoney.ui.splash

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.vincetti.vimoney.R

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observersInit()
    }

    private fun observersInit() {
        viewModel.networkError.observe(viewLifecycleOwner) {
            if (it) alertNetworkDialogShow()
        }
        viewModel.need2Navigate2Home.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                viewModel.navigated2Home()
            }
        }
        viewModel.need2Navigate2Self.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_splashFragment_self)
            }
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
