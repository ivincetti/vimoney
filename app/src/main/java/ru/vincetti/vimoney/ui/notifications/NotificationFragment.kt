package ru.vincetti.vimoney.ui.notifications

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_notifications.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.extensions.updateMargin

class NotificationFragment : Fragment(R.layout.fragment_notifications) {

    val viewModel: NotificationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewsInit()
        observersInit()
        insetsInit()
    }

    private fun viewsInit() {
        setting_navigation_back_btn.setOnClickListener { viewModel.backButtonClicked() }
        notification_notify_btn.setOnClickListener { viewModel.notifyButton() }
    }

    private fun observersInit() {
        viewModel.need2Navigate2Home.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigateUp()
                viewModel.navigatedBack()
            }
        }
    }

    private fun insetsInit() {
        ViewCompat.setOnApplyWindowInsetsListener(notification_toolbar) { _, insets ->
            notification_toolbar.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
    }
}
