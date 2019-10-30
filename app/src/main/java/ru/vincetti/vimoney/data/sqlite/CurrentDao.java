package ru.vincetti.vimoney.data.sqlite;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ru.vincetti.vimoney.data.models.CurrencyModel;

@Dao
public interface CurrentDao {
    @Query("Select * from currency ORDER BY id ASC")
    LiveData<List<CurrencyModel>> loadAllCurrency();

    @Query("Select * from currency WHERE code = :code LIMIT 1")
    LiveData<CurrencyModel> loadCurrencyByCode(int code);

    @Insert
    void insertCurrency(CurrencyModel cur);
}
