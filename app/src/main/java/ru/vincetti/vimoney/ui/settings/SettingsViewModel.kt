package ru.vincetti.vimoney.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.App
import ru.vincetti.vimoney.settings.json.JsonFile

class SettingsViewModel : ViewModel() {

    private var _need2Navigate2Home = MutableLiveData<Boolean>()
    val need2Navigate2Home: LiveData<Boolean>
        get() = _need2Navigate2Home

    private var _need2Navigate2Categories = MutableLiveData<Boolean>()
    val need2Navigate2Categories: LiveData<Boolean>
        get() = _need2Navigate2Categories

    private var _importButtonState = MutableLiveData<Boolean>()
    val importButtonState: LiveData<Boolean>
        get() = _importButtonState

    private var _exportButtonState = MutableLiveData<Boolean>()
    val exportButtonState: LiveData<Boolean>
        get() = _exportButtonState

    init {
        _need2Navigate2Home.value = false
        _need2Navigate2Categories.value = false
        _importButtonState.value = true
        _exportButtonState.value = true
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
}
