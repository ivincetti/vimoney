package ru.vincetti.vimoney.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class CategoryModel(

        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,

        val name: String,

        val symbol: String
)
