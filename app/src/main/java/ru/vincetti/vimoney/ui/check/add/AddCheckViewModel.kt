package ru.vincetti.vimoney.ui.check.add

import android.graphics.Color
import android.text.TextUtils
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.models.AccountModel
import ru.vincetti.vimoney.data.models.CurrencyModel
import ru.vincetti.vimoney.data.repository.AccountRepo
import ru.vincetti.vimoney.data.repository.CurrencyRepo
import ru.vincetti.vimoney.ui.check.DEFAULT_CHECK_ID
import ru.vincetti.vimoney.utils.SingleLiveEvent

@Suppress("TooManyFunctions")
class AddCheckViewModel @ViewModelInject constructor(
    private val accountRepo: AccountRepo,
    private val currencyRepo: CurrencyRepo
) : ViewModel() {

    private var checkID = DEFAULT_CHECK_ID

    val isDefault = MutableLiveData<Boolean>()
    private var isDefaultBool = true

    val need2NavigateBack = SingleLiveEvent<Boolean>()

    private var _needAllBalance = MutableLiveData<Boolean>()
    val needAllBalance: LiveData<Boolean>
        get() = _needAllBalance

    private var _needOnMain = MutableLiveData<Boolean>()
    val needOnMain: LiveData<Boolean>
        get() = _needOnMain

    var need2AllData = MutableLiveData<Boolean>()

    private var _currency = MutableLiveData<CurrencyModel>()
    val currency
        get() = _currency

    private var _color = MutableLiveData<Int>()
    val color
        get() = _color

    val currencyList = liveData {
        emit(currencyRepo.loadAll())
    }

    private var _check = MutableLiveData<AccountModel>()
    val check: LiveData<AccountModel>
        get() = _check

    init {
        isDefault.value = true
        need2AllData.value = false
        _needOnMain.value = true
        _needAllBalance.value = true
        _check.value = AccountModel()
        _color.value = Color.parseColor(_check.value!!.color)
    }

    fun loadAccount(id: Int) {
        viewModelScope.launch {
            accountRepo.loadById(id)?.let {
                checkID = id
                _color.value = Color.parseColor(it.color)
                _check.value = it
                _currency.value = currencyRepo.loadByCode(it.currency)
                _needAllBalance.value = it.needAllBalance
                _needOnMain.value = it.needOnMain
                isDefault.value = false
                isDefaultBool = false
            }
        }
    }

    fun save(name: String, type: String) {
        if (
            TextUtils.isEmpty(name) ||
            TextUtils.isEmpty(type) ||
            currency.value == null ||
            color.value!! > 0
        ) {
            need2AllData.value = true
        } else {
            val tmpAcc = check.value
            tmpAcc?.let {
                it.name = name
                it.type = type
                it.needAllBalance = _needAllBalance.value!!
                it.needOnMain = _needOnMain.value!!
                if (!isDefaultBool) {
                    viewModelScope.launch {
                        accountRepo.update(it)
                    }
                } else {
                    it.sum = 0
                    it.color = java.lang.String.format("#%06x", (_color.value!! and 0xffffff))
                    viewModelScope.launch {
                        accountRepo.add(it)
                    }
                }
                need2NavigateBack()
            }
        }
    }

    fun restore() {
        if (!isDefaultBool) {
            viewModelScope.launch {
                accountRepo.unArchiveById(checkID)
                need2NavigateBack()
            }
        }
    }

    fun delete() {
        if (!isDefaultBool) {
            viewModelScope.launch {
                accountRepo.archiveById(checkID)
                need2NavigateBack()
            }
        }
    }

    fun setNeedAllBalance(isChecked: Boolean) {
        _needAllBalance.value = isChecked
    }

    fun setNeedOnMain(isChecked: Boolean) {
        _needOnMain.value = isChecked
    }

    fun noDataDialogClosed() {
        need2AllData.value = false
    }

    fun need2NavigateBack() {
        need2NavigateBack.value = true
    }

    fun setCurrency(checkCurrency: Int) {
        _check.value?.currency = checkCurrency
        viewModelScope.launch {
            _currency.value = currencyRepo.loadByCode(checkCurrency)
        }
    }

    fun setBackgroundColor(selectedColor: Int) {
        _color.value = selectedColor
        val hexColor = java.lang.String.format("#%06x", (selectedColor and 0xffffff))
        _check.value?.color = hexColor
    }
}
