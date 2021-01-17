package ru.vincetti.vimoney.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.vincetti.vimoney.utils.SingleLiveEvent

class NotificationViewModel : ViewModel() {

    val need2Navigate2Home = SingleLiveEvent<Boolean>()

    private var _need2Notify = MutableLiveData<Boolean>()
    val need2Notify: LiveData<Boolean>
        get() = _need2Notify

    init {
        _need2Notify.value = false
    }

    fun backButtonClicked() {
        need2Navigate2Home.value = true
    }

    fun notifyButtonClicked() {
        _need2Notify.value = true
    }

    fun notifyChecked() {
        _need2Notify.value = false
    }
}
