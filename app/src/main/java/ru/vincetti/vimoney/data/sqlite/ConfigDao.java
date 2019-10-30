package ru.vincetti.vimoney.data.sqlite;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.vincetti.vimoney.data.models.ConfigModel;

@Dao
public interface ConfigDao {
    @Query("SELECT * FROM config")
    LiveData<List<ConfigModel>> loadConfigs();

    @Query("SELECT * FROM config WHERE key_name = :key")
    LiveData<ConfigModel> loadConfigByKey(String key);

    @Insert
    void insertConfig(ConfigModel conf);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateConfig(ConfigModel conf);
}
