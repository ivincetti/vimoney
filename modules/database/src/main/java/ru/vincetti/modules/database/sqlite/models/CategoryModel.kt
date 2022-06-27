package ru.vincetti.modules.database.sqlite.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.vincetti.modules.core.models.Category

@Entity(tableName = "category")
data class CategoryModel(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    val name: String,

    val symbol: String,

    @ColumnInfo(name = "expense")
    val isForExpense: Boolean = false,

    @ColumnInfo(name = "income")
    val isForIncome: Boolean = false,

    @ColumnInfo(name = "archive")
    var isArchive: Boolean = false,
) {
    fun toCategory(): Category {
        return Category(
            id = id,
            name = name,
            symbol = symbol,
            isForExpense = isForExpense,
            isForIncome = isForIncome,
            isArchive = isArchive,
        )
    }

    companion object {
        fun from(category: Category): CategoryModel {
            return CategoryModel(
                id = category.id,
                name = category.name,
                symbol = category.symbol,
                isForExpense = category.isForExpense,
                isForIncome = category.isForIncome,
                isArchive = category.isArchive,
            )
        }
    }
}
