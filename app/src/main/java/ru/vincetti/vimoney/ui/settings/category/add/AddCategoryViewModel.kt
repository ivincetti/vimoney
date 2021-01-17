package ru.vincetti.vimoney.ui.settings.category.add

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.models.CategoryModel
import ru.vincetti.vimoney.data.repository.CategoryRepo
import ru.vincetti.vimoney.ui.settings.category.symbol.Category
import ru.vincetti.vimoney.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    private val categoryRepo: CategoryRepo
) : ViewModel() {

    companion object {
        const val EXTRA_CATEGORY_ID = "Extra_category_id"
        private const val DEFAULT_CATEGORY_ID = -1
    }

    private var categoryID = DEFAULT_CATEGORY_ID

    private val categories = Category.values().map {
        it.symbol
    }

    val isDefault = MutableLiveData<Boolean>()
    private var isDefaultBool = true

    val need2NavigateBack = SingleLiveEvent<Boolean>()

    private var _categoryName = MutableLiveData<String>()
    val categoryName: LiveData<String>
        get() = _categoryName

    private var _categorySymbol = MutableLiveData<String>()
    val categorySymbol: LiveData<String>
        get() = _categorySymbol

    private var _need2AllData = MutableLiveData<Boolean>()
    val need2AllData: LiveData<Boolean>
        get() = _need2AllData

    init {
        isDefault.value = isDefaultBool
        _need2AllData.value = false
        _categorySymbol.value = "\uf544"
    }

    fun setCategorySymbol(position: Int) {
        _categorySymbol.value = categories[position]
    }

    fun loadCategory(id: Int) {
        viewModelScope.launch {
            categoryRepo.loadById(id)?.let {
                categoryID = id
                _categoryName.value = it.name
                _categorySymbol.value = it.symbol
                isDefault.value = false
                isDefaultBool = false
            }
        }
    }

    fun save(name: String, symbol: String) {
        viewModelScope.launch {
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(symbol)) {
                _need2AllData.value = true
            } else {
                val tmpCategory = CategoryModel(name = name, symbol = symbol)
                if (!isDefaultBool) {
                    tmpCategory.id = categoryID
                    categoryRepo.update(tmpCategory)
                } else {
                    categoryRepo.add(tmpCategory)
                }
                need2navigateBack()
            }
        }
    }

    fun need2navigateBack() {
        need2NavigateBack.value = true
    }

    fun noDataDialogClosed() {
        _need2AllData.value = false
    }
}
