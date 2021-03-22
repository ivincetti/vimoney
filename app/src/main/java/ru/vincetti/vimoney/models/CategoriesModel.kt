package ru.vincetti.vimoney.models

import ru.vincetti.modules.core.models.Category
import ru.vincetti.modules.database.repository.CategoryRepo
import javax.inject.Inject

class CategoriesModel @Inject constructor(
    private val categoryRepo: CategoryRepo
) {

    fun getAllCategories() = categoryRepo.loadAllLive()

    suspend fun loadCategoryById(id: Int): Category? {
        return categoryRepo.loadById(id)
    }

    suspend fun addCategory(cat: Category) {
        categoryRepo.add(cat)
    }

    suspend fun updateCategory(cat: Category) {
        categoryRepo.update(cat)
    }
}
