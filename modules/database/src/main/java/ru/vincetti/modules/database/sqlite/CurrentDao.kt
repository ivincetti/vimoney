package ru.vincetti.modules.database.sqlite

import androidx.room.*
import ru.vincetti.modules.database.sqlite.models.CurrencyModel

@Dao
interface CurrentDao {

    @Query("Select * from currency ORDER BY id ASC")
    suspend fun loadAllCurrency(): List<CurrencyModel>?

    @Query("Select * from currency WHERE code = :code LIMIT 1")
    suspend fun loadCurrencyByCode(code: Int): CurrencyModel?

    @Insert
    suspend fun insertCurrency(cur: CurrencyModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCurrency(cur: CurrencyModel)

    @Delete
    suspend fun deleteCurrency(cur: CurrencyModel)
}
