package ru.vincetti.modules.database.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.modules.core.models.Category
import ru.vincetti.modules.database.sqlite.CategoryDao
import ru.vincetti.modules.database.sqlite.models.CategoryModel
import javax.inject.Inject

class CategoryRepo private constructor(
    private val categoryDao: CategoryDao,
    private val dispatcher: CoroutineDispatcher
) {

    @Inject
    constructor(categoryDao: CategoryDao) : this(categoryDao, Dispatchers.IO)

    fun loadAllLive(): LiveData<List<Category>> {
        return categoryDao.loadAllLive().map { list ->
            list.map { it.toCategory() }
        }
    }

    fun loadByIdLive(id: Int): LiveData<Category?> {
        return categoryDao.loadByIdLive(id).map {
            it?.toCategory()
        }
    }

    suspend fun loadAll(): List<Category> = withContext(dispatcher) {
        categoryDao.loadAll().map { it.toCategory() }
    }

    suspend fun loadById(id: Int): Category? = withContext(dispatcher) {
        categoryDao.loadById(id)?.toCategory()
    }

    suspend fun add(cat: Category) = withContext(dispatcher) {
        categoryDao.insertCategory(CategoryModel.from(cat))
    }

    suspend fun add(cats: List<Category>) = withContext(dispatcher) {
        categoryDao.insertCategories(
            cats.map { CategoryModel.from(it) }
        )
    }

    suspend fun update(cat: Category) = withContext(dispatcher) {
        categoryDao.updateCategory(CategoryModel.from(cat))
    }

    suspend fun delete(cat: Category) = withContext(dispatcher) {
        categoryDao.deleteCategory(CategoryModel.from(cat))
    }

    suspend fun deleteAll() = withContext(dispatcher) {
        categoryDao.deleteAllCategories()
    }
}
