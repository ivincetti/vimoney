package ru.vincetti.vimoney.ui.splash

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import ru.vincetti.vimoney.R

class SplashFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(
                R.layout.activity_splash,
                container,
                false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = cManager.getActiveNetworkInfo()

        val viewModel = ViewModelProviders.of(requireActivity()).get(SplashViewModel::class.java)
        // tmp
        viewModel.networkError.value = (nInfo == null || !nInfo.isConnected())

        viewModel.networkError.observe(this, Observer {
            if (it) alertNetworkDialogShow()
        })

        viewModel.need2Navigate2Home.observe(this, Observer {
            if(it) findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        })
    }

    private fun alertNetworkDialogShow() {
        val builder = AlertDialog.Builder(requireContext())
                .setMessage(resources.getString(R.string.splash_nonetwork_string))
                .setPositiveButton(resources.getString(R.string.splash_nonetwork_positive)) { _, _ ->
                    findNavController().navigate(R.id.action_splashFragment_self)
                }
                .setNegativeButton(resources.getString(R.string.splash_nonetwork_negative)) { dialog, _ ->
                    dialog.dismiss()
                }
        builder.create().show()
    }
}