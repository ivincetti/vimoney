package ru.vincetti.vimoney.utils

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.models.AccountModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.transaction.TransactionViewModel
import java.util.*

class TransactionViewModelUtils {

    companion object {
        fun genCurrencyIdHash(t: List<AccountListModel>): HashMap<Int, String> {
            val hash = HashMap<Int, String>()
            for (o in t) {
                hash.put(o.id, o.symbol)
            }
            return hash
        }

        fun genAccountsHash(t: List<AccountModel>): HashMap<Int, String> {
            val hash = HashMap<Int, String>()
            for (o in t) {
                hash.put(o.id, o.name)
            }
            return hash
        }

        suspend fun viewModelUpdate(accountDao: AccountDao, viewModel: TransactionViewModel) {
            withContext(Dispatchers.IO) {
                val tmpAccList = accountDao.loadAllAccountsList()
                val tmpAccNotArchList = accountDao.loadNotArhiveAccountsList()
                val tmpAccListFull = accountDao.loadAllAccountsFullList()
                withContext(Dispatchers.Main) {
                    viewModel.setAccountNames(genAccountsHash(tmpAccList))
                    viewModel.setNotArchiveAccountNames(genAccountsHash(tmpAccNotArchList))
                    viewModel.setCurrencyIdSymbols(genCurrencyIdHash(tmpAccListFull))
                }
            }
        }

        fun updateTransactionsViewModel(activity: FragmentActivity, viewModel: UpdateViewModel){
            val transactionViewModel =
                    ViewModelProviders.of(activity).get(TransactionViewModel::class.java)
            viewModel.updateTransactionsViewModel(transactionViewModel)
        }

        fun updateTransactionsViewModel(activity: FragmentActivity, viewModel: UpdateAndroidViewModel){
            val transactionViewModel =
                    ViewModelProviders.of(activity).get(TransactionViewModel::class.java)
            viewModel.updateTransactionsViewModel(transactionViewModel)
        }
    }
}

abstract class UpdateViewModel : ViewModel(), UpdatableViewModel

abstract class UpdateAndroidViewModel(app: Application) : AndroidViewModel(app), UpdatableViewModel

interface  UpdatableViewModel{
    fun updateTransactionsViewModel(t: TransactionViewModel)
}
