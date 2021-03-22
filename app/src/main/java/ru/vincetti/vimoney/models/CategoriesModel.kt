package ru.vincetti.vimoney.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import ru.vincetti.modules.core.models.Category
import ru.vincetti.modules.database.repository.CategoryRepo
import javax.inject.Inject
import ru.vincetti.vimoney.ui.settings.category.symbol.Category as Categories

class CategoriesModel @Inject constructor(
    private val categoryRepo: CategoryRepo
) {

    private var categoryID = DEFAULT_CATEGORY_ID

    private var isDefaultBool = true

    private val categories = Categories.values().map { it.symbol }

    private val category = MutableLiveData<Category>()

    val isDefault: LiveData<Boolean> = category.map { it.id > 0 }

    val categorySymbol: LiveData<String> = category.map { it.symbol }

    private var _categoryName = MutableLiveData<String>()
    val categoryName: LiveData<String>
        get() = _categoryName

    init {
        category.value = Category()
    }

    fun setCategorySymbol(position: Int) {
        category.value = category.value!!.copy(symbol = categories[position])
    }

    suspend fun loadCategory(id: Int) {
        if (id > 0) {
            loadCategoryById(id)?.let {
                category.value = it
                _categoryName.value = it.name
                categoryID = id
                isDefaultBool = false
            }
        }
    }

    suspend fun save(name: String, symbol: String) {
        val tmpCategory = Category(name = name, symbol = symbol)
        if (!isDefaultBool) {
            tmpCategory.id = categoryID
            updateCategory(tmpCategory)
        } else {
            addCategory(tmpCategory)
        }
    }

    fun getAllCategories(): LiveData<List<Category>> = categoryRepo.loadAllLive()

    private suspend fun loadCategoryById(id: Int): Category? {
        return categoryRepo.loadById(id)
    }

    private suspend fun addCategory(cat: Category) {
        categoryRepo.add(cat)
    }

    private suspend fun updateCategory(cat: Category) {
        categoryRepo.update(cat)
    }

    companion object {

        private const val DEFAULT_CATEGORY_ID = -1
    }
}
