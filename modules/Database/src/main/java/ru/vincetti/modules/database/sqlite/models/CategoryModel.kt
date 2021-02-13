package ru.vincetti.modules.database.sqlite.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.vincetti.modules.core.models.Category

@Entity(tableName = "category")
data class CategoryModel(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    val name: String,

    val symbol: String
) {
    fun toCategory(): Category {
        return Category(
            id, name, symbol
        )
    }

    companion object {
        fun from(category: Category): CategoryModel {
            return CategoryModel(
                category.id,
                category.name,
                category.symbol
            )
        }
    }
}
