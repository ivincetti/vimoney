package ru.vincetti.vimoney.ui.splash

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private lateinit var viewModel: SplashViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentSplashBinding.inflate(inflater).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)

        val cManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = cManager.getActiveNetworkInfo()

        if (nInfo == null || !nInfo.isConnected()) viewModel.setNoNetwork()
        viewModel.networkError.observe(viewLifecycleOwner, Observer {
            if (it) alertNetworkDialogShow()
        })

        viewModel.need2Navigate2Home.observe(viewLifecycleOwner, Observer {
            if (it) findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        })
    }

    private fun alertNetworkDialogShow() {
        val builder = AlertDialog.Builder(requireContext())
                .setMessage(resources.getString(R.string.splash_nonetwork_string))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.splash_nonetwork_positive)) { _, _ ->
                    viewModel.resetNetworkStatus()
                    findNavController().navigate(R.id.action_splashFragment_self)
                }
                .setNegativeButton(resources.getString(R.string.splash_nonetwork_negative)) { _, _ ->
                    activity?.finish()
                }
        builder.create().show()
    }
}