package ru.vincetti.vimoney.ui.settings.category.add

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.vincetti.modules.core.utils.SingleLiveEvent
import ru.vincetti.vimoney.models.CategoriesModel
import ru.vincetti.vimoney.ui.settings.category.symbol.Category
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    private val categoriesModel: CategoriesModel
) : ViewModel() {

    companion object {
        const val EXTRA_CATEGORY_ID = "Extra_category_id"
        private const val DEFAULT_CATEGORY_ID = -1
    }

    private var categoryID = DEFAULT_CATEGORY_ID

    private val categories = Category.values().map { it.symbol }

    private val _isDefault = MutableLiveData<Boolean>()
    val isDefault: LiveData<Boolean>
        get() = _isDefault

    private var isDefaultBool = true

    private val _need2NavigateBack = SingleLiveEvent<Unit>()
    val need2NavigateBack: LiveData<Unit>
        get() = _need2NavigateBack

    private var _categoryName = MutableLiveData<String>()
    val categoryName: LiveData<String>
        get() = _categoryName

    private var _categorySymbol = MutableLiveData<String>()
    val categorySymbol: LiveData<String>
        get() = _categorySymbol

    private var _need2AllData = MutableLiveData<Unit>()
    val need2AllData: LiveData<Unit>
        get() = _need2AllData

    init {
        _isDefault.value = isDefaultBool
        _categorySymbol.value = "\uf544"
    }

    fun setCategorySymbol(position: Int) {
        _categorySymbol.value = categories[position]
    }

    fun loadCategory(id: Int) {
        if (id > 0) {
            viewModelScope.launch {
                categoriesModel.loadCategoryById(id)?.let {
                    categoryID = id
                    _categoryName.value = it.name
                    _categorySymbol.value = it.symbol
                    _isDefault.value = false
                    isDefaultBool = false
                }
            }
        }
    }

    fun save(name: String, symbol: String) {
        viewModelScope.launch {
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(symbol)) {
                _need2AllData.value = Unit
            } else {
                val tmpCategory = ru.vincetti.modules.core.models.Category(name = name, symbol = symbol)
                if (!isDefaultBool) {
                    tmpCategory.id = categoryID
                    categoriesModel.updateCategory(tmpCategory)
                } else {
                    categoriesModel.addCategory(tmpCategory)
                }
                need2navigateBack()
            }
        }
    }

    fun need2navigateBack() {
        _need2NavigateBack.value = Unit
    }
}
