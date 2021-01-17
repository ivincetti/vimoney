package ru.vincetti.vimoney.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.settings.json.JsonFile
import ru.vincetti.vimoney.utils.SingleLiveEvent

class SettingsViewModel @ViewModelInject constructor(
    private val jsonFile: JsonFile
) : ViewModel() {

    val need2Navigate2Home = SingleLiveEvent<Boolean>()

    val need2Navigate2Categories = SingleLiveEvent<Boolean>()

    private var _buttonsState = MutableLiveData<Boolean>()
    val buttonsState: LiveData<Boolean>
        get() = _buttonsState

    init {
        _buttonsState.value = true
    }

    fun backButtonClicked() {
        need2Navigate2Home.value = true
    }

    fun categoriesButtonClicked() {
        need2Navigate2Categories.value = true
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
