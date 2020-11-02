package ru.vincetti.vimoney.ui.pin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PinViewModel : ViewModel() {

    private var _pin = MutableLiveData<String>()
    val pin: LiveData<String>
        get() = _pin

    fun pinSet(pin: String) {
        _pin.value = pin
    }
}
