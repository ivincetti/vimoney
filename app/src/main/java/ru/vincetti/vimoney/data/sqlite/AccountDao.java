package ru.vincetti.vimoney.data.sqlite;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.vincetti.vimoney.data.models.AccountListModel;
import ru.vincetti.vimoney.data.models.AccountModel;

@Dao
public interface AccountDao {

    @Query("SELECT * FROM accounts ORDER BY archive ASC, name ASC")
    LiveData<List<AccountModel>> loadAllAccounts();

    @Query("SELECT accounts.id, accounts.name, currency.symbol AS account_symbol, " +
            "accounts.sum, accounts.type, accounts.archive " +
            "FROM accounts, currency " +
            "WHERE accounts.currency == currency.code " +
            "ORDER BY accounts.archive ASC, accounts.name ASC")
    LiveData<List<AccountListModel>> loadAllAccountsFull();

    @Query("SELECT * FROM accounts WHERE archive = 0 ORDER BY name ASC")
    LiveData<List<AccountModel>> loadNotArhiveAccounts();

    @Query("SELECT accounts.id, accounts.name, currency.symbol AS account_symbol, " +
            "accounts.sum, accounts.type, accounts.archive " +
            "FROM accounts, currency " +
            "WHERE accounts.currency == currency.code AND  accounts.archive = 0 " +
            "ORDER BY accounts.name ASC")
    LiveData<List<AccountListModel>> loadNotArhiveAccountsFull();

    @Query("SELECT * FROM accounts WHERE id = :accId")
    LiveData<AccountModel> loadAccountById(int accId);

    @Query("SELECT accounts.id, accounts.name, currency.symbol AS account_symbol, " +
            "accounts.sum, accounts.type, accounts.archive " +
            "FROM accounts, currency " +
            "WHERE accounts.currency == currency.code " +
            "AND  accounts.id = :accId " +
            "ORDER BY accounts.name ASC")
    LiveData<AccountListModel> loadAccountByIdFull(int accId);

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
