package ru.vincetti.vimoney.data.sqlite;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import ru.vincetti.vimoney.data.DateConverter;
import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.models.ConfigModel;
import ru.vincetti.vimoney.data.models.TransactionModel;

@Database(entities = {AccountModel.class, TransactionModel.class, ConfigModel.class},
        version = 2, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, VimonContract.DB_NAME)
                        .addMigrations(AppDatabase.MIGRATION_1_2)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract AccountDao accountDao();

    public abstract TransactionDao transactionDao();

    public abstract ConfigDao configDao();

    // delete acc_id column
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(final SupportSQLiteDatabase db) {
            db.execSQL("BEGIN TRANSACTION");
            db.execSQL("CREATE TABLE acc_backup(id INTEGER PRIMARY KEY NOT NULL, name TEXT, type TEXT, sum INTEGER NOT NULL)");
            db.execSQL("INSERT INTO acc_backup (id, name, type, sum) SELECT id, name, type, sum FROM accounts");
            db.execSQL("DROP TABLE accounts");
            db.execSQL("ALTER TABLE acc_backup RENAME TO accounts");
            db.execSQL("COMMIT");
        }
    };
}
