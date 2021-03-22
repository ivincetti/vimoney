package ru.vincetti.vimoney.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.vincetti.modules.core.utils.SingleLiveEvent

class NotificationViewModel : ViewModel() {

    private val _need2Navigate2Home = SingleLiveEvent<Unit>()
    val need2Navigate2Home: LiveData<Unit>
        get() = _need2Navigate2Home

    private var _need2Notify = SingleLiveEvent<Unit>()
    val need2Notify: LiveData<Unit>
        get() = _need2Notify

    fun backButtonClicked() {
        _need2Navigate2Home.value = Unit
    }

    fun notifyButtonClicked() {
        _need2Notify.value = Unit
    }
}
