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

    private var _need2Navigate = MutableLiveData<Boolean>()
    val need2Navigate: LiveData<Boolean>
        get() = _need2Navigate

    private var _categoryName = MutableLiveData<String>()
    val categoryName: LiveData<String>
        get() = _categoryName

    private var _categorySymbol = MutableLiveData<String>()
    val categorySymbol: LiveData<String>
        get() = _categorySymbol

    var need2AllData = MutableLiveData<Boolean>()

    init {
        isDefault.value = isDefaultBool
        need2AllData.value = false
        _need2Navigate.value = false
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

    /** Save category logic. */
    fun save(name: String, symbol: String) {
        viewModelScope.launch {
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(symbol)) {
                need2AllData.value = true
            } else {
                val tmpCategory = CategoryModel(name = name, symbol = symbol)
                if (!isDefaultBool) {
                    tmpCategory.id = categoryID
                    categoryDao.updateCategory(tmpCategory)
                } else {
                    categoryDao.insertCategory(tmpCategory)
                }
                _need2Navigate.value = true
            }
        }
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
