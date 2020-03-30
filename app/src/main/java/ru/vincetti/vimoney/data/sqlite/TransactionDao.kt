package ru.vincetti.vimoney.data.sqlite

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.vincetti.vimoney.data.models.TransactionListModel
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.models.TransactionStatDayModel

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY id ASC")
    suspend fun loadAllTransactions(): List<TransactionModel>?

    @Query("SELECT * FROM transactions WHERE transactions.system == 0 ORDER BY date DESC LIMIT :num")
    fun loadAllTransactionsCount(num: Int): LiveData<List<TransactionModel>>

    @Query("SELECT transactions.id, accounts.name AS account_name, currency.symbol AS account_symbol, " +
            "transactions.sum, transactions.type, transactions.date, transactions.description, " +
            "transactions.extra_key, transactions.extra_value " +
            "FROM transactions, accounts, currency " +
            "WHERE transactions.account_id == accounts.id " +
            "AND accounts.currency == currency.code " +
            "AND transactions.system == 0 " +
            "ORDER BY transactions.date DESC")
    fun loadAllTransactionsFull(): LiveData<List<TransactionListModel>>

    @Query("SELECT transactions.id, accounts.name AS account_name, currency.symbol AS account_symbol, " +
            "transactions.sum, transactions.type, transactions.date, transactions.description, " +
            "transactions.extra_key, transactions.extra_value " +
            "FROM transactions, accounts, currency " +
            "WHERE transactions.account_id == accounts.id " +
            "AND accounts.currency == currency.code " +
            "AND transactions.system == 0 " +
            "ORDER BY transactions.date DESC LIMIT :num")
    fun loadAllTransactionsCountFull(num: Int): LiveData<List<TransactionListModel>>

    @Query("SELECT * FROM transactions WHERE account_id = :id AND transactions.system == 0 ORDER BY date DESC LIMIT :num")
    fun loadCheckTransactionsCount(id: Int, num: Int): LiveData<List<TransactionModel>>

    @Query("SELECT transactions.id, accounts.name AS account_name, currency.symbol AS account_symbol," +
            "transactions.sum, transactions.type, transactions.date, transactions.description, " +
            "transactions.extra_key, transactions.extra_value " +
            "FROM transactions, accounts, currency " +
            "WHERE transactions.account_id == accounts.id " +
            "AND accounts.currency == currency.code " +
            "AND ((transactions.account_id == :id" + " AND transactions.system == 0) " +
            "OR transactions.id IN (SELECT id from transactions WHERE extra_value IN(SELECT id from transactions WHERE account_id = :id AND system=1))) " +
            "ORDER BY transactions.date DESC")
    fun loadCheckTransactionsFull(id: Int): LiveData<List<TransactionListModel>>

    @Query("SELECT transactions.id, accounts.name AS account_name, currency.symbol AS account_symbol," +
            "transactions.sum, transactions.type, transactions.date, transactions.description, " +
            "transactions.extra_key, transactions.extra_value " +
            "FROM transactions, accounts, currency " +
            "WHERE transactions.account_id == accounts.id " +
            "AND accounts.currency == currency.code " +
            "AND ((transactions.account_id == :id" + " AND transactions.system == 0) " +
            "OR transactions.id IN (SELECT id from transactions WHERE extra_value IN(SELECT id from transactions WHERE account_id = :id AND system=1))) " +
            "ORDER BY transactions.date DESC LIMIT :num")
    fun loadCheckTransactionsCountFull(id: Int, num: Int): LiveData<List<TransactionListModel>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun loadTransactionById(id: Int): TransactionModel?

    @Query("SELECT IFNULL(Sum(sum),0) FROM transactions WHERE type ="
            + TransactionModel.TRANSACTION_TYPE_INCOME
            + " AND strftime('%m', datetime(date/1000, 'unixepoch', 'localtime')) = :month"
            + " AND strftime('%Y', datetime(date/1000, 'unixepoch', 'localtime')) = :year"
            + " AND system=0")
    fun loadSumTransactionIncomeMonth(month: String, year: String): LiveData<Int>

    @Query("SELECT IFNULL(Sum(sum),0) FROM transactions WHERE type ="
            + TransactionModel.TRANSACTION_TYPE_SPENT
            + " AND strftime('%m', datetime(date/1000, 'unixepoch', 'localtime')) = :month"
            + " AND strftime('%Y', datetime(date/1000, 'unixepoch', 'localtime')) = :year")
    fun loadSumTransactionExpenseMonth(month: String, year: String): LiveData<Int>

    @Query("SELECT strftime('%d', datetime(date/1000, 'unixepoch', 'localtime')) AS day, " +
            "((Select IFNULL(Sum(sum),0) FROM transactions t2 WHERE " +
            "strftime('%d', datetime(t2.date/1000, 'unixepoch', 'localtime')) = strftime('%d', datetime(transactions.date/1000, 'unixepoch', 'localtime')) AND " +
            "strftime('%m', datetime(t2.date/1000, 'unixepoch', 'localtime')) = :month AND " +
            "strftime('%Y', datetime(t2.date/1000, 'unixepoch', 'localtime')) = \"2019\" AND " +
            "t2.type = 1) -\n" +
            "(Select IFNULL(Sum(sum),0) FROM transactions t3 WHERE \n" +
            "strftime('%d', datetime(t3.date/1000, 'unixepoch', 'localtime')) = strftime('%d', datetime(transactions.date/1000, 'unixepoch', 'localtime')) AND\n" +
            "strftime('%m', datetime(t3.date/1000, 'unixepoch', 'localtime')) = :month AND\n" +
            "strftime('%Y', datetime(t3.date/1000, 'unixepoch', 'localtime')) = \"2019\" AND\n" +
            "type = 2 )) AS sum " +
            "FROM transactions WHERE system=0 "
            + " AND strftime('%m', datetime(date/1000, 'unixepoch', 'localtime')) = :month"
            + " AND strftime('%Y', datetime(date/1000, 'unixepoch', 'localtime')) = :year"
            + " GROUP BY day "
            + " ORDER BY transactions.date"
    )
    suspend fun loadTransactionStatByMonth(month: String, year: String): List<TransactionStatDayModel>

    @Query("SELECT IFNULL((Select SUM(sum) FROM transactions WHERE type="
            + TransactionModel.TRANSACTION_TYPE_INCOME + " AND account_id = :accId),0) - "
            + "IFNULL((SELECT SUM(sum) FROM transactions WHERE (type="
            + TransactionModel.TRANSACTION_TYPE_SPENT
            + " OR type=" + TransactionModel.TRANSACTION_TYPE_TRANSFER + ") " +
            "AND  account_id = :accId),0)")
    suspend fun loadSumByCheckId(accId: Int): Float

    @Query("SELECT SUM(sum) FROM transactions WHERE type="
            + TransactionModel.TRANSACTION_TYPE_INCOME + " AND account_id = :accId")
    suspend fun loadSumIncomeByCheckId(accId: Int): Float

    @Query("SELECT SUM(sum) FROM transactions"
            + " WHERE (type=" + TransactionModel.TRANSACTION_TYPE_SPENT
            + " OR type=" + TransactionModel.TRANSACTION_TYPE_TRANSFER + ")"
            + " AND  account_id = :accId")
    suspend fun loadSumExpenseByCheckId(accId: Int): Float

    @Query("SELECT account_id FROM transactions WHERE id = :id")
    suspend fun getAccountTransactionById(id: Int): Int

    @Query("DELETE FROM transactions WHERE id = :transId")
    suspend fun deleteTransactionById(transId: Int)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(t: TransactionModel): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTransaction(t: TransactionModel)
}
