package ru.vincetti.vimoney.ui.settings.category.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.vincetti.modules.core.utils.SingleLiveEvent
import ru.vincetti.vimoney.models.CategoriesModel
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    private val model: CategoriesModel
) : ViewModel() {

    val isDefault: LiveData<Boolean> = model.isDefault

    val categoryName: LiveData<String> = model.categoryName

    val categorySymbol: LiveData<String> = model.categorySymbol

    private var _need2AllData = MutableLiveData<Unit>()
    val need2AllData: LiveData<Unit>
        get() = _need2AllData

    private val _need2NavigateBack = SingleLiveEvent<Unit>()
    val need2NavigateBack: LiveData<Unit>
        get() = _need2NavigateBack

    fun setCategorySymbol(position: Int) {
        model.setCategorySymbol(position)
    }

    fun loadCategory(id: Int) {
        viewModelScope.launch {
            model.loadCategory(id)
        }
    }

    fun save(name: String, symbol: String) {
        if (name.isEmpty() || symbol.isEmpty()) {
            _need2AllData.value = Unit
        } else {
            viewModelScope.launch {
                model.save(name, symbol)
                need2navigateBack()
            }
        }
    }

    fun need2navigateBack() {
        _need2NavigateBack.value = Unit
    }
}
