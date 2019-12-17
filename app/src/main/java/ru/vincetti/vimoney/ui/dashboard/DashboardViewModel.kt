package ru.vincetti.vimoney.ui.dashboard

import androidx.lifecycle.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.sqlite.TransactionDao
import java.text.SimpleDateFormat
import java.util.*

class DashboardViewModel(private val dao: TransactionDao) : ViewModel() {

    private val cal: Calendar = Calendar.getInstance()

    var income: LiveData<Int> =
            dao.loadSumTransactionIncomeMonth(SimpleDateFormat("MM")
                    .format(cal.time), "2019")

    val expense = dao
            .loadSumTransactionExpenseMonth(SimpleDateFormat("MM")
                    .format(cal.time), "2019")

    private var _monthString = MutableLiveData<String>()
    val monthString
        get() = _monthString

    private var _dataSet = MutableLiveData<LineData>()
    val dataSet
        get() = _dataSet

    private var _isShowProgress = MutableLiveData<Boolean>()
    val isShowProgress
        get() = _isShowProgress

    init {
        cal.time = Date()
        _isShowProgress.value = true
        getData()
    }

    private fun getData() {
        monthString.value = SimpleDateFormat("MMM").format(cal.time)
        getStat()
    }

    /**
     * получение статистики
     */
    private fun getStat() {
        viewModelScope.launch {
            val stat = dao
                    .loadTransactionStatByMonth(SimpleDateFormat("MM")
                            .format(cal.time), "2019")
            var sum = 0f
            val entries = ArrayList<Entry>()
            entries.add(Entry(0f, sum))
            for (model in stat) {
                sum += model.sum
                entries.add(Entry(model.day.toFloat(), sum))
            }
            val dataSet = LineDataSet(entries, "Label")
            dataSet.apply {
                setDrawFilled(true)
                setDrawCircles(false)
                lineWidth = 1.8f
                circleRadius = 4f
                fillAlpha = 100
                setDrawHorizontalHighlightIndicator(false)
            }
            // create a data object with the data sets
            val lineData = LineData(dataSet)
            _isShowProgress.value = false
            _dataSet.value = lineData
        }
    }

    fun setMonthPrev() {
        cal.add(Calendar.MONTH, -1)
        getData()
    }

    fun setMonthNext() {
        cal.add(Calendar.MONTH, 1)
        getData()
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