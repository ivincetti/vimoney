package ru.vincetti.vimoney.ui.notifications

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.work.WorkManager
import ru.vincetti.modules.core.ui.viewBinding
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentNotificationsBinding
import ru.vincetti.vimoney.extensions.top
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.workers.NotificationWorker

class NotificationFragment : Fragment(R.layout.fragment_notifications) {

    val viewModel: NotificationViewModel by viewModels()

    private val binding: FragmentNotificationsBinding by viewBinding(FragmentNotificationsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewsInit()
        observersInit()
        insetsInit()
    }

    private fun viewsInit() {
        binding.settingNavigationBackBtn.setOnClickListener { viewModel.backButtonClicked() }
        binding.notificationNotifyBtn.setOnClickListener { viewModel.notifyButtonClicked() }
    }

    private fun observersInit() {
        viewModel.need2Navigate2Home.observe(viewLifecycleOwner) { findNavController().navigateUp() }
        viewModel.need2Notify.observe(viewLifecycleOwner) { startService() }
    }

    private fun insetsInit() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.notificationToolbar) { view, insets ->
            view.updateMargin(top = insets.top())
            insets
        }
    }

    private fun startService() {
        WorkManager
            .getInstance(requireContext())
            .enqueue(NotificationWorker.createWorkRequest())
    }
}
