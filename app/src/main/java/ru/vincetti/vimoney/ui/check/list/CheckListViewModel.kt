package ru.vincetti.vimoney.ui.check.list

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.vincetti.vimoney.data.repository.AccountRepo
import javax.inject.Inject

@HiltViewModel
class CheckListViewModel @Inject constructor(
    accountRepo: AccountRepo
) : ViewModel() {

    val accList = accountRepo.loadAllFull()
}
