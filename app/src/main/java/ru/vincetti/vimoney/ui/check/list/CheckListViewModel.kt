package ru.vincetti.vimoney.ui.check.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.ui.dashboard.DashboardViewModel

class CheckListViewModel(dao: AccountDao) : ViewModel() {
    val accList: LiveData<List<AccountListModel>> = dao.loadAllAccountsFull()
}


class CheckListModelFactory(private val dao: AccountDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckListViewModel::class.java)) {
            return CheckListViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}