package ru.vincetti.vimoney.ui.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.vincetti.modules.core.utils.SingleLiveEvent
import ru.vincetti.vimoney.models.SplashModel
import ru.vincetti.vimoney.utils.NetworkUtils
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val splashModel: SplashModel,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _action = SingleLiveEvent<Action>()
    val action: LiveData<Action>
        get() = _action

    init {
        checkDb()
    }

    private fun checkDb() {
        viewModelScope.launch {
            val isConfigExist = splashModel.loadConfigByKey() != null

            if (isConfigExist) {
                _action.value = Action.NavigateMain
            } else {
                startLoadingConfig()
            }
        }
    }

    fun resetNetworkStatus() {
        _action.value = Action.NavigateSelf
    }

    private suspend fun startLoadingConfig() {
        if (networkUtils.isAvailable()) {
            loadJsonFromServer()
        } else {
            _action.value = Action.Error
        }
    }

    private suspend fun loadJsonFromServer() {
        try {
            splashModel.updateConfig()
            _action.value = Action.NavigateMain
        } catch (e: Exception) {
            Log.d("TAG", " load from json error ${e.message}")
            _action.value = Action.Error
        }
    }

    sealed class Action {
        object NavigateSelf : Action()
        object NavigateMain : Action()
        object Error : Action()
    }
}
