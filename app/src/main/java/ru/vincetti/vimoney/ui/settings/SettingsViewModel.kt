package ru.vincetti.vimoney.ui.settings

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.App
import ru.vincetti.vimoney.data.repository.PrefsRepo
import ru.vincetti.vimoney.settings.json.JsonFile

class SettingsViewModel(private val prefsRepo: PrefsRepo) : ViewModel() {

    private var _need2Navigate2Home = MutableLiveData<Boolean>()
    val need2Navigate2Home: LiveData<Boolean>
        get() = _need2Navigate2Home

    private var _need2Navigate2Categories = MutableLiveData<Boolean>()
    val need2Navigate2Categories: LiveData<Boolean>
        get() = _need2Navigate2Categories

    private var _need2Navigate2Pin = MutableLiveData<Boolean>()
    val need2Navigate2Pin: LiveData<Boolean>
        get() = _need2Navigate2Pin

    private var _pinSwitchState = MutableLiveData<Boolean>()
    val pinSwitchState: LiveData<Boolean>
        get() = _pinSwitchState

    private var _importButtonState = MutableLiveData<Boolean>()
    val importButtonState: LiveData<Boolean>
        get() = _importButtonState

    private var _exportButtonState = MutableLiveData<Boolean>()
    val exportButtonState: LiveData<Boolean>
        get() = _exportButtonState

    init {
        _need2Navigate2Home.value = false
        _need2Navigate2Categories.value = false
        _need2Navigate2Pin.value = false
        _importButtonState.value = true
        _exportButtonState.value = true
        _pinSwitchState.value = false
    }

    fun backButtonClicked() {
        _need2Navigate2Home.value = true
    }

    fun navigated2Home() {
        _need2Navigate2Home.value = false
    }

    fun categoriesButtonClicked() {
        _need2Navigate2Categories.value = true
    }

    fun navigated2Categories() {
        _need2Navigate2Categories.value = false
    }

    fun securitySwitchClicked() {
        if (prefsRepo.isPinSet) {
            prefsRepo.resetPin()
        } else {
            _need2Navigate2Pin.value = true
        }
    }

    fun navigated2Pin() {
        _need2Navigate2Pin.value = false
    }

    fun saveJson() {
        _exportButtonState.value = false
        App.context?.let {
            viewModelScope.launch {
                JsonFile.save(it)
                withContext(Dispatchers.Main) {
                    _exportButtonState.value = true
                }
            }
        }
    }

    fun loadJson() {
        _importButtonState.value = false
        val context = App.context
        context?.let {
            viewModelScope.launch {
                JsonFile.load(it)
                withContext(Dispatchers.Main) {
                    _importButtonState.value = true
                }
            }
        }
    }

    fun checkNonObservablesState() {
        _pinSwitchState.value = prefsRepo.isPinSet
    }
}

class SettingsViewModelFactory(
    private val prefsRepo: PrefsRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(prefsRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
