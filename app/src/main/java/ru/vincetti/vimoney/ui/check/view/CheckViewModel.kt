package ru.vincetti.vimoney.ui.check.view

import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.vimoney.models.CheckModel

class CheckViewModel @AssistedInject constructor(
    private val model: CheckModel,
    @Assisted private val accountId: Int
) : ViewModel() {

    private val _content = MutableLiveData<State>()
    val content: LiveData<State>
        get() = _content

    private var state: State = State.Loading
        set(value) {
            field = value
            _content.value = value
        }

    init {
        model.setAccountId(accountId)
        load()
    }

    fun restore() {
        viewModelScope.launch {
            model.restore()
            updateCheckData()
        }
    }

    fun delete() {
        viewModelScope.launch {
            model.delete()
            updateCheckData()
        }
    }

    fun update() {
        (state as? State.Content)?.let {
            state = it.copy(updating = true)

            viewModelScope.launch {
                model.update()
                updateCheckData()
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            updateCheckData()
        }
    }

    private suspend fun updateCheckData() {
        state = getCheckState()
    }

    private suspend fun getCheckState(): State {
        return model.loadLiveAccountById()?.toState() ?: State.Error
    }

    private fun AccountList.toState(): State {
        return State.Content(
            updating = false,
            checkId = this.id,
            checkName = this.name,
            checkType = this.type,
            checkBalance = this.sum.toString(),
            checkSymbol = this.curSymbol,
            checkLabelColor = this.color,
            isArchive = this.isArchive,
            isNeedOnMain = this.needOnMain,
        )
    }

    @AssistedFactory
    interface CheckViewModelFactory {

        fun create(accountId: Int): CheckViewModel
    }

    sealed class State {

        data class Content(
            val updating: Boolean,
            val checkId: Int,
            val checkName: String,
            val checkType: String,
            val checkBalance: String,
            val checkSymbol: String,
            val checkLabelColor: String,
            val isArchive: Boolean,
            val isNeedOnMain: Boolean,
        ) : State()

        object Loading : State()

        object Error : State()
    }

    companion object {

        fun provideFactory(
            assistedFactory: CheckViewModelFactory,
            accountId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(accountId) as T
            }
        }
    }
}
