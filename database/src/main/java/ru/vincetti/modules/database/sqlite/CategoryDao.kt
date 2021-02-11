package ru.vincetti.modules.database.sqlite

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.vincetti.modules.database.sqlite.models.CategoryModel

@Dao
interface CategoryDao {

    @Query("Select * from category ORDER BY id ASC")
    fun loadCategoriesObservable(): LiveData<List<CategoryModel>>

    @Query("Select * from category ORDER BY id ASC")
    suspend fun loadCategories(): List<CategoryModel>?

    @Query("Select * from category WHERE id = :id LIMIT 1")
    suspend fun loadCategoryById(id: Int): CategoryModel?

    @Insert
    suspend fun insertCategory(cat: CategoryModel)

    @Insert
    suspend fun insertCategories(cat: List<CategoryModel>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCategory(cat: CategoryModel)

    @Delete
    suspend fun deleteCategory(cat: CategoryModel)

    @Query("Delete FROM category")
    suspend fun deleteAllCategories()
}
