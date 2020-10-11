package ru.vincetti.vimoney.ui.settings.category.add

import android.text.TextUtils
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.models.CategoryModel
import ru.vincetti.vimoney.data.sqlite.CategoryDao
import ru.vincetti.vimoney.ui.settings.category.symbol.Category

class AddCategoryViewModel(
    private val categoryDao: CategoryDao
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

    private var _need2NavigateBack = MutableLiveData<Boolean>()
    val need2NavigateBack: LiveData<Boolean>
        get() = _need2NavigateBack

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
        _need2NavigateBack.value = false
        _categorySymbol.value = "\uf544"
    }

    fun setCategorySymbol(position: Int) {
        _categorySymbol.value = categories[position]
    }

    fun loadCategory(id: Int) {
        viewModelScope.launch {
            categoryDao.loadCategoryById(id)?.let {
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
                    categoryDao.updateCategory(tmpCategory)
                } else {
                    categoryDao.insertCategory(tmpCategory)
                }
                _need2NavigateBack.value = true
            }
        }
    }

    fun need2navigateBack() {
        _need2NavigateBack.value = true
    }

    fun navigatedBack() {
        _need2NavigateBack.value = false
    }

    fun noDataDialogClosed() {
        _need2AllData.value = false
    }
}

class AddCategoryViewModelFactory(
    private val categoryDao: CategoryDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCategoryViewModel::class.java)) {
            return AddCategoryViewModel(categoryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
