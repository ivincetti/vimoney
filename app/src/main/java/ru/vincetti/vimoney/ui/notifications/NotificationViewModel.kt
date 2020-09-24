package ru.vincetti.vimoney.ui.notifications

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.vincetti.vimoney.App
import ru.vincetti.vimoney.service.NotificationService

class NotificationViewModel : ViewModel() {

    private var _need2Navigate2Home = MutableLiveData<Boolean>()
    val need2Navigate2Home: LiveData<Boolean>
        get() = _need2Navigate2Home

    init {
        _need2Navigate2Home.value = false
    }

    fun backButtonClicked() {
        _need2Navigate2Home.value = true
    }

    fun navigatedBack() {
        _need2Navigate2Home.value = false
    }

    fun notifyButton() {
        App.context?.let {
            it.startService(
                Intent(it, NotificationService::class.java)
                    .setAction(NotificationService.NOTIFICATION_ACTION)
            )
        }
    }
}
