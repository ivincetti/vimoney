package ru.vincetti.vimoney.ui.history.filter

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.vimoney.models.FilterModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
@Suppress("TooManyFunctions")
class FilterViewModel @Inject constructor(
    private val filterModel: FilterModel
) : ViewModel() {

    private val _sum = MutableLiveData<Float>()
    val sum: LiveData<Float>
        get() = _sum

    private val _categoryId = MutableLiveData<Int>()
    val category = _categoryId.switchMap {
        liveData {
            emit(filterModel.loadCategoryById(it))
        }
    }

    private val _accountId = MutableLiveData<Int>()
    val account = _accountId.switchMap {
        liveData {
            emit(filterModel.loadAccountById(it))
        }
    }

    private val _dateTo = MutableLiveData<Date>()
    val dateTo: LiveData<Date>
        get() = _dateTo

    private val _dateFrom = MutableLiveData<Date>()
    val dateFrom: LiveData<Date>
        get() = _dateFrom

    private val _sumReset = MutableLiveData<Boolean>()
    val sumReset: LiveData<Boolean>
        get() = _sumReset

    private val _descriptionReset = MutableLiveData<Boolean>()
    val descriptionReset: LiveData<Boolean>
        get() = _descriptionReset

    private val _dateFromReset = MutableLiveData<Boolean>()
    val dateFromReset: LiveData<Boolean>
        get() = _dateFromReset

    private val _dateToReset = MutableLiveData<Boolean>()
    val dateToReset: LiveData<Boolean>
        get() = _dateToReset

    init {
        _accountId.value = Transaction.DEFAULT_ID
        _categoryId.value = Transaction.DEFAULT_CATEGORY
        _descriptionReset.value = false
    }

    fun setAccount(id: Int) {
        _accountId.value = id
    }

    fun setCategoryID(categoryID: Int) {
        _categoryId.value = categoryID
    }

    fun setSum(newSum: String) {
        if (newSum.isNotEmpty()) {
            val sumF = newSum.toFloat()
            if (sumF > 0) {
                _sum.value = sumF
            }
        }
    }

    fun setDateFrom(newDate: Date) {
        _dateFrom.value = newDate
    }

    fun setDateTo(newDate: Date) {
        _dateTo.value = newDate
    }

    fun showAccounts() {
        Log.d("Vimoney FilterViewModel", "showAccounts")
    }

    fun showCategories() {
        Log.d("Vimoney FilterViewModel", "showCategories")
    }

    fun resetAccount() {
        _accountId.value = 0
    }

    fun resetCategory() {
        _categoryId.value = 0
    }

    fun resetSum() {
        _sumReset.value = true
    }

    fun sumDeleted() {
        _sumReset.value = false
    }

    fun resetDescription() {
        _descriptionReset.value = true
    }

    fun descriptionDeleted() {
        _descriptionReset.value = false
    }

    fun resetDateFrom() {
        _dateFromReset.value = true
    }

    fun dateFromDeleted() {
        _dateFromReset.value = false
    }

    fun resetDateTo() {
        _dateToReset.value = true
    }

    fun dateToDeleted() {
        _dateToReset.value = false
    }
}
