package ru.vincetti.vimoney.ui.history.filter

import android.util.Log
import androidx.lifecycle.*
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.repository.AccountRepo
import ru.vincetti.vimoney.data.repository.CategoryRepo
import java.util.*

@Suppress("TooManyFunctions")
class FilterViewModel(
    private val accountRepo: AccountRepo,
    private val categoryRepo: CategoryRepo
) : ViewModel() {

    private val _needToNavigate = MutableLiveData<Boolean>()
    val needToNavigate
        get() = _needToNavigate

    private val _sum = MutableLiveData<Float>()
    val sum
        get() = _sum

    private val _categoryId = MutableLiveData<Int>()
    val category = _categoryId.switchMap {
        liveData {
            emit(categoryRepo.loadById(it))
        }
    }

    private val _accountId = MutableLiveData<Int>()
    val account = _accountId.switchMap {
        liveData {
            emit(accountRepo.loadById(it))
        }
    }

    private val _dateTo = MutableLiveData<Date>()
    val dateTo
        get() = _dateTo

    private val _dateFrom = MutableLiveData<Date>()
    val dateFrom
        get() = _dateFrom

    private val _sumReset = MutableLiveData<Boolean>()
    val sumReset
        get() = _sumReset

    private val _descriptionReset = MutableLiveData<Boolean>()
    val descriptionReset
        get() = _descriptionReset

    private val _dateFromReset = MutableLiveData<Boolean>()
    val dateFromReset
        get() = _dateFromReset

    private val _dateToReset = MutableLiveData<Boolean>()
    val dateToReset
        get() = _dateToReset

    init {
        _accountId.value = TransactionModel.DEFAULT_ID
        _categoryId.value = TransactionModel.DEFAULT_CATEGORY
        _needToNavigate.value = false
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

class FilterViewModelFactory(
    private val accountRepo: AccountRepo,
    private val categoryRepo: CategoryRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilterViewModel::class.java)) {
            return FilterViewModel(accountRepo, categoryRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
