package ru.vincetti.vimoney.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vincetti.modules.files.JsonFile
import ru.vincetti.modules.core.utils.SingleLiveEvent
import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.modules.database.repository.CategoryRepo
import ru.vincetti.modules.database.repository.TransactionRepo
import ru.vincetti.modules.database.sqlite.ExportData
import ru.vincetti.modules.database.sqlite.ImportData
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val jsonFile: JsonFile,
    private val transactionRepo: TransactionRepo,
    private val accountRepo: AccountRepo,
    private val categoryRepo: CategoryRepo
) : ViewModel() {

    val need2Navigate2Home = SingleLiveEvent<Boolean>()

    val need2Navigate2Categories = SingleLiveEvent<Boolean>()

    private var _buttonsState = MutableLiveData<Boolean>()
    val buttonsState: LiveData<Boolean>
        get() = _buttonsState

    init {
        _buttonsState.value = true
    }

    fun backButtonClicked() {
        need2Navigate2Home.value = true
    }

    fun categoriesButtonClicked() {
        need2Navigate2Categories.value = true
    }

    fun saveJson() {
        _buttonsState.value = false
        viewModelScope.launch {
            jsonFile.save(
                ExportData.export(
                    transactionRepo,
                    accountRepo,
                    categoryRepo
                )
            )
            withContext(Dispatchers.Main) {
                _buttonsState.value = true
            }
        }
    }

    fun loadJson() {
        _buttonsState.value = false
        viewModelScope.launch {
            ImportData.import(
                transactionRepo,
                accountRepo,
                categoryRepo,
                jsonFile.getData()
            )
            withContext(Dispatchers.Main) {
                _buttonsState.value = true
            }
        }
    }
}
