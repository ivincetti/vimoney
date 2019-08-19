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

    @Query("SELECT * FROM accounts WHERE id = :accId")
    LiveData<AccountModel> loadAccountById(int accId);

    @Query("UPDATE accounts SET sum = :sum WHERE id = :accId")
    int updateSumByAccId(int accId, float sum);

    @Query("DELETE FROM accounts WHERE id = :accId")
    void deleteAccountById(int accId);

    @Insert
    void insertAccount(AccountModel acc);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAccount(AccountModel acc);

    @Delete
    void deleteAccount(AccountModel acc);

}
