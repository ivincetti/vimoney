package ru.vincetti.vimoney.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "config")
data class ConfigModel(
        @PrimaryKey(autoGenerate = true) var id: Int?,
        @ColumnInfo(name = "key_name") private val keyName: String,
        private val value: String
) {

    fun getKeyName(): String {
        return keyName
    }

    fun getValue(): String {
        return value
    }
}
