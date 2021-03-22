package ru.vincetti.vimoney.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.vincetti.modules.core.models.TransactionStatDay
import ru.vincetti.modules.core.utils.DatesFormat
import ru.vincetti.modules.database.repository.TransactionRepo
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

class DashboardModel @Inject constructor(
    private val transactionRepo: TransactionRepo
) {

    private var localDate = LocalDate.now()

    private var _monthString = MutableLiveData<String>()
    val monthString: LiveData<String>
        get() = _monthString

    private var _yearString = MutableLiveData<String>()
    val yearString: LiveData<String>
        get() = _yearString

    private val _income = MutableLiveData<Int>()
    val income: LiveData<Int>
        get() = _income

    private val _expense = MutableLiveData<Int>()
    val expense: LiveData<Int>
        get() = _expense

    private var _dataSet = MutableLiveData<LinkedHashMap<String, Float>>()
    val dataSet: LiveData<LinkedHashMap<String, Float>>
        get() = _dataSet

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    init {
        _income.value = 0
        _expense.value = 0
    }

    suspend fun getGraphData() {
        _isLoading.value = true
        getDateData()
        getAmountData()
        getGraphStat()
        _isLoading.value = false
    }

    private fun getDateData() {
        _monthString.value = DatesFormat.getMonthName(localDate)
        _yearString.value = DatesFormat.getYear0000(localDate)
    }

    private suspend fun getAmountData() {
        val (income, expense) = loadIncomeExpenseMonth(localDate)
        _income.value = income
        _expense.value = expense
    }

    private suspend fun getGraphStat() {
        val stat = loadTransactionStatMonth(localDate)

        var sum = 0f
        val entries = LinkedHashMap<String, Float>()
        entries["0"] = sum
        for (model in stat) {
            sum += model.sum
            entries[model.day] = sum
        }
        _dataSet.value = entries
    }

    suspend fun setMonthPrev() {
        localDate = localDate.minusMonths(1)
        getGraphData()
    }

    suspend fun setMonthNext() {
        localDate = localDate.plusMonths(1)
        getGraphData()
    }

    suspend fun setYearPrev() {
        localDate = localDate.minusYears(1)
        getGraphData()
    }

    suspend fun setYearNext() {
        localDate = localDate.plusYears(1)
        getGraphData()
    }

    private suspend fun loadIncomeExpenseMonth(localDate: LocalDate): Pair<Int, Int> {
        return transactionRepo.loadIncomeExpenseMonth(localDate)
    }

    private suspend fun loadTransactionStatMonth(localDate: LocalDate): List<TransactionStatDay> {
        return transactionRepo.loadTransactionStatMonth(localDate)
    }
}
