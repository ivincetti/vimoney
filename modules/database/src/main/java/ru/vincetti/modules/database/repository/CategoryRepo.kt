package ru.vincetti.modules.database.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import ru.vincetti.modules.core.models.Category
import ru.vincetti.modules.database.sqlite.CategoryDao
import ru.vincetti.modules.database.sqlite.models.CategoryModel
import javax.inject.Inject

class CategoryRepo @Inject constructor(
    private val categoryDao: CategoryDao
) {

    suspend fun loadById(id: Int): Category? {
        return categoryDao.loadCategoryById(id)?.toCategory()
    }

    fun loadAllObservable(): LiveData<List<Category>> {
        return Transformations.map(categoryDao.loadCategoriesObservable()) { categories ->
            categories.map { it.toCategory() }
        }
    }

    suspend fun loadAll(): List<Category>? {
        return categoryDao.loadCategories()?.map { it.toCategory() }
    }

    suspend fun add(cat: Category) {
        categoryDao.insertCategory(CategoryModel.from(cat))
    }

    suspend fun add(cats: List<Category>) {
        categoryDao.insertCategories(
            cats.map { CategoryModel.from(it) }
        )
    }

    suspend fun update(cat: Category) {
        categoryDao.updateCategory(CategoryModel.from(cat))
    }

    suspend fun delete(cat: Category) {
        categoryDao.deleteCategory(CategoryModel.from(cat))
    }

    suspend fun deleteAll() {
        categoryDao.deleteAllCategories()
    }
}
