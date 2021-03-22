package ru.vincetti.vimoney.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vincetti.modules.core.utils.SingleLiveEvent
import ru.vincetti.vimoney.models.SettingsModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsModel: SettingsModel
) : ViewModel() {

    private val _need2Navigate2Home = SingleLiveEvent<Unit>()
    val need2Navigate2Home: LiveData<Unit>
        get() = _need2Navigate2Home

    private val _need2Navigate2Categories = SingleLiveEvent<Unit>()
    val need2Navigate2Categories: LiveData<Unit>
        get() = _need2Navigate2Categories

    private var _buttonsState = MutableLiveData<Boolean>()
    val buttonsState: LiveData<Boolean>
        get() = _buttonsState

    init {
        _buttonsState.value = true
    }

    fun backButtonClicked() {
        _need2Navigate2Home.value = Unit
    }

    fun categoriesButtonClicked() {
        _need2Navigate2Categories.value = Unit
    }

    fun saveJson() {
        _buttonsState.value = false
        viewModelScope.launch {
            settingsModel.export()
            withContext(Dispatchers.Main) {
                _buttonsState.value = true
            }
        }
    }

    fun loadJson() {
        _buttonsState.value = false
        viewModelScope.launch {
            settingsModel.import()
            withContext(Dispatchers.Main) {
                _buttonsState.value = true
            }
        }
    }
}
