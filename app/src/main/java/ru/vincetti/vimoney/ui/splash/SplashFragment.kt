package ru.vincetti.vimoney.ui.splash

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.utils.isNetworkAvailable

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private lateinit var viewModel: SplashViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)

        if (!isNetworkAvailable(requireContext())) viewModel.setNoNetwork()
        viewModel.networkError.observe(viewLifecycleOwner, Observer {
            if (it) alertNetworkDialogShow()
        })

        viewModel.need2Navigate2Home.observe(viewLifecycleOwner, Observer {
            if (it) findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        })

        viewModel.need2Navigate2Self.observe(viewLifecycleOwner, Observer {
            if (it) findNavController().navigate(R.id.action_splashFragment_self)
        })
    }

    private fun alertNetworkDialogShow() {
        val builder = AlertDialog.Builder(requireContext())
                .setMessage(resources.getString(R.string.splash_nonetwork_string))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.splash_nonetwork_positive)) { _, _ ->
                    viewModel.resetNetworkStatus()
                }
                .setNegativeButton(resources.getString(R.string.splash_nonetwork_negative)) { _, _ ->
                    activity?.finish()
                }
        builder.create().show()
    }
}