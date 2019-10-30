package ru.vincetti.vimoney.data.sqlite;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import ru.vincetti.vimoney.data.models.TransactionListModel;
import ru.vincetti.vimoney.data.models.TransactionModel;
import ru.vincetti.vimoney.data.models.TransactionStatDayModel;

import static ru.vincetti.vimoney.data.models.TransactionModel.TRANSACTION_TYPE_INCOME;
import static ru.vincetti.vimoney.data.models.TransactionModel.TRANSACTION_TYPE_SPENT;
import static ru.vincetti.vimoney.data.models.TransactionModel.TRANSACTION_TYPE_TRANSFER;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY id ASC")
    Single<List<TransactionModel>> loadAllTransactions();

    @Query("SELECT * FROM transactions WHERE transactions.system == 0 ORDER BY date DESC LIMIT :num")
    LiveData<List<TransactionModel>> loadAllTransactionsCount(int num);

    @Query("SELECT transactions.id, accounts.name AS account_name, currency.symbol AS account_symbol, " +
            "transactions.sum, transactions.type, transactions.date, transactions.description, " +
            "transactions.extra_key, transactions.extra_value " +
            "FROM transactions, accounts, currency " +
            "WHERE transactions.account_id == accounts.id " +
            "AND accounts.currency == currency.code " +
            "AND transactions.system == 0 " +
            "ORDER BY transactions.date DESC")
    LiveData<List<TransactionListModel>> loadAllTransactionsFull();

    @Query("SELECT transactions.id, accounts.name AS account_name, currency.symbol AS account_symbol, " +
            "transactions.sum, transactions.type, transactions.date, transactions.description, " +
            "transactions.extra_key, transactions.extra_value " +
            "FROM transactions, accounts, currency " +
            "WHERE transactions.account_id == accounts.id " +
            "AND accounts.currency == currency.code " +
            "AND transactions.system == 0 " +
            "ORDER BY transactions.date DESC LIMIT :num")
    LiveData<List<TransactionListModel>> loadAllTransactionsCountFull(int num);

    @Query("SELECT * FROM transactions WHERE account_id = :id AND transactions.system == 0 ORDER BY date DESC LIMIT :num")
    LiveData<List<TransactionModel>> loadCheckTransactionsCount(int id, int num);

    @Query("SELECT transactions.id, accounts.name AS account_name, currency.symbol AS account_symbol," +
            "transactions.sum, transactions.type, transactions.date, transactions.description, " +
            "transactions.extra_key, transactions.extra_value " +
            "FROM transactions, accounts, currency " +
            "WHERE transactions.account_id == accounts.id " +
            "AND accounts.currency == currency.code " +
            "AND ((transactions.account_id == :id" + " AND transactions.system == 0) " +
            "OR transactions.id IN (SELECT id from transactions WHERE extra_value IN(SELECT id from transactions WHERE account_id = :id AND system=1))) " +
            "ORDER BY transactions.date DESC")
    LiveData<List<TransactionListModel>> loadCheckTransactionsFull(int id);

    @Query("SELECT transactions.id, accounts.name AS account_name, currency.symbol AS account_symbol," +
            "transactions.sum, transactions.type, transactions.date, transactions.description, " +
            "transactions.extra_key, transactions.extra_value " +
            "FROM transactions, accounts, currency " +
            "WHERE transactions.account_id == accounts.id " +
            "AND accounts.currency == currency.code " +
            "AND ((transactions.account_id == :id" + " AND transactions.system == 0) " +
            "OR transactions.id IN (SELECT id from transactions WHERE extra_value IN(SELECT id from transactions WHERE account_id = :id AND system=1))) " +
            "ORDER BY transactions.date DESC LIMIT :num")
    LiveData<List<TransactionListModel>> loadCheckTransactionsCountFull(int id, int num);

    @Query("SELECT * FROM transactions WHERE id = :id")
    LiveData<TransactionModel> loadTransactionById(int id);

    @Query("SELECT SUM(sum) FROM transactions WHERE type =" + TRANSACTION_TYPE_INCOME
            + " AND strftime('%m', datetime(date/1000, 'unixepoch', 'localtime')) = :month"
            + " AND strftime('%Y', datetime(date/1000, 'unixepoch', 'localtime')) = :year"
            + " AND system=0")
    LiveData<Integer> loadSumTransactionIncomeMonth(String month, String year);

    @Query("SELECT SUM(sum) FROM transactions WHERE type =" + TRANSACTION_TYPE_INCOME
            + " AND strftime('%m', datetime(date/1000, 'unixepoch', 'localtime')) = :month"
            + " AND strftime('%Y', datetime(date/1000, 'unixepoch', 'localtime')) = :year"
            + " AND system=0")
    Single<Integer> loadSumTransactionIncomeMonthRx(String month, String year);

    @Query("SELECT SUM(sum) FROM transactions WHERE type =" + TRANSACTION_TYPE_SPENT
            + " AND strftime('%m', datetime(date/1000, 'unixepoch', 'localtime')) = :month"
            + " AND strftime('%Y', datetime(date/1000, 'unixepoch', 'localtime')) = :year")
    LiveData<Integer> loadSumTransactionExpenseMonth(String month, String year);

    @Query("SELECT SUM(sum) FROM transactions WHERE type =" + TRANSACTION_TYPE_SPENT
            + " AND strftime('%m', datetime(date/1000, 'unixepoch', 'localtime')) = :month"
            + " AND strftime('%Y', datetime(date/1000, 'unixepoch', 'localtime')) = :year")
    Single<Integer> loadSumTransactionExpenseMonthRx(String month, String year);

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
    Single<List<TransactionStatDayModel>> loadTransactionStatByMonth(String month, String year);

    @Query("SELECT IFNULL((Select SUM(sum) FROM transactions WHERE type=" + TRANSACTION_TYPE_INCOME + " AND account_id = :accId),0) - "
            + "IFNULL((SELECT SUM(sum) FROM transactions WHERE (type=" + TRANSACTION_TYPE_SPENT + " OR type=" + TRANSACTION_TYPE_TRANSFER + ") AND  account_id = :accId),0)")
    float loadSumByCheckId(int accId);

    @Query("SELECT SUM(sum) FROM transactions WHERE type=" + TRANSACTION_TYPE_INCOME + " AND account_id = :accId")
    float loadSumIncomeByCheckId(int accId);

    @Query("SELECT SUM(sum) FROM transactions WHERE (type=" + TRANSACTION_TYPE_SPENT
            + " OR type=" + TRANSACTION_TYPE_TRANSFER + ")"
            + " AND  account_id = :accId")
    float loadSumExpenseByCheckId(int accId);

    @Query("SELECT account_id FROM transactions WHERE id = :id")
    int getAccountTransactionById(int id);

    @Query("DELETE FROM transactions WHERE id = :transId")
    void deleteTransactionById(int transId);

    @Query("DELETE FROM transactions")
    Completable deleteAllTransactions();

    @Insert
    long insertTransaction(TransactionModel t);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTransaction(TransactionModel t);
}
