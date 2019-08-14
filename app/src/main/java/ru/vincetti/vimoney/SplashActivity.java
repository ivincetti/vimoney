package ru.vincetti.vimoney;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.vincetti.vimoney.data.JsonDownloader;
import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.models.ConfigModel;
import ru.vincetti.vimoney.data.models.json.AccountsItem;
import ru.vincetti.vimoney.data.models.json.ConfigFile;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;
import ru.vincetti.vimoney.data.sqlite.VimonContract;
import ru.vincetti.vimoney.home.HomeActivity;
import ru.vincetti.vimoney.utils.TransactionsGenerator;

import static ru.vincetti.vimoney.data.sqlite.VimonContract.ConfigEntry.CONFIG_KEY_NAME_USER_NAME;

public class SplashActivity extends AppCompatActivity {
    private final String LOG_TAG = "SPLASH_DEBUG";

    private ProgressBar progress;
    private JsonDownloader jsonDownloader;
    private AppDatabase mDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progress = findViewById(R.id.splash_content_progressbar);

        Log.d("DEBUG", "Перед созданием БД");
        mDb = AppDatabase.getInstance(this);
        Log.d("DEBUG", "После создания БД");
        retrofitInit();
        Log.d("DEBUG", "Инициализация retrofit");
        loadJson();
        Log.d("DEBUG", "Началась загрузка конфига");
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
                Log.d("DEBUG", "Получение конфига");
                if (response.body() != null) {
                    Log.d("DEBUG", "Начинаем сохранение конфига");
                    configDbUpdate(response.body().getDateEdit(), response);
                    HomeActivity.start(getApplicationContext());
                }
            }

            @Override
            public void onFailure(Call<ConfigFile> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, t.getMessage());
            }
        });
    }

    public void configDbUpdate(long timeMillisLong, Response<ConfigFile> response) {
        LiveData<ConfigModel> tmpConfig = mDb.configDao()
                .loadConfigByKey(VimonContract.ConfigEntry.CONFIG_KEY_NAME_DATE_EDIT);

        tmpConfig.observe(this, new Observer<ConfigModel>() {
            @Override
            public void onChanged(ConfigModel config) {
                tmpConfig.removeObserver(this);
                if (config == null) {
                    Log.d("DEBUG", "данных нет - insert");
                    configDbDateInsert(timeMillisLong);
                    TransactionsGenerator.generate(getApplicationContext());

                    // user info to base
                    Log.d("DEBUG", "Обновление пользователя");
                    userUpdate(response.body().getUser().getName());

                    // accounts info to base
                    Log.d("DEBUG", "Обновление счетов");
                    accountsUpdate(response);
                } else {
                    if(Long.valueOf(config.getValue()) < timeMillisLong){
                        Log.d("DEBUG", "данные есть - требуется обновление, date " + config.getValue());
                        configDbDateUpdate(response.body().getDateEdit(), config.getId());

                        // user info to base
                        Log.d("DEBUG", "Обновление пользователя");
                        userUpdate(response.body().getUser().getName());

                        // accounts info to base
                        Log.d("DEBUG", "Обновление счетов");
                        accountsUpdate(response);
                    } else {
                        Log.d("DEBUG", "данные есть - обновление не требуется");
                    }
                }
            }
        });
    }

    private void accountsUpdate(Response<ConfigFile> response){
        Log.d("DEBUG", "Обновление счетов");
        List<AccountsItem> accountsItems = response.body().getAccounts();

        for (AccountsItem acc : accountsItems) {
            accountUpdate(acc.getId(),
                    acc.getType(),
                    acc.getTitle(),
                    acc.getInstrument(),
                    acc.getBalance());
        }
    }

    // insert new date edit in config DB table
    private void configDbDateInsert(long timeMillisLong) {
        ConfigModel newConfig = new ConfigModel(VimonContract.ConfigEntry.CONFIG_KEY_NAME_DATE_EDIT,
                String.valueOf(timeMillisLong));

        mDb.configDao().insertConfig(newConfig);
    }

    // update new date edit in config DB table
    private void configDbDateUpdate(long timeMillisLong, int id) {
        ConfigModel newConfig = new ConfigModel(id,
                VimonContract.ConfigEntry.CONFIG_KEY_NAME_DATE_EDIT,
                String.valueOf(timeMillisLong));

        mDb.configDao().updateConfig(newConfig);
    }

    public void userUpdate(String user) {
        LiveData<ConfigModel> mUser = mDb.configDao().loadConfigByKey(CONFIG_KEY_NAME_USER_NAME);
        mUser.observe(this, new Observer<ConfigModel>() {
            @Override
            public void onChanged(ConfigModel configModel) {
                ConfigModel newUser = new ConfigModel(CONFIG_KEY_NAME_USER_NAME, user);
                mUser.removeObserver(this);
                if (configModel == null) {
                    mDb.configDao().insertConfig(newUser);
                } else {
                    newUser.setId(configModel.getId());
                    mDb.configDao().updateConfig(newUser);
                }
            }
        });
    }

    public void accountUpdate(int accId, String type, String title, int ins, int balance) {
        AccountModel newAcc = new AccountModel(accId, title, type, balance);
        LiveData<AccountModel> tmpAcc = mDb.accountDao().loadAccountByAccId(accId);
        tmpAcc.observe(this, new Observer<AccountModel>() {
            @Override
            public void onChanged(AccountModel accountModel) {
                tmpAcc.removeObserver(this);
                if(accountModel == null){
                    mDb.accountDao().insertAccount(newAcc);
                } else {
                    newAcc.setId(accountModel.getId());
                    mDb.accountDao().updateAccount(newAcc);
                }
            }
        });
    }
}
