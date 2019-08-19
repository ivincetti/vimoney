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

    @Query("SELECT * FROM accounts ORDER BY archive ASC")
    LiveData<List<AccountModel>> loadAllAccounts();

    @Query("SELECT * FROM accounts WHERE archive = 0")
    LiveData<List<AccountModel>> loadNotArhiveAccounts();

    @Query("SELECT * FROM accounts WHERE id = :accId")
    LiveData<AccountModel> loadAccountById(int accId);

    @Query("UPDATE accounts SET sum = :sum WHERE id = :accId")
    int updateSumByAccId(int accId, float sum);

    @Query("UPDATE accounts SET archive = 1 WHERE id = :accId")
    void archiveAccountById(int accId);

    @Query("UPDATE accounts SET archive = 0 WHERE id = :accId")
    void fromArchiveAccountById(int accId);

    @Insert
    void insertAccount(AccountModel acc);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAccount(AccountModel acc);

    @Delete
    void deleteAccount(AccountModel acc);

}
