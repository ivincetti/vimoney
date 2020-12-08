package ru.vincetti.vimoney.data.sqlite

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.models.AccountModel

@Suppress("TooManyFunctions")
@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts ORDER BY id ASC")
    suspend fun loadAllAccounts(): List<AccountModel>?

    @Query(
        """
        SELECT accounts.*, c.symbol
        FROM accounts
        JOIN currency c ON c.code = accounts.currency
        ORDER BY accounts.archive ASC, accounts.need_on_main_screen DESC, accounts.name ASC
        """
    )
    fun loadAllAccountsFull(): LiveData<List<AccountListModel>>

    @Query(
        """
        SELECT accounts.*, c.symbol 
        FROM accounts
        JOIN currency c ON c.code = accounts.currency
        WHERE archive = 0 
        ORDER BY name ASC
        """
    )
    suspend fun loadNotArchiveAccounts(): List<AccountListModel>?

    @Query(
        """
        SELECT accounts.*, c.symbol 
        FROM accounts
        JOIN currency c ON c.code = accounts.currency
        WHERE accounts.archive = 0
        AND accounts.need_on_main_screen = 1
        ORDER BY accounts.name ASC
        """
    )
    fun loadMainAccountsFull(): LiveData<List<AccountListModel>>

    @Query("SELECT * FROM accounts WHERE id = :accId")
    suspend fun loadAccountById(accId: Int): AccountModel?

    @Query(
        """
        SELECT accounts.*, c.symbol 
        FROM accounts
        JOIN currency c ON c.code = accounts.currency
        AND  accounts.id = :accId
        ORDER BY accounts.name ASC
        """
    )
    fun loadAccountByIdFull(accId: Int): LiveData<AccountListModel>

    @Query("UPDATE accounts SET sum = :sum WHERE id = :accId")
    suspend fun updateSumByAccId(accId: Int, sum: Float)

    @Query("UPDATE accounts SET archive = 1 WHERE id = :accId")
    suspend fun archiveAccountById(accId: Int)

    @Query("UPDATE accounts SET archive = 0 WHERE id = :accId")
    suspend fun fromArchiveAccountById(accId: Int)

    @Insert
    suspend fun insertAccount(acc: AccountModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAccount(acc: AccountModel)

    @Query("Delete FROM accounts")
    suspend fun deleteAllAccounts()
}
