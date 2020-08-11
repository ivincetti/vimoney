package ru.vincetti.vimoney.ui.check.add

import android.app.Application
import android.graphics.Color
import android.text.TextUtils
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.models.AccountModel
import ru.vincetti.vimoney.data.models.CurrencyModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.data.sqlite.CurrentDao
import ru.vincetti.vimoney.ui.check.DEFAULT_CHECK_ID

class AddCheckViewModel(
    private val accDao: AccountDao,
    private val curDao: CurrentDao,
    val app: Application
) : AndroidViewModel(app) {

    private var checkID = DEFAULT_CHECK_ID

    val isDefault = MutableLiveData<Boolean>()
    private var isDefaultBool = true

    private var _need2Navigate = MutableLiveData<Boolean>()
    val need2Navigate: LiveData<Boolean>
        get() = _need2Navigate

    private var _needAllBalance = MutableLiveData<Boolean>()
    val needAllBalance: LiveData<Boolean>
        get() = _needAllBalance

    var need2AllData = MutableLiveData<Boolean>()

    private var _currency = MutableLiveData<CurrencyModel>()
    val currency
        get() = _currency

    private var _color = MutableLiveData<Int>()
    val color
        get() = _color

    val currencyList = curDao.loadAllCurrency()

    private var _check = MutableLiveData<AccountModel>()
    val check: LiveData<AccountModel>
        get() = _check

    init {
        isDefault.value = true
        need2AllData.value = false
        _need2Navigate.value = false
        _needAllBalance.value = true
        _check.value = AccountModel()
        _color.value = Color.parseColor(_check.value!!.color)
    }

    fun loadAccount(id: Int) {
        viewModelScope.launch {
            accDao.loadAccountById(id)?.let {
                checkID = id
                _color.value = Color.parseColor(it.color)
                _check.value = it
                _currency.value = curDao.loadCurrencyByCode(it.currency)
                _needAllBalance.value = it.needAllBalance
                isDefault.value = false
                isDefaultBool = false
            }
        }
    }

    /** Save account logic. */
    fun save(name: String, type: String) {
        if (
            TextUtils.isEmpty(name)
            || TextUtils.isEmpty(type)
            || currency.value == null
            || color.value!! > 0
        ) {
            need2AllData.value = true
        } else {
            val tmpAcc = check.value
            tmpAcc?.let {
                it.name = name
                it.type = type
                it.needAllBalance = _needAllBalance.value!!
                if (!isDefaultBool) {
                    // update logic
                    viewModelScope.launch {
                        accDao.updateAccount(it)
                    }
                } else {
                    // new transaction
                    it.sum = 0
                    it.color = java.lang.String.format("#%06x", (_color.value!! and 0xffffff))
                    viewModelScope.launch {
                        accDao.insertAccount(it)
                    }
                }
                _need2Navigate.value = true
            }
        }
    }

    /** Restore from archive account logic. */
    fun restore() {
        if (!isDefaultBool) {
            viewModelScope.launch {
                accDao.fromArchiveAccountById(checkID)
                _need2Navigate.value = true
            }
        }
    }

    /** Archive account logic. */
    fun delete() {
        if (!isDefaultBool) {
            viewModelScope.launch {
                accDao.archiveAccountById(checkID)
                _need2Navigate.value = true
            }
        }
    }

    fun setNeed2AllData(isChecked: Boolean) {
        _needAllBalance.value = isChecked
    }

    fun setCurrency(checkCurrency: Int) {
        _check.value?.currency = checkCurrency
        viewModelScope.launch {
            _currency.value = curDao.loadCurrencyByCode(checkCurrency)
        }
    }

    fun setBackgroundColor(selectedColor: Int) {
        _color.value = selectedColor
        val hexColor = java.lang.String.format("#%06x", (selectedColor and 0xffffff))
        _check.value?.color = hexColor
    }
}

class AddCheckModelFactory(
    private val accDao: AccountDao,
    private val curDao: CurrentDao,
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCheckViewModel::class.java)) {
            return AddCheckViewModel(accDao, curDao, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
