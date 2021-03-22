package ru.vincetti.vimoney.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.core.utils.SingleLiveEvent
import ru.vincetti.vimoney.models.HomeModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val model: HomeModel
) : ViewModel() {

    val incomeSum: LiveData<Int> = model.loadIncomeActual()
    val expenseSum: LiveData<Int> = model.loadExpenseActual()

    val accounts: LiveData<List<AccountList>> = model.mainAccounts()

    val userBalance: LiveData<Int> = model.userBalance()

    private var _needNavigate2Check = SingleLiveEvent<Int>()
    val needNavigate2Check: LiveData<Int>
        get() = _needNavigate2Check

    private var _homeButtonEnabled = MutableLiveData<Boolean>()
    val homeButtonEnabled: LiveData<Boolean>
        get() = _homeButtonEnabled

    fun updateAllAccounts() {
        _homeButtonEnabled.value = false
        viewModelScope.launch {
            model.balancesUpdate()
            _homeButtonEnabled.value = true
        }
    }

    fun clickOnCheck(id: Int) {
        _needNavigate2Check.value = id
    }
}
