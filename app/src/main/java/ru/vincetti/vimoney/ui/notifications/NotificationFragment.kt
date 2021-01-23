package ru.vincetti.vimoney.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.vincetti.vimoney.databinding.FragmentNotificationsBinding
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.service.NotificationService

class NotificationFragment : Fragment() {

    val viewModel: NotificationViewModel by viewModels()

    private var _binding: FragmentNotificationsBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentNotificationsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewsInit()
        observersInit()
        insetsInit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun viewsInit() {
        binding.settingNavigationBackBtn.setOnClickListener { viewModel.backButtonClicked() }
        binding.notificationNotifyBtn.setOnClickListener { viewModel.notifyButtonClicked() }
    }

    private fun observersInit() {
        viewModel.need2Navigate2Home.observe(viewLifecycleOwner) {
            if (it) findNavController().navigateUp()
        }
        viewModel.need2Notify.observe(viewLifecycleOwner) {
            if (it) {
                startService()
                viewModel.notifyChecked()
            }
        }
    }

    private fun insetsInit() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.notificationToolbar) { view, insets ->
            view.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
    }

    private fun startService() {
        requireContext().startService(
            Intent(requireContext(), NotificationService::class.java)
                .setAction(NotificationService.NOTIFICATION_ACTION)
        )
    }
}
