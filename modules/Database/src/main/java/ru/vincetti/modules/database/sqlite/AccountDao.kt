package ru.vincetti.modules.database.sqlite

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.vincetti.modules.database.sqlite.models.AccountListModel
import ru.vincetti.modules.database.sqlite.models.AccountModel

@Suppress("TooManyFunctions")
@Dao
interface AccountDao {

    @Query(
        """
        SELECT accounts.*, c.symbol
        FROM accounts
        JOIN currency c ON c.code = accounts.currency
        ORDER BY accounts.archive ASC, accounts.need_on_main_screen DESC, accounts.name ASC
        """
    )
    fun loadAllFullLive(): LiveData<List<AccountListModel>>

    @Query(
        """
        SELECT accounts.*, c.symbol 
        FROM accounts
        JOIN currency c ON c.code = accounts.currency
        AND  accounts.id = :accId
        ORDER BY accounts.name ASC
        """
    )
    fun loadListByIdLive(accId: Int): LiveData<AccountListModel?>

    @Query(
        """
        SELECT accounts.*, c.symbol 
        FROM accounts
        JOIN currency c ON c.code = accounts.currency
        AND  accounts.id = :accId
        ORDER BY accounts.name ASC
        """
    )
    suspend fun loadListById(accId: Int): AccountListModel?

    @Query("SELECT * FROM accounts WHERE id = :accId")
    suspend fun loadById(accId: Int): AccountModel?

    @Query("SELECT * FROM accounts ORDER BY id ASC")
    suspend fun loadAll(): List<AccountModel>

    @Query("UPDATE accounts SET sum = :sum WHERE id = :accId")
    suspend fun updateSumByAccId(accId: Int, sum: Float)

    @Query("UPDATE accounts SET archive = 1 WHERE id = :accId")
    suspend fun archiveById(accId: Int)

    @Query("UPDATE accounts SET archive = 0 WHERE id = :accId")
    suspend fun fromArchiveById(accId: Int)

    @Insert
    suspend fun insert(acc: AccountModel)

    @Insert
    suspend fun insert(acc: List<AccountModel>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(acc: AccountModel)

    @Query("Delete FROM accounts")
    suspend fun deleteAll()
}
