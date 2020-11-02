package ru.vincetti.vimoney.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.vincetti.vimoney.data.repository.PrefsRepo

class LoginViewModel(private val prefsRepo: PrefsRepo) : ViewModel() {

    private var _need2Navigate2Home = MutableLiveData<Boolean>()
    val need2Navigate2Home: LiveData<Boolean>
        get() = _need2Navigate2Home

    private var _need2ReenterPin = MutableLiveData<Boolean>()
    val need2ReenterPin: LiveData<Boolean>
        get() = _need2ReenterPin

    init {
        _need2Navigate2Home.value = !prefsRepo.isPinSet
        _need2ReenterPin.value = false
    }

    fun navigated2Home() {
        _need2Navigate2Home.value = false
    }

    fun need2ReenterPinProcessed() {
        _need2ReenterPin.value = false
    }

    fun checkPin(pin2Check: String) {
        if (prefsRepo.pin == pin2Check) {
            _need2Navigate2Home.value = true
        } else {
            _need2ReenterPin.value = true
        }
    }
}

class LoginViewModelFactory(
    private val prefsRepo: PrefsRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(prefsRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
