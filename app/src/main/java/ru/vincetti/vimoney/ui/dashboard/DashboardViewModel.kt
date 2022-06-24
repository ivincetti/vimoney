package ru.vincetti.vimoney.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.vincetti.modules.core.utils.DatesFormat
import ru.vincetti.modules.core.utils.SingleLiveEvent
import ru.vincetti.modules.database.repository.TransactionRepo
import java.time.LocalDate
import javax.inject.Inject
import kotlin.collections.set

@HiltViewModel
class DashboardViewModel @Inject constructor(
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

    val need2Navigate2Home = SingleLiveEvent<Boolean>()

    init {
        income.value = 0
        expense.value = 0
        _isShowProgress.value = true
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
        need2Navigate2Home.value = true
    }
}
