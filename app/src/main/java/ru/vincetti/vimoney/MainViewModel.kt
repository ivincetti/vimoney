package ru.vincetti.vimoney

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.models.AccountModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import java.util.*

class MainViewModel(
        val app: Application
) : AndroidViewModel(app) {

    private val accDao: AccountDao = AppDatabase.getInstance(app).accountDao()
    var curSymbolsId = MutableLiveData<HashMap<Int, String>>()
    var accountNames = MutableLiveData<HashMap<Int, String>>()
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

    private fun genAccountsHash(t: List<AccountModel>): HashMap<Int, String> {
        val hash = HashMap<Int, String>()
        // TODO почему здесь ругается
        for (o in t) hash[o.id!!] = o.name
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

