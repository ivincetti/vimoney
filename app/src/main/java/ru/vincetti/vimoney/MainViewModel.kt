package ru.vincetti.vimoney

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.models.AccountModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import java.util.*
import kotlin.collections.set

class MainViewModel(
        private val accDao: AccountDao
) : ViewModel() {

    private var curSymbolsId = MutableLiveData<HashMap<Int, String>>()
    private var accountNames = MutableLiveData<HashMap<Int, String>>()
    var accountNotArchiveNames = MutableLiveData<HashMap<Int, String>>()

    init {
        viewModelScope.launch {
            viewModelUpdate(accDao)
        }
    }

    private fun genCurrencyIdHash(t: List<AccountListModel>): HashMap<Int, String> {
        val hash = HashMap<Int, String>()
        for (o in t) hash[o.id] = o.curSymbol
        return hash
    }

    private fun genAccountsHash(list: List<AccountModel>): HashMap<Int, String> {
        val hash = HashMap<Int, String>()
        for (o in list) {
            o.name?.let {
                hash[o.id] = it
            }
        }
        return hash
    }

    private suspend fun viewModelUpdate(accountDao: AccountDao) {
        withContext(Dispatchers.IO) {
            val tmpAccList = accountDao.loadAllAccountsList()
            val tmpAccNotArchList = accountDao.loadNotArchiveAccountsList()
            val tmpAccListFull = accountDao.loadAllAccountsFullList()
            withContext(Dispatchers.Main) {
                accountNames.value = genAccountsHash(tmpAccList)
                accountNotArchiveNames.value = genAccountsHash(tmpAccNotArchList)
                curSymbolsId.value = genCurrencyIdHash(tmpAccListFull)
            }
        }
    }

    fun loadFromCurSymbols(id: Int) = curSymbolsId.value?.get(id)
    fun loadFromAccountNames(id: Int) = accountNames.value?.get(id)
    fun loadFromAccountNotArchiveNames(id: Int) = accountNotArchiveNames.value?.get(id)
}

class MainViewModelFactory(
        private val accDao: AccountDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(accDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
