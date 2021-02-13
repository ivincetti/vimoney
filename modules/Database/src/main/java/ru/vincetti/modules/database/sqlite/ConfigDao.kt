package ru.vincetti.modules.database.sqlite

import androidx.room.*
import ru.vincetti.modules.database.sqlite.models.ConfigModel

@Dao
interface ConfigDao {

    @Query("SELECT * FROM config WHERE key_name = :key LIMIT 1")
    suspend fun loadConfigByKey(key: String): ConfigModel?

    @Insert
    suspend fun insertConfig(conf: ConfigModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateConfig(conf: ConfigModel)
}
