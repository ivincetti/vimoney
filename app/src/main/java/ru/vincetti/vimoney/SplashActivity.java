package ru.vincetti.vimoney;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.vincetti.vimoney.data.AccountsItem;
import ru.vincetti.vimoney.data.ConfigFile;
import ru.vincetti.vimoney.data.JsonDownloader;
import ru.vincetti.vimoney.data.sqlite.DbHelper;
import ru.vincetti.vimoney.data.sqlite.VimonContract;
import ru.vincetti.vimoney.home.HomeActivity;

public class SplashActivity extends AppCompatActivity {
    private final String LOG_TAG = "SPLASH_DEBUG";

    private ProgressBar progress;
    private JsonDownloader jsonDownloader;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progress = findViewById(R.id.splash_content_progressbar);

        db = new DbHelper(this).getWritableDatabase();
        retrofitInit();
        loadJson();
    }

    private void retrofitInit() {
        Retrofit json = new Retrofit.Builder()
                .baseUrl("https://vincetti.ru/vimoney/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonDownloader = json.create(JsonDownloader.class);
    }

    private void loadJson() {
        jsonDownloader.loadPreferences("Ru").enqueue(new Callback<ConfigFile>() {
            @Override
            public void onResponse(Call<ConfigFile> call, Response<ConfigFile> response) {
                Log.d(LOG_TAG, "Json Download complete");
                // user info to base
                int userId = response.body().getUser().getId();
                String userName = response.body().getUser().getName();
                userUpdate(userId, userName);

                // accounts info to base
                List<AccountsItem> accountsItems = response.body().getAccounts();
                for (AccountsItem acc : accountsItems) {
                    accountUpdate(acc.getId(),
                            acc.getType(),
                            acc.getTitle(),
                            acc.getInstrument(),
                            acc.getBalance());
                }

                HomeActivity.start(getApplicationContext());
            }

            @Override
            public void onFailure(Call<ConfigFile> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, t.getMessage());
            }
        });
    }

    public void userUpdate(int id, String user) {
        ContentValues userCV = new ContentValues();
        userCV.put(VimonContract.UserEntry.COLUMN_USER_ID, id);
        userCV.put(VimonContract.UserEntry.COLUMN_NAME, user);

        Cursor userCursor = db.query(VimonContract.UserEntry.TABLE_NAME, null,
                VimonContract.UserEntry.COLUMN_USER_ID + " = " + id,
                null, null,
                null, null);
        try {
            Log.d(LOG_TAG, "User Cursor init");
            if (userCursor.getCount() > 0) {
                Log.d(LOG_TAG, "user update");
                db.update(VimonContract.UserEntry.TABLE_NAME,
                        userCV,
                        VimonContract.UserEntry.COLUMN_USER_ID + " = " + id,
                        null);
            } else {
                Log.d(LOG_TAG, "user insert");
                db.insert(VimonContract.UserEntry.TABLE_NAME, null, userCV);
            }
        } finally {
            userCursor.close();
        }
    }


    public void accountUpdate(int id, String type, String title, int ins, int balance) {
        ContentValues accountCV = new ContentValues();
        accountCV.put(VimonContract.AccountsEntry.COLUMN_ACCOUNT_ID, id);
        accountCV.put(VimonContract.AccountsEntry.COLUMN_TYPE, type);
        accountCV.put(VimonContract.AccountsEntry.COLUMN_TITLE, title);
        accountCV.put(VimonContract.AccountsEntry.COLUMN_INSTRUMENT, ins);
        accountCV.put(VimonContract.AccountsEntry.COLUMN_BALANCE, balance);

        Cursor userCursor = db.query(VimonContract.AccountsEntry.TABLE_NAME, null,
                VimonContract.AccountsEntry.COLUMN_ACCOUNT_ID + " = " + id,
                null, null,
                null, null);
        try {
            Log.d(LOG_TAG, "Account Cursor init " + id);
            if (userCursor.getCount() > 0) {
                Log.d(LOG_TAG, "Account update " + id);
                db.update(VimonContract.AccountsEntry.TABLE_NAME,
                        accountCV,
                        VimonContract.AccountsEntry.COLUMN_ACCOUNT_ID + " = " + id,
                        null);
            } else {
                Log.d(LOG_TAG, "Account insert " + id);
                db.insert(VimonContract.AccountsEntry.TABLE_NAME, null, accountCV);
            }
        } finally {
            userCursor.close();
        }
    }
}
