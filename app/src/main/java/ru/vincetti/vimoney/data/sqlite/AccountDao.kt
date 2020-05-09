package ru.vincetti.vimoney.data.sqlite

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.models.AccountModel

@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts ORDER BY id ASC")
    suspend fun loadAllAccounts(): List<AccountModel>?

    @Query("SELECT * FROM accounts ORDER BY archive ASC, name ASC")
    suspend fun loadAllAccountsList(): List<AccountModel>

    @Query("SELECT accounts.id, accounts.name, currency.symbol AS account_symbol, " +
            "accounts.sum, accounts.type, accounts.archive, accounts.color " +
            "FROM accounts, currency " +
            "WHERE accounts.currency == currency.code " +
            "ORDER BY accounts.archive ASC, accounts.name ASC")
    fun loadAllAccountsFull(): LiveData<List<AccountListModel>>

    @Query("SELECT accounts.id, accounts.name, currency.symbol AS account_symbol, " +
            "accounts.sum, accounts.type, accounts.archive, accounts.color " +
            "FROM accounts, currency " +
            "WHERE accounts.currency == currency.code " +
            "ORDER BY accounts.archive ASC, accounts.name ASC")
    suspend fun loadAllAccountsFullList(): List<AccountListModel>

    @Query("SELECT * FROM accounts WHERE archive = 0 ORDER BY name ASC")
    fun loadNotArchiveAccounts(): LiveData<List<AccountModel>>

    @Query("SELECT * FROM accounts WHERE archive = 0 ORDER BY name ASC")
    suspend fun loadNotArchiveAccountsList(): List<AccountModel>

    @Query("SELECT accounts.id, accounts.name, currency.symbol AS account_symbol, " +
            "accounts.sum, accounts.type, accounts.archive, accounts.color " +
            "FROM accounts, currency " +
            "WHERE accounts.currency == currency.code AND  accounts.archive = 0 " +
            "ORDER BY accounts.name ASC")
    fun loadNotArchiveAccountsFull(): List<AccountListModel>

    @Query("SELECT * FROM accounts WHERE id = :accId")
    suspend fun loadAccountById(accId: Int): AccountModel

    @Query("SELECT accounts.id, accounts.name, currency.symbol AS account_symbol, " +
            "accounts.sum, accounts.type, accounts.archive, accounts.color " +
            "FROM accounts, currency " +
            "WHERE accounts.currency == currency.code " +
            "AND  accounts.id = :accId " +
            "ORDER BY accounts.name ASC")
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
