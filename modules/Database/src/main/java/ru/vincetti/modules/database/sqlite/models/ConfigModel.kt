package ru.vincetti.modules.database.sqlite.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.vincetti.modules.core.models.Config

@Entity(tableName = "config")
data class ConfigModel(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "key_name")
    val keyName: String?,

    val value: String?
) {
    fun toConfig(): Config {
        return Config(
            id, keyName, value
        )
    }

    companion object {
        fun from(config: Config): ConfigModel {
            return ConfigModel(
                config.id,
                config.keyName,
                config.value
            )
        }
    }
}
