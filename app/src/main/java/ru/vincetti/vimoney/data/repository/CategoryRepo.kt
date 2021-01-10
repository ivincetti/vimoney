package ru.vincetti.vimoney.data.repository

import androidx.lifecycle.LiveData
import ru.vincetti.vimoney.data.models.CategoryModel
import ru.vincetti.vimoney.data.sqlite.CategoryDao
import javax.inject.Inject

class CategoryRepo @Inject constructor(
    private val categoryDao: CategoryDao
) {

    suspend fun loadById(id: Int): CategoryModel? {
        return categoryDao.loadCategoryById(id)
    }

    fun loadAllObservable(): LiveData<List<CategoryModel>> {
        return categoryDao.loadCategoriesObservable()
    }

    suspend fun loadAll(): List<CategoryModel>? {
        return categoryDao.loadCategories()
    }

    suspend fun add(cat: CategoryModel) {
        categoryDao.insertCategory(cat)
    }

    suspend fun update(cat: CategoryModel) {
        categoryDao.updateCategory(cat)
    }

    suspend fun delete(cat: CategoryModel) {
        categoryDao.deleteCategory(cat)
    }

    suspend fun deleteAll() {
        categoryDao.deleteAllCategories()
    }
}
