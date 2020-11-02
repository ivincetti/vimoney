package ru.vincetti.vimoney.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.vincetti.vimoney.data.repository.PrefsRepo

class SetPinViewModel(private val prefsRepo: PrefsRepo) : ViewModel() {

    private var _need2Navigate2Settings = MutableLiveData<Boolean>()
    val need2Navigate2Settings: LiveData<Boolean>
        get() = _need2Navigate2Settings

    private var _need2ReenterPin = MutableLiveData<Boolean>()
    val need2ReenterPin: LiveData<Boolean>
        get() = _need2ReenterPin

    private var _need2CheckPin = MutableLiveData<Boolean>()
    val need2CheckPin: LiveData<Boolean>
        get() = _need2CheckPin

    private var tmpPin = ""
    private var attempts = 0

    init {
        _need2Navigate2Settings.value = false
        _need2ReenterPin.value = false
        _need2CheckPin.value = false
    }

    fun backButtonClicked() {
        _need2Navigate2Settings.value = true
    }

    fun navigated2Settings() {
        _need2Navigate2Settings.value = false
    }

    fun reenterPinProcessed() {
        _need2ReenterPin.value = false
    }

    fun needCheckProcessed(){
        _need2CheckPin.value = false
    }

    fun setPinFromPad(tmpPin: String) {
        checkPin(tmpPin)
    }

    private fun checkPin(pinForCheck: String) {
        when (attempts) {
            0 -> {
                tmpPin = pinForCheck
                attempts++
                _need2CheckPin.value = true
            }
            1 -> {
                if (tmpPin == pinForCheck) {
                    pinSet(tmpPin)
                } else {
                    _need2ReenterPin.value = true
                    attempts = 0
                }
            }
        }
    }

    private fun pinSet(pin: String) {
        savePin(pin)
        _need2Navigate2Settings.value = true
    }

    private fun savePin(pin: String) {
        prefsRepo.pin = pin
    }
}

class SetPinViewModelFactory(
    private val prefsRepo: PrefsRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SetPinViewModel::class.java)) {
            return SetPinViewModel(prefsRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
