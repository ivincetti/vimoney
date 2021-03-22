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

    private val _networkError = SingleLiveEvent<Unit>()
    val networkError: LiveData<Unit>
        get() = _networkError

    private val _need2Navigate2Home = SingleLiveEvent<Unit>()
    val need2Navigate2Home: LiveData<Unit>
        get() = _need2Navigate2Home

    private val _need2Navigate2Self = SingleLiveEvent<Unit>()
    val need2Navigate2Self: LiveData<Unit>
        get() = _need2Navigate2Self

    init {
        checkDb()
    }

    private fun checkDb() {
        viewModelScope.launch {
            splashModel.loadConfigByKey()?.let {
                _need2Navigate2Home.value = Unit
            } ?: run {
                if (networkUtils.isAvailable()) {
                    loadJsonFromServer()
                } else {
                    _networkError.value = Unit
                }
            }
        }
    }

    fun resetNetworkStatus() {
        _need2Navigate2Self.value = Unit
    }

    private fun loadJsonFromServer() {
        viewModelScope.launch {
            try {
                splashModel.updateConfig()
                _need2Navigate2Home.value = Unit
            } catch (e: Exception) {
                Log.d("TAG", " load from json error ${e.message}")
                _networkError.value = Unit
            }
        }
    }
}
