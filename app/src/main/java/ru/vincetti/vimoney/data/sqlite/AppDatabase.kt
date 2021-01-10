package ru.vincetti.vimoney.data.sqlite

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.vincetti.vimoney.data.DateConverter
import ru.vincetti.vimoney.data.models.*

@Database(
    entities = [
        AccountModel::class,
        TransactionModel::class,
        ConfigModel::class,
        CurrencyModel::class,
        CategoryModel::class
    ],
    version = 13,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun configDao(): ConfigDao
    abstract fun currentDao(): CurrentDao
    abstract fun categoryDao(): CategoryDao

    companion object {

        private const val DB_NAME = "vimoney.db"

        fun newInstance(applicationContext: Context): AppDatabase {
            return Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                DB_NAME
            ).addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
                MIGRATION_5_6,
                MIGRATION_6_7,
                MIGRATION_7_8,
                MIGRATION_8_9,
                MIGRATION_9_10,
                MIGRATION_10_11,
                MIGRATION_11_12,
                MIGRATION_12_13
            ).build()
        }

        /** Check: delete acc_id. */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("BEGIN TRANSACTION")
                db.execSQL(
                    """CREATE TABLE acc_backup(
                        id INTEGER PRIMARY KEY NOT NULL,
                        name TEXT,
                        type TEXT,
                        sum INTEGER NOT NULL
                        )"""
                )
                db.execSQL(
                    "INSERT INTO acc_backup (id, name, type, sum) SELECT id, name, type, sum FROM accounts"
                )
                db.execSQL("DROP TABLE accounts")
                db.execSQL("ALTER TABLE acc_backup RENAME TO accounts")
                db.execSQL("COMMIT")
            }
        }

        /** Check: archive. */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE accounts ADD COLUMN archive INTEGER DEFAULT 0 NOT NULL")
            }
        }

        /** Check: extra key and extra value. +Currency. */
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE accounts ADD COLUMN currency INTEGER DEFAULT 0 NOT NULL")
                db.execSQL("ALTER TABLE accounts ADD COLUMN extra_key TEXT DEFAULT '' NOT NULL")
                db.execSQL("ALTER TABLE accounts ADD COLUMN extra_value TEXT DEFAULT '' NOT NULL")
                db.execSQL("UPDATE accounts SET currency = 810")
                db.execSQL("CREATE TABLE currency(id INTEGER PRIMARY KEY NOT NULL, code INTEGER NOT NULL, name TEXT)")
            }
        }

        /** Currency: symbol. */
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE currency ADD COLUMN symbol TEXT DEFAULT '' NOT NULL")
            }
        }

        /** Transactions: extra key and extra value. */
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE transactions ADD COLUMN extra_key TEXT DEFAULT '' NOT NULL")
                db.execSQL("ALTER TABLE transactions ADD COLUMN extra_value TEXT DEFAULT '' NOT NULL")
            }
        }

        /** Transactions: system and deleted. */
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE transactions ADD COLUMN system INTEGER DEFAULT 0 NOT NULL")
                db.execSQL("ALTER TABLE transactions ADD COLUMN deleted INTEGER DEFAULT 0 NOT NULL")
            }
        }

        /** Check: color. */
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE accounts ADD COLUMN color TEXT DEFAULT '' NOT NULL")
                db.execSQL("UPDATE accounts SET color=\"#164fc6\"")
            }
        }

        /** Check: include in all balance. */
        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE accounts ADD COLUMN need_all_balance INTEGER DEFAULT 1 NOT NULL")
            }
        }

        /** Category option. */
        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE transactions ADD COLUMN category_id INTEGER DEFAULT 0 NOT NULL")
                db.execSQL(
                    """CREATE TABLE category(
                        id INTEGER PRIMARY KEY NOT NULL, 
                        name TEXT NOT NULL, 
                        symbol TEXT NOT NULL
                        )"""
                )
            }
        }

        /** Transactions: default category. */
        private val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("UPDATE transactions SET category_id = 1")
                db.execSQL("DELETE from category")
                db.execSQL("INSERT INTO category (id, name, symbol) VALUES(1,\'?\',\'?\')")
            }
        }

        /** Default category fix. */
        private val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("UPDATE transactions SET category_id = 1 WHERE category_id = 0")
            }
        }

        /** Check: show on main screen. */
        private val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE accounts ADD COLUMN need_on_main_screen INTEGER DEFAULT 1 NOT NULL")
            }
        }
    }
}
