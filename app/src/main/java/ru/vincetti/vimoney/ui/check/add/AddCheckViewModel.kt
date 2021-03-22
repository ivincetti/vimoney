package ru.vincetti.vimoney.ui.check.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.vincetti.modules.core.models.Currency
import ru.vincetti.modules.core.utils.SingleLiveEvent
import ru.vincetti.vimoney.models.AddCheckModel
import javax.inject.Inject

@HiltViewModel
class AddCheckViewModel @Inject constructor(
    private val addCheckModel: AddCheckModel,
) : ViewModel() {

    val isDefault: LiveData<Boolean> = addCheckModel.isDefault

    val name: LiveData<String> = addCheckModel.name

    val type: LiveData<String> = addCheckModel.type

    val isArchive: LiveData<Boolean> = addCheckModel.isArchive

    val needAllBalance: LiveData<Boolean> = addCheckModel.needAllBalance

    val needOnMain: LiveData<Boolean> = addCheckModel.needOnMain

    val currency: LiveData<String> = addCheckModel.currency

    val color: LiveData<Int> = addCheckModel.color

    private val _need2AllData = SingleLiveEvent<Unit>()
    val need2AllData: LiveData<Unit>
        get() = _need2AllData

    private val _need2NavigateBack = SingleLiveEvent<Unit>()
    val need2NavigateBack: LiveData<Unit>
        get() = _need2NavigateBack

    val currencyList: LiveData<List<Currency>> = liveData {
        emit(addCheckModel.getAllCurrency())
    }

    fun loadAccount(id: Int) {
        viewModelScope.launch {
            addCheckModel.loadAccountById(id)
        }
    }

    fun save(name: String, type: String) {
        if (name.isEmpty() || type.isEmpty()) {
            _need2AllData.value = Unit
        } else {
            viewModelScope.launch {
                addCheckModel.save(name, type)
                need2NavigateBack()
            }
        }
    }

    fun restore() {
        viewModelScope.launch {
            addCheckModel.restore()
            need2NavigateBack()
        }
    }

    fun delete() {
        viewModelScope.launch {
            addCheckModel.delete()
            need2NavigateBack()
        }
    }

    fun setNeedAllBalance(isChecked: Boolean) {
        addCheckModel.setNeedAllBalance(isChecked)
    }

    fun setNeedOnMain(isChecked: Boolean) {
        addCheckModel.setNeedOnMain(isChecked)
    }

    fun need2NavigateBack() {
        _need2NavigateBack.value = Unit
    }

    fun setCurrency(checkCurrency: Int) {
        addCheckModel.setCurrency(checkCurrency)
    }

    fun setBackgroundColor(selectedColor: Int) {
        addCheckModel.setBackgroundColor(selectedColor)
    }
}
