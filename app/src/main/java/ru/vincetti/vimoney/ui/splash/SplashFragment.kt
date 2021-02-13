package ru.vincetti.vimoney.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentSplashBinding

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val viewModel: SplashViewModel by viewModels()

    private var _binding: FragmentSplashBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSplashBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observersInit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observersInit() {
        viewModel.networkError.observe(viewLifecycleOwner) {
            if (it) alertNetworkDialogShow()
        }
        viewModel.need2Navigate2Home.observe(viewLifecycleOwner) {
            if (it) findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        }
        viewModel.need2Navigate2Self.observe(viewLifecycleOwner) {
            if (it) findNavController().navigate(R.id.action_splashFragment_self)
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
