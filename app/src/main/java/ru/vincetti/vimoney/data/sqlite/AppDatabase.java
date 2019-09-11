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
import ru.vincetti.vimoney.data.models.CurrencyModel;
import ru.vincetti.vimoney.data.models.TransactionModel;

@Database(entities = {
        AccountModel.class,
        TransactionModel.class,
        ConfigModel.class,
        CurrencyModel.class},
        version = 8, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, VimonContract.DB_NAME)
                        .addMigrations(AppDatabase.MIGRATION_1_2,
                                AppDatabase.MIGRATION_2_3,
                                AppDatabase.MIGRATION_3_4,
                                AppDatabase.MIGRATION_4_5,
                                AppDatabase.MIGRATION_5_6,
                                AppDatabase.MIGRATION_6_7,
                                AppDatabase.MIGRATION_7_8
                        )
                        .build();
            }
        }
        return sInstance;
    }

    public abstract AccountDao accountDao();

    public abstract TransactionDao transactionDao();

    public abstract ConfigDao configDao();

    public abstract CurrentDao currentDao();

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

    // add archive account column
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(final SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE accounts ADD COLUMN archive INTEGER DEFAULT 0 NOT NULL");
        }
    };

    // add account columns: extrakey and extravalue, currency
    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(final SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE accounts ADD COLUMN currency INTEGER DEFAULT 0 NOT NULL");
            db.execSQL("ALTER TABLE accounts ADD COLUMN extra_key TEXT DEFAULT '' NOT NULL");
            db.execSQL("ALTER TABLE accounts ADD COLUMN extra_value TEXT DEFAULT '' NOT NULL");
            db.execSQL("UPDATE accounts SET currency = 810");
            db.execSQL("CREATE TABLE currency(id INTEGER PRIMARY KEY NOT NULL, code INTEGER NOT NULL, name TEXT)");
        }
    };

    // add currency symbol column
    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(final SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE currency ADD COLUMN symbol TEXT DEFAULT '' NOT NULL");
        }
    };

    // add transaction columns: extrakey and extravalue
    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(final SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE transactions ADD COLUMN extra_key TEXT DEFAULT '' NOT NULL");
            db.execSQL("ALTER TABLE transactions ADD COLUMN extra_value TEXT DEFAULT '' NOT NULL");
        }
    };

    // add transaction columns: system and deleted
    private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(final SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE transactions ADD COLUMN system INTEGER DEFAULT 0 NOT NULL");
            db.execSQL("ALTER TABLE transactions ADD COLUMN deleted INTEGER DEFAULT 0 NOT NULL");
        }
    };

    // add transaction columns: system and deleted
    private static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(final SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE accounts ADD COLUMN color TEXT DEFAULT '' NOT NULL");
            db.execSQL("UPDATE accounts SET color=\"#164fc6\"");
        }
    };
}
