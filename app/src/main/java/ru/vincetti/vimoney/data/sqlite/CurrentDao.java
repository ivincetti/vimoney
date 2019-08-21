package ru.vincetti.vimoney.data.sqlite;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.vincetti.vimoney.data.models.CurrencyModel;

@Dao
public interface CurrentDao {

    @Query("Select * from currency ORDER BY id ASC")
    LiveData<List<CurrencyModel>> loadAllCurrency();

    @Insert
    void insertCurrency(CurrencyModel cur);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCurrency(CurrencyModel cur);

    @Delete
    void deleteCurrency(CurrencyModel cur);
}
