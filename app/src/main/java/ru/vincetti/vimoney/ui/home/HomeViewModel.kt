package ru.vincetti.vimoney.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.repository.AccountRepo
import ru.vincetti.vimoney.data.repository.TransactionRepo

class HomeViewModel @ViewModelInject constructor(
    private val accountRepo: AccountRepo,
    private val transRepo: TransactionRepo
) : ViewModel() {

    val expenseSum = MutableLiveData<Int>()
    val incomeSum = MutableLiveData<Int>()

    val accounts = accountRepo.loadMain()

    private var _homeButtonEnabled = MutableLiveData<Boolean>()
    val homeButtonEnabled: LiveData<Boolean>
        get() = _homeButtonEnabled

    val userBalance: LiveData<Int> = accounts.switchMap {
        liveData {
            emit(accountRepo.balanceUpdate())
        }
    }

    init {
        _homeButtonEnabled.value = true
        viewModelScope.launch {
            val incomeExpense = transRepo.loadIncomeExpenseActual()
            incomeSum.value = incomeExpense.first
            expenseSum.value = incomeExpense.second
        }
    }

    fun updateAllAccounts() {
        _homeButtonEnabled.value = false
        viewModelScope.launch {
            accountRepo.balanceUpdateAll()
            _homeButtonEnabled.value = true
        }
    }
}
