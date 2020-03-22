package ru.vincetti.vimoney.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.App
import ru.vincetti.vimoney.settings.json.JsonFile
import ru.vincetti.vimoney.utils.showNotification

class SettingsViewModel : ViewModel() {

    private var _need2Navigate2Home = MutableLiveData<Boolean>()
    val need2Navigate2Home: LiveData<Boolean>
        get() = _need2Navigate2Home

    init {
        _need2Navigate2Home.value = false
    }

    fun homeButton() {
        _need2Navigate2Home.value = true
    }

    fun saveJson() {
        val context = App.context
        context?.let {
            JsonFile.save(it)
        }
    }

    fun loadJson() {
        val context = App.context
        context?.let {
            viewModelScope.launch {
                JsonFile.load(it)
            }
            showNotification(
                    context,
                    "Import done",
                    "all transactions successfully imported"
            )
        }
    }
}