package ru.vincetti.vimoney.data.sqlite

import androidx.paging.DataSource
import androidx.room.*
import ru.vincetti.vimoney.data.models.TransactionListModel
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.models.TransactionStatDayModel
import java.util.*

@Suppress("TooManyFunctions")
@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY id ASC")
    suspend fun loadAllTransactions(): List<TransactionModel>

    @Query(
        """
        SELECT tr.id, acc.name AS account_name, cur.symbol AS account_symbol,
        tr.sum, tr.type, tr.date, tr.description, cat.symbol as category_icon, 
        tr.extra_key, tr.extra_value
        FROM transactions as tr
        JOIN accounts acc ON acc.id = tr.account_id
        JOIN category cat ON cat.id = tr.category_id
        JOIN currency cur ON cur.code = acc.currency
        WHERE
        (:accountID == 0 OR tr.account_id = :accountID)
        AND (:categoryID == 0 OR tr.category_id = :categoryID)
        AND (:comment IS NULL OR tr.description LIKE  '%' || :comment || '%')
        AND (:dateFrom IS NULL OR tr.date > :dateFrom) 
        AND (:dateTo IS NULL OR tr.date < :dateTo) 
        AND (:sumFrom == 0 OR tr.sum >= :sumFrom)
        AND tr.system == 0
        ORDER BY tr.date DESC
        """
    )
    fun loadFilterTransactionsFull(
        accountID: Int,
        categoryID: Int,
        comment: String?,
        dateFrom: Date?,
        dateTo: Date?,
        sumFrom: Int
    ): DataSource.Factory<Int, TransactionListModel>

    @Query(
        """
        SELECT tr.id, acc.name AS account_name, cur.symbol AS account_symbol,
        tr.sum, tr.type, tr.date, tr.description, cat.symbol as category_icon, 
        tr.extra_key, tr.extra_value
        FROM transactions as tr
        JOIN accounts acc ON acc.id = tr.account_id
        JOIN category cat ON cat.id = tr.category_id
        JOIN currency cur ON cur.code = acc.currency
        WHERE
        (:accountID == 0 OR tr.account_id = :accountID)
        AND (:categoryID == 0 OR tr.category_id = :categoryID)
        AND (:comment IS NULL OR tr.description LIKE  '%' || :comment || '%')
        AND (:dateFrom IS NULL OR tr.date > :dateFrom) 
        AND (:dateTo IS NULL OR tr.date < :dateTo) 
        AND (:sumFrom == 0 OR tr.sum >= :sumFrom)
        AND tr.system == 0
        ORDER BY tr.date DESC
        LIMIT :count
        """
    )
    fun loadFilterTransactionsFullLimit(
        accountID: Int,
        categoryID: Int,
        comment: String?,
        dateFrom: Date?,
        dateTo: Date?,
        sumFrom: Int,
        count: Int
    ): DataSource.Factory<Int, TransactionListModel>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun loadTransactionById(id: Int): TransactionModel?

    @Query(
        """
        SELECT IFNULL(Sum(sum),0) FROM transactions
        WHERE type = ${TransactionModel.TRANSACTION_TYPE_INCOME}
        AND strftime('%m', datetime(date/1000, 'unixepoch', 'localtime')) = :month
        AND strftime('%Y', datetime(date/1000, 'unixepoch', 'localtime')) = :year
        AND system=0
        """
    )
    suspend fun loadSumTransactionIncomeMonth(month: String, year: String): Int

    @Query(
        """
        SELECT IFNULL(Sum(sum),0) FROM transactions
        WHERE type =${TransactionModel.TRANSACTION_TYPE_SPENT}
        AND strftime('%m', datetime(date/1000, 'unixepoch', 'localtime')) = :month
        AND strftime('%Y', datetime(date/1000, 'unixepoch', 'localtime')) = :year
        """
    )
    suspend fun loadSumTransactionExpenseMonth(month: String, year: String): Int

    @Query(
        """
        SELECT strftime('%d', datetime(date/1000, 'unixepoch', 'localtime')) AS day,
        (
        (Select IFNULL(Sum(sum),0) FROM transactions t2 
        WHERE strftime('%d', datetime(t2.date/1000, 'unixepoch', 'localtime')) = 
        strftime('%d', datetime(transactions.date/1000, 'unixepoch', 'localtime')) 
        AND strftime('%m', datetime(t2.date/1000, 'unixepoch', 'localtime')) = :month 
        AND strftime('%Y', datetime(t2.date/1000, 'unixepoch', 'localtime')) = :year 
        AND t2.type = 1 
        AND system = 0) - 
        (Select IFNULL(Sum(sum),0) FROM transactions t3
        WHERE strftime('%d', datetime(t3.date/1000, 'unixepoch', 'localtime')) =
        strftime('%d', datetime(transactions.date/1000, 'unixepoch', 'localtime'))
        AND strftime('%m', datetime(t3.date/1000, 'unixepoch', 'localtime')) = :month
        AND strftime('%Y', datetime(t3.date/1000, 'unixepoch', 'localtime')) = :year
        AND type = 2 
        AND system = 0)
        ) AS sum
        FROM transactions WHERE system = 0
        AND strftime('%m', datetime(date/1000, 'unixepoch', 'localtime')) = :month
        AND strftime('%Y', datetime(date/1000, 'unixepoch', 'localtime')) = :year
        GROUP BY day
        ORDER BY transactions.date
        """
    )
    suspend fun loadTransactionStatByMonth(
        month: String,
        year: String
    ): List<TransactionStatDayModel>

    @Query(
        """
        SELECT 
        IFNULL((Select SUM(sum) FROM transactions 
        WHERE type=${TransactionModel.TRANSACTION_TYPE_INCOME}
        AND account_id = :accId)
        ,0) -
        IFNULL((SELECT SUM(sum) FROM transactions
        WHERE (type=${TransactionModel.TRANSACTION_TYPE_SPENT}
        OR type=${TransactionModel.TRANSACTION_TYPE_TRANSFER})
        AND  account_id = :accId)
        ,0)
        """
    )
    suspend fun loadSumByCheckId(accId: Int): Float

    @Query(
        """
        SELECT SUM(sum) FROM transactions 
        WHERE type=${TransactionModel.TRANSACTION_TYPE_INCOME} 
        AND account_id = :accId
        """
    )
    suspend fun loadSumIncomeByCheckId(accId: Int): Float

    @Query(
        """
        SELECT SUM(sum) FROM transactions
        WHERE (type=${TransactionModel.TRANSACTION_TYPE_SPENT}
        OR type=${TransactionModel.TRANSACTION_TYPE_TRANSFER})
        AND  account_id = :accId
        """
    )
    suspend fun loadSumExpenseByCheckId(accId: Int): Float

    @Query("SELECT account_id FROM transactions WHERE id = :id")
    suspend fun getAccountTransactionById(id: Int): Int

    @Query("DELETE FROM transactions WHERE id = :transId")
    suspend fun deleteTransactionById(transId: Int)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(t: TransactionModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(t: List<TransactionModel>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTransaction(t: TransactionModel)
}
