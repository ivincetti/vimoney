package ru.vincetti.vimoney.ui.check.list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import ru.vincetti.vimoney.data.repository.AccountRepo

class CheckListViewModel @ViewModelInject constructor(
    accountRepo: AccountRepo
) : ViewModel() {

    val accList = accountRepo.loadAllFull()
}
