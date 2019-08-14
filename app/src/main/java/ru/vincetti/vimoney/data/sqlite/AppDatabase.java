package ru.vincetti.vimoney.data.sqlite;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ru.vincetti.vimoney.data.DateConverter;
import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.models.ConfigModel;
import ru.vincetti.vimoney.data.models.TransactionModel;

@Database(entities = {AccountModel.class, TransactionModel.class, ConfigModel.class},
        version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, VimonContract.DB_NAME)
                        // TEMP
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return sInstance;
    }

    public abstract AccountDao accountDao();
    public abstract TransactionDao transactionDao();
    public abstract ConfigDao configDao();
}
