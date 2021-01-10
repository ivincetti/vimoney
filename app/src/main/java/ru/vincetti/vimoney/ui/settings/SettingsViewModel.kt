package ru.vincetti.vimoney.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.settings.json.JsonFile

class SettingsViewModel @ViewModelInject constructor(
    private val jsonFile: JsonFile
) : ViewModel() {

    private var _need2Navigate2Home = MutableLiveData<Boolean>()
    val need2Navigate2Home: LiveData<Boolean>
        get() = _need2Navigate2Home

    private var _need2Navigate2Categories = MutableLiveData<Boolean>()
    val need2Navigate2Categories: LiveData<Boolean>
        get() = _need2Navigate2Categories

    private var _buttonsState = MutableLiveData<Boolean>()
    val buttonsState: LiveData<Boolean>
        get() = _buttonsState

    init {
        _need2Navigate2Home.value = false
        _need2Navigate2Categories.value = false
        _buttonsState.value = true
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

    fun saveJson() {
        _buttonsState.value = false
        viewModelScope.launch {
            jsonFile.save()
            withContext(Dispatchers.Main) {
                _buttonsState.value = true
            }
        }
    }

    fun loadJson() {
        _buttonsState.value = false
        viewModelScope.launch {
            jsonFile.load()
            withContext(Dispatchers.Main) {
                _buttonsState.value = true
            }
        }
    }
}
