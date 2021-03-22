package ru.vincetti.vimoney.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.vincetti.modules.core.utils.SingleLiveEvent
import ru.vincetti.vimoney.models.DashboardModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val model: DashboardModel,
) : ViewModel() {

    val income: LiveData<Int> = model.income

    val expense: LiveData<Int> = model.expense

    val monthString: LiveData<String> = model.monthString

    val yearString: LiveData<String> = model.yearString

    val dataSet: LiveData<LinkedHashMap<String, Float>> = model.dataSet

    val isShowProgress: LiveData<Boolean> = model.isLoading

    private val _need2Navigate2Home = SingleLiveEvent<Unit>()
    val need2Navigate2Home: LiveData<Unit>
        get() = _need2Navigate2Home

    init {
        viewModelScope.launch {
            model.getGraphData()
        }
    }

    fun setMonthPrev() {
        viewModelScope.launch {
            model.setMonthPrev()
        }
    }

    fun setMonthNext() {
        viewModelScope.launch {
            model.setMonthNext()
        }
    }

    fun setYearPrev() {
        viewModelScope.launch {
            model.setYearPrev()
        }
    }

    fun setYearNext() {
        viewModelScope.launch {
            model.setYearNext()
        }
    }

    fun backButtonClicked() {
        _need2Navigate2Home.value = Unit
    }
}
