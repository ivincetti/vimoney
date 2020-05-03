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

    private var _importButtonState = MutableLiveData<Boolean>()
    val importButtonState: LiveData<Boolean>
        get() = _importButtonState

    private var _exportButtonState = MutableLiveData<Boolean>()
    val exportButtonState: LiveData<Boolean>
        get() = _exportButtonState

    init {
        _need2Navigate2Home.value = false
        _importButtonState.value = true
        _exportButtonState.value = true
    }

    fun homeButton() {
        _need2Navigate2Home.value = true
    }

    fun saveJson() {
        App.context?.let {
            viewModelScope.launch {
                JsonFile.save(it)
                withContext(Dispatchers.Main) {
                    setImportButtonState(true)
                }
            }
        }
    }

    fun loadJson() {
        val context = App.context
        context?.let {
            viewModelScope.launch {
                JsonFile.load(it)
                withContext(Dispatchers.Main) {
                    setExportButtonState(true)
                }
            }
        }
    }

    private fun setImportButtonState(state: Boolean) {
        _importButtonState.value = state
    }

    private fun setExportButtonState(state: Boolean) {
        _exportButtonState.value = state
    }

}
