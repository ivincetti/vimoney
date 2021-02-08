package ru.vincetti.vimoney.ui.check.list

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.modules.core.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class CheckListViewModel @Inject constructor(
    accountRepo: AccountRepo
) : ViewModel() {

    val accList = accountRepo.loadAllFull()
    val needNavigate2Check = SingleLiveEvent<Int>()

    fun clickOnElement(id: Int) {
        needNavigate2Check.value = id
    }
}
