package ru.vincetti.vimoney.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedPinViewModel : ViewModel() {

    private var _pin = MutableLiveData<String>()
    val pin: LiveData<String>
        get() = _pin

    private var _need2ReenterPin = MutableLiveData<Boolean>()
    val need2ReenterPin: LiveData<Boolean>
        get() = _need2ReenterPin

    private var _need2CheckPin = MutableLiveData<Boolean>()
    val need2CheckPin: LiveData<Boolean>
        get() = _need2CheckPin

    init {
        _need2ReenterPin.value = false
        _need2CheckPin.value = false
    }

    fun pinSet(pin: String) {
        _pin.value = pin
    }

    fun need2check() {
        _need2CheckPin.value = true
    }
    fun need2RCheckPinProcessed(){
        _need2CheckPin.value = false
    }

    fun need2Reenter() {
        _need2ReenterPin.value = true
    }

    fun need2ReenterPinProcessed(){
        _need2ReenterPin.value = false
    }
}
