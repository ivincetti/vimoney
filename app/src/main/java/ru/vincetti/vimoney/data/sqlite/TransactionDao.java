package ru.vincetti.vimoney.data.sqlite;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.vincetti.vimoney.data.models.TransactionModel;

import static ru.vincetti.vimoney.data.models.TransactionModel.TRANSACTION_TYPE_INCOME;
import static ru.vincetti.vimoney.data.models.TransactionModel.TRANSACTION_TYPE_SPENT;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY updated_at DESC")
    LiveData<List<TransactionModel>> loadAllTransactions();

    @Query("SELECT * FROM transactions ORDER BY updated_at DESC LIMIT :num")
    LiveData<List<TransactionModel>> loadAllTransactionsCount(int num);

    @Query("SELECT * FROM transactions WHERE account_id = :id ORDER BY updated_at DESC LIMIT :num")
    LiveData<List<TransactionModel>> loadCheckTransactionsCount(int id, int num);

    @Query("SELECT * FROM transactions WHERE id = :id")
    LiveData<TransactionModel> loadTransactionById(int id);

    @Query("SELECT SUM(sum) FROM transactions WHERE type=" + TRANSACTION_TYPE_INCOME + " AND account_id = :accId")
    float loadSumIncomeByCheckId(int accId);

    @Query("SELECT SUM(sum) FROM transactions WHERE type=" + TRANSACTION_TYPE_SPENT + " AND  account_id = :accId")
    float loadSumExpenseByCheckId(int accId);

    @Insert
    void insertTransaction(TransactionModel t);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTransaction(TransactionModel t);

    @Delete
    void deleteTransaction(TransactionModel t);
}