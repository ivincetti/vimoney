package ru.vincetti.vimoney;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.sql.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.JsonDownloader;
import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.models.ConfigModel;
import ru.vincetti.vimoney.data.models.CurrencyModel;
import ru.vincetti.vimoney.data.models.TransactionModel;
import ru.vincetti.vimoney.data.models.json.AccountsItem;
import ru.vincetti.vimoney.data.models.json.ConfigFile;
import ru.vincetti.vimoney.data.models.json.CurrencyItem;
import ru.vincetti.vimoney.data.models.json.TransactionsItem;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;
import ru.vincetti.vimoney.data.sqlite.VimonContract;
import ru.vincetti.vimoney.home.HomeActivity;

import static ru.vincetti.vimoney.data.sqlite.VimonContract.ConfigEntry.CONFIG_KEY_NAME_USER_NAME;
import static ru.vincetti.vimoney.utils.LogicMath.accountBalanceUpdateById;

public class SplashActivity extends AppCompatActivity {
    private final String LOG_TAG = "SPLASH_DEBUG";

    private JsonDownloader jsonDownloader;
    private AppDatabase mDb;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;

        mDb = AppDatabase.getInstance(this);
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
                if (response.body() != null) {
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
                    configDbDateInsert(timeMillisLong);
                    // user info to base
                    userUpdate(response.body().getUser().getName());
                    // accounts info to base
                    accountsUpdate(response.body().getAccounts());
                    currencyImport(response.body().getCurrency());
                    transactionsImport(response.body().getTransactions());
                } else {
                    if (Long.valueOf(config.getValue()) < timeMillisLong) {
                        configDbDateUpdate(response.body().getDateEdit(), config.getId());
                        // user info to base
                        userUpdate(response.body().getUser().getName());
                    }
                }
            }
        });
    }

    private void accountsUpdate(List<AccountsItem> accountsItems) {
        for (AccountsItem acc : accountsItems) {
            accountUpdate(acc.getId(),
                    acc.getType(),
                    acc.getTitle(),
                    acc.getInstrument(),
                    acc.getBalance());
        }
    }

    // import currency from config
    private void currencyImport(List<CurrencyItem> currencyItems) {
        for (CurrencyItem cur : currencyItems) {
            currencyUpdate(cur.getName(),
                    cur.getCode(),
                    cur.getSymbol());
        }
    }

    // import sample transactions from config
    private void transactionsImport(List<TransactionsItem> transactionItems) {
        for (TransactionsItem tr : transactionItems) {
            transactionImport(tr.getDate(),
                    tr.getAccountId(),
                    tr.getDescription(),
                    tr.getType(),
                    tr.getSum());
        }
    }

    // insert new date edit in config DB table
    private void configDbDateInsert(long timeMillisLong) {
        ConfigModel newConfig = new ConfigModel(VimonContract.ConfigEntry.CONFIG_KEY_NAME_DATE_EDIT,
                String.valueOf(timeMillisLong));
        AppExecutors.getsInstance().diskIO().execute(
                () -> mDb.configDao().insertConfig(newConfig));
    }

    // update new date edit in config DB table
    private void configDbDateUpdate(long timeMillisLong, int id) {
        ConfigModel newConfig = new ConfigModel(id,
                VimonContract.ConfigEntry.CONFIG_KEY_NAME_DATE_EDIT,
                String.valueOf(timeMillisLong));
        AppExecutors.getsInstance().diskIO().execute(
                () -> mDb.configDao().updateConfig(newConfig));
    }

    public void userUpdate(String user) {
        LiveData<ConfigModel> mUser = mDb.configDao().loadConfigByKey(CONFIG_KEY_NAME_USER_NAME);
        mUser.observe(this, new Observer<ConfigModel>() {
            @Override
            public void onChanged(ConfigModel configModel) {
                ConfigModel newUser = new ConfigModel(CONFIG_KEY_NAME_USER_NAME, user);
                mUser.removeObserver(this);
                if (configModel == null) {
                    AppExecutors.getsInstance().diskIO().execute(
                            () -> mDb.configDao().insertConfig(newUser));
                } else {
                    newUser.setId(configModel.getId());
                    AppExecutors.getsInstance().diskIO().execute(
                            () -> mDb.configDao().updateConfig(newUser));
                }
            }
        });
    }

    public void accountUpdate(int accId, String type, String title, int ins, int balance) {
        AccountModel newAcc = new AccountModel(accId, title, type, balance, 810);
        LiveData<AccountModel> tmpAcc = mDb.accountDao().loadAccountById(accId);
        tmpAcc.observe(this, new Observer<AccountModel>() {
            @Override
            public void onChanged(AccountModel accountModel) {
                tmpAcc.removeObserver(this);
                if (accountModel == null) {
                    AppExecutors.getsInstance().diskIO().execute(
                            () -> mDb.accountDao().insertAccount(newAcc));
                } else {
                    newAcc.setId(accountModel.getId());
                    AppExecutors.getsInstance().diskIO().execute(
                            () -> mDb.accountDao().updateAccount(newAcc));
                }
                accountBalanceUpdateById(mContext, accId);
            }
        });
    }

    public void currencyUpdate(String currencyName, int currencyCode, String symbol) {
        CurrencyModel newCurrency = new CurrencyModel(currencyCode, currencyName, symbol);
        AppExecutors.getsInstance().diskIO().execute(
                () -> mDb.currentDao().insertCurrency(newCurrency));
    }

    public void transactionImport(long date, int accId, String desc, int trType, float sum) {
        TransactionModel newTr = new TransactionModel(new Date(date), accId, desc, trType, sum);
        AppExecutors.getsInstance().diskIO().execute(
                () -> mDb.transactionDao().insertTransaction(newTr));
    }
}
