package ru.vincetti.vimoney.ui.check.add

import android.graphics.Color
import android.text.TextUtils
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.core.models.Currency
import ru.vincetti.modules.core.utils.SingleLiveEvent
import ru.vincetti.vimoney.models.AddCheckModel
import ru.vincetti.vimoney.ui.check.DEFAULT_CHECK_ID
import javax.inject.Inject

@HiltViewModel
@Suppress("TooManyFunctions")
class AddCheckViewModel @Inject constructor(
    private val addCheckModel: AddCheckModel
) : ViewModel() {

    private var checkID = DEFAULT_CHECK_ID

    private var _check = MutableLiveData<Account>()
    val check: LiveData<Account>
        get() = _check

    private val _isDefault = MutableLiveData<Boolean>()
    val isDefault: LiveData<Boolean>
        get() = _isDefault

    private var isDefaultBool = true

    private val _need2NavigateBack = SingleLiveEvent<Unit>()
    val need2NavigateBack: LiveData<Unit>
        get() = _need2NavigateBack

    private val _need2AllData = MutableLiveData<Unit>()
    val need2AllData: LiveData<Unit>
        get() = _need2AllData

    private var _needAllBalance = MutableLiveData<Boolean>()
    val needAllBalance: LiveData<Boolean>
        get() = _needAllBalance

    private var _needOnMain = MutableLiveData<Boolean>()
    val needOnMain: LiveData<Boolean>
        get() = _needOnMain

    private var _currency = MutableLiveData<Currency>()
    val currency: LiveData<Currency>
        get() = _currency

    private var _color = MutableLiveData<Int>()
    val color: LiveData<Int>
        get() = _color

    val currencyList: LiveData<List<Currency>> = liveData {
        emit(addCheckModel.getAllCurrency())
    }

    init {
        _isDefault.value = true
        _needOnMain.value = true
        _needAllBalance.value = true
        _check.value = Account().also {
            _color.value = Color.parseColor(it.color)
        }
    }

    fun loadAccount(id: Int) {
        if (id > 0) {
            viewModelScope.launch {
                addCheckModel.loadAccountById(id)?.let {
                    checkID = id
                    _color.value = Color.parseColor(it.color)
                    _check.value = it
                    // TODO доделать
                    _currency.value = addCheckModel.loadCurrencyByCode(it.currency)
                    _needAllBalance.value = it.needAllBalance
                    _needOnMain.value = it.needOnMain
                    _isDefault.value = false
                    isDefaultBool = false
                }
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
            _need2AllData.value = Unit
        } else {
            val tmpAcc = check.value
            tmpAcc?.let {
                it.name = name
                it.type = type
                it.needAllBalance = _needAllBalance.value!!
                it.needOnMain = _needOnMain.value!!
                if (!isDefaultBool) {
                    viewModelScope.launch {
                        addCheckModel.updateAccount(it)
                    }
                } else {
                    it.sum = 0
                    it.color = java.lang.String.format("#%06x", (_color.value!! and 0xffffff))
                    viewModelScope.launch {
                        addCheckModel.addAccount(it)
                    }
                }
                need2NavigateBack()
            }
        }
    }

    fun restore() {
        if (!isDefaultBool) {
            viewModelScope.launch {
                addCheckModel.unArchiveAccountById(checkID)
                need2NavigateBack()
            }
        }
    }

    fun delete() {
        if (!isDefaultBool) {
            viewModelScope.launch {
                addCheckModel.archiveAccountById(checkID)
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

    fun need2NavigateBack() {
        _need2NavigateBack.value = Unit
    }

    fun setCurrency(checkCurrency: Int) {
        _check.value?.currency = checkCurrency
        viewModelScope.launch {
            _currency.value = addCheckModel.loadCurrencyByCode(checkCurrency)
        }
    }

    fun setBackgroundColor(selectedColor: Int) {
        _color.value = selectedColor
        val hexColor = java.lang.String.format("#%06x", (selectedColor and 0xffffff))
        _check.value?.color = hexColor
    }
}
