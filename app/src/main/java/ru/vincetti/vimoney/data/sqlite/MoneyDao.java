package ru.vincetti.vimoney.data.sqlite;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MoneyDao {

    @Query("SELECT * FROM transactions ORDER BY updated_at DESC")
    LiveData<List<Transaction>> loadAllTransactions();

    @Query("SELECT * FROM transactions WHERE id = :id")
    LiveData<Transaction> loadTransactionById(int id);

    @Insert
    void insertTransaction(Transaction t);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTransaction(Transaction t);

    @Delete
    void deleteTransaction(Transaction t);

}
