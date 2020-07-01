package ru.vincetti.vimoney.ui.notifications

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_notifications.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.extensions.updateMargin

class NotificationFragment : Fragment(R.layout.fragment_notifications) {

    val viewModel: NotificationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.need2Navigate2Home.observe(viewLifecycleOwner, Observer {
            if (it) findNavController().navigate(R.id.action_notificationFragment_to_homeFragment)
        })

        setting_navigation_back_btn.setOnClickListener {
            viewModel.homeButton()
        }

        notification_notify_btn.setOnClickListener {
            viewModel.notifyButton()
        }

        insetsInit()
    }

    private fun insetsInit() {
        ViewCompat.setOnApplyWindowInsetsListener(notification_toolbar) { _, insets ->
            notification_toolbar.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
    }
}