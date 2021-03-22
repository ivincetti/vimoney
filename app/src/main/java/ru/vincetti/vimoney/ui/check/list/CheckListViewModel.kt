package ru.vincetti.vimoney.ui.check.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.vincetti.modules.core.utils.SingleLiveEvent
import ru.vincetti.vimoney.models.CheckListModel
import javax.inject.Inject

@HiltViewModel
class CheckListViewModel @Inject constructor(
    checkListModel: CheckListModel,
) : ViewModel() {

    val accounts = checkListModel.loadAllAccounts()

    private val _needNavigate2Check = SingleLiveEvent<Int>()
    val needNavigate2Check: LiveData<Int>
        get() = _needNavigate2Check

    fun clickOnElement(id: Int) {
        _needNavigate2Check.value = id
    }
}
