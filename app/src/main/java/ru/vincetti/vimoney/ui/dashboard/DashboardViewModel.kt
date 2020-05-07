package ru.vincetti.vimoney.ui.dashboard

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.sqlite.TransactionDao
import java.text.SimpleDateFormat
import java.util.*

class DashboardViewModel(private val dao: TransactionDao) : ViewModel() {

    private val cal: Calendar = Calendar.getInstance()

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
        cal.time = Date()
        income.value = 0
        expense.value = 0
        _isShowProgress.value = true
        _need2Navigate2Home.value = false
        getGraphData()
    }

    private fun getGraphData() {
        _monthString.value = SimpleDateFormat("MMM").format(cal.time)
        _yearString.value = SimpleDateFormat("yyyy").format(cal.time)
        getAmountData()
        getGraphStat()
    }

    private fun getAmountData() {
        viewModelScope.launch {
            income.value = dao.loadSumTransactionIncomeMonth(
                    SimpleDateFormat("MM").format(cal.time),
                    SimpleDateFormat("yyyy").format(cal.time)
            )
            expense.value = dao.loadSumTransactionExpenseMonth(
                    SimpleDateFormat("MM").format(cal.time),
                    SimpleDateFormat("yyyy").format(cal.time)
            )
        }
    }

    /** Получение статистики. */
    private fun getGraphStat() {
        viewModelScope.launch {
            val stat = dao.loadTransactionStatByMonth(
                    SimpleDateFormat("MM").format(cal.time),
                    SimpleDateFormat("yyyy").format(cal.time)
            )
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
        cal.add(Calendar.MONTH, -1)
        getGraphData()
    }

    fun setMonthNext() {
        cal.add(Calendar.MONTH, 1)
        getGraphData()
    }

    fun setYearPrev() {
        cal.add(Calendar.YEAR, -1)
        getGraphData()
    }

    fun setYearNext() {
        cal.add(Calendar.YEAR, 1)
        getGraphData()
    }

    fun homeButton() {
        _need2Navigate2Home.value = true
    }
}

class DashboardViewModelFactory(private val dao: TransactionDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
