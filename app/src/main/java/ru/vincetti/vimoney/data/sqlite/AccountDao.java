package ru.vincetti.vimoney.data.sqlite;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.vincetti.vimoney.data.models.AccountModel;

@Dao
public interface AccountDao {

    @Query("SELECT * FROM accounts")
    LiveData<List<AccountModel>> loadAllAccounts();

    @Query("SELECT * FROM accounts WHERE id = :id")
    LiveData<AccountModel> loadAccountById(int id);

    @Query("SELECT * FROM accounts WHERE acc_id = :id")
    LiveData<AccountModel> loadAccountByAccId(int id);

    @Query("UPDATE accounts SET sum = :sum WHERE id = :acc_id")
    int updateSumByAccId(int acc_id, float sum);

    @Insert
    void insertAccount(AccountModel acc);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAccount(AccountModel acc);

    @Delete
    void deleteAccount(AccountModel acc);

}
