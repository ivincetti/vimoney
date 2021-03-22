package ru.vincetti.vimoney.models

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.core.models.Currency
import ru.vincetti.modules.usecases.account.*
import ru.vincetti.modules.usecases.currency.GetAllCurrenciesUseCase
import ru.vincetti.modules.usecases.currency.GetLiveCurrencySymbolByCodeUseCase
import javax.inject.Inject

class AddCheckModel @Inject constructor(
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val addAccountUseCase: AddAccountUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val archiveAccountByIdUseCase: ArchiveAccountByIdUseCase,
    private val unArchiveAccountByIdUseCase: UnArchiveAccountByIdUseCase,
    private val getAllCurrenciesUseCase: GetAllCurrenciesUseCase,
    private val getLiveCurrencySymbolByCodeUseCase: GetLiveCurrencySymbolByCodeUseCase,
) {

    private var accountID = Account.DEFAULT_CHECK_ID
    private var isDefaultBool = true

    private var account = MutableLiveData<Account>()

    private val _name: MutableLiveData<String> = MutableLiveData()
    val name: LiveData<String>
        get() = _name

    private val _type: MutableLiveData<String> = MutableLiveData()
    val type: LiveData<String>
        get() = _type

    val isArchive: LiveData<Boolean> = account.map { it.isArchive }

    val currency: LiveData<String> = account.switchMap { getLiveCurrencySymbolByCodeUseCase(it.currency) }

    val color: LiveData<Int> = account.map { Color.parseColor(it.color) }

    val needOnMain: LiveData<Boolean> = account.map { it.needOnMain }

    val needAllBalance: LiveData<Boolean> = account.map { it.needAllBalance }

    val isDefault: LiveData<Boolean> = account.map { (it.id > 0).not() }

    init {
        account.value = Account()
    }

    suspend fun getAllCurrency(): List<Currency> = getAllCurrenciesUseCase()

    suspend fun loadAccountById(id: Int) {
        if (id > 0) {
            getAccountByIdUseCase(id)?.let {
                accountID = id
                account.value = it
                _name.value = it.name
                _type.value = it.type
                isDefaultBool = false
            }
        }
    }

    suspend fun save(name: String, type: String) {
        account.value?.let {
            it.name = name
            it.type = type
            if (!isDefaultBool) {
                updateAccountUseCase(it)
            } else {
                it.sum = 0
                addAccountUseCase(it)
            }
        }
    }

    suspend fun restore() {
        if (!isDefaultBool) {
            unArchiveAccountByIdUseCase(accountID)
        }
    }

    suspend fun delete() {
        if (!isDefaultBool) {
            archiveAccountByIdUseCase(accountID)
        }
    }

    fun setNeedAllBalance(isChecked: Boolean) {
        account.value!!.also {
            it.needAllBalance = isChecked
            account.value = it
        }
    }

    fun setNeedOnMain(isChecked: Boolean) {
        account.value!!.also {
            it.needOnMain = isChecked
            account.value = it
        }
    }

    fun setCurrency(checkCurrency: Int) {
        account.value!!.also {
            it.currency = checkCurrency
            account.value = it
        }
    }

    fun setBackgroundColor(selectedColor: Int) {
        val hexColor = "#${selectedColor.toUInt().toString(16)}"
        account.value!!.also {
            it.color = hexColor
            account.value = it
        }
    }
}
