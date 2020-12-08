package ru.vincetti.vimoney.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationViewModel : ViewModel() {

    private var _need2Navigate2Home = MutableLiveData<Boolean>()
    val need2Navigate2Home: LiveData<Boolean>
        get() = _need2Navigate2Home

    private var _need2Notify = MutableLiveData<Boolean>()
    val need2Notify: LiveData<Boolean>
        get() = _need2Notify

    init {
        _need2Navigate2Home.value = false
        _need2Notify.value = false
    }

    fun backButtonClicked() {
        _need2Navigate2Home.value = true
    }

    fun navigatedBack() {
        _need2Navigate2Home.value = false
    }

    fun notifyButtonClicked() {
        _need2Notify.value = true
    }

    fun notifyChecked() {
        _need2Notify.value = false
    }
}
