package ru.vincetti.vimoney.ui.dashboard

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.repository.TransactionRepo
import ru.vincetti.vimoney.utils.DatesFormat
import java.time.LocalDate
import java.util.*

class DashboardViewModel @ViewModelInject constructor(
    private val transRepo: TransactionRepo
) : ViewModel() {

    private var localDate = LocalDate.now()

    val income = MutableLiveData<Int>()
    val expense = MutableLiveData<Int>()

    private var _monthString = MutableLiveData<String>()
    val monthString
        get() = _monthString

    private var _yearString = MutableLiveData<String>()
    val yearString
        get() = _yearString

    private var _dataSet = MutableLiveData<LinkedHashMap<String, Float>>()
    val dataSet
        get() = _dataSet

    private var _isShowProgress = MutableLiveData<Boolean>()
    val isShowProgress
        get() = _isShowProgress

    private var _need2Navigate2Home = MutableLiveData<Boolean>()
    val need2Navigate2Home: LiveData<Boolean>
        get() = _need2Navigate2Home

    init {
        income.value = 0
        expense.value = 0
        _isShowProgress.value = true
        _need2Navigate2Home.value = false
        getGraphData()
    }

    private fun getGraphData() {
        _monthString.value = DatesFormat.getMonthName(localDate)
        _yearString.value = DatesFormat.getYear0000(localDate)
        getAmountData()
        getGraphStat()
    }

    private fun getAmountData() {
        viewModelScope.launch {
            val incomeExpense = transRepo.loadIncomeExpenseMonth(localDate)
            income.value = incomeExpense.first
            expense.value = incomeExpense.second
        }
    }

    private fun getGraphStat() {
        viewModelScope.launch {
            val stat = transRepo.loadTransactionStatMonth(localDate)

            var sum = 0f
            val entries = LinkedHashMap<String, Float>()
            entries["0"] = sum
            for (model in stat) {
                sum += model.sum
                entries[model.day] = sum
            }
            _dataSet.value = entries
            _isShowProgress.value = false
        }
    }

    fun setMonthPrev() {
        localDate = localDate.minusMonths(1)
        getGraphData()
    }

    fun setMonthNext() {
        localDate = localDate.plusMonths(1)
        getGraphData()
    }

    fun setYearPrev() {
        localDate = localDate.minusYears(1)
        getGraphData()
    }

    fun setYearNext() {
        localDate = localDate.plusYears(1)
        getGraphData()
    }

    fun backButtonClicked() {
        _need2Navigate2Home.value = true
    }

    fun navigatedBack() {
        _need2Navigate2Home.value = false
    }
}
