package ru.vincetti.vimoney.data.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static String DB_Name = "user.db";
    private static int DB_VERSION = 1;

    private static final String USER_CREATE_QUERY = ""
            + "CREATE TABLE " + VimonContract.UserEntry.TABLE_NAME + " ( "
            + VimonContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VimonContract.UserEntry.COLUMN_USER_ID + " INTEGER, "
            + VimonContract.UserEntry.COLUMN_NAME + " TEXT "
            + ");";

    private static final String ACCOUNTS_CREATE_QUERY = ""
            + "CREATE TABLE " + VimonContract.AccountsEntry.TABLE_NAME + " ( "
            + VimonContract.AccountsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VimonContract.AccountsEntry.COLUMN_ACCOUNT_ID + " INTEGER, "
            + VimonContract.AccountsEntry.COLUMN_TYPE + " TEXT, "
            + VimonContract.AccountsEntry.COLUMN_TITLE + " TEXT, "
            + VimonContract.AccountsEntry.COLUMN_INSTRUMENT + " INTEGER, "
            + VimonContract.AccountsEntry.COLUMN_BALANCE + " INTEGER "
            + ");";


    public DbHelper(Context context) {
        super(context, DB_Name, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(USER_CREATE_QUERY);
        sqLiteDatabase.execSQL(ACCOUNTS_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //do nothing
    }
}
