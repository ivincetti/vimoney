package ru.vincetti.vimoney.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentNotificationsBinding
import ru.vincetti.vimoney.service.NotificationService

class NotificationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentNotificationsBinding.inflate(inflater)
        binding.settingNavigationBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_notificationFragment_to_homeFragment)
        }
        binding.notificationNotifyBtn.setOnClickListener {
            requireActivity().startService(
                    Intent(requireContext(), NotificationService::class.java)
                            .setAction(NotificationService.NOTIFICATION_ACTION))
        }
        return binding.root
    }
}