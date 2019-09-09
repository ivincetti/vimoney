package ru.vincetti.vimoney;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
import ru.vincetti.vimoney.transaction.TransactionViewModel;

import static ru.vincetti.vimoney.data.sqlite.VimonContract.ConfigEntry.CONFIG_KEY_NAME_USER_NAME;
import static ru.vincetti.vimoney.utils.LogicMath.accountBalanceUpdateById;
import static ru.vincetti.vimoney.utils.Utils.viewModelUpdate;

public class SplashActivity extends AppCompatActivity {
    private final String LOG_TAG = "SPLASH_DEBUG";

    private JsonDownloader jsonDownloader;
    private AppDatabase mDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mDb = AppDatabase.getInstance(this);

        LiveData<ConfigModel> tmpConfig = mDb.configDao()
                .loadConfigByKey(VimonContract.ConfigEntry.CONFIG_KEY_NAME_DATE_EDIT);
        tmpConfig.observe(this, new Observer<ConfigModel>() {
            @Override
            public void onChanged(ConfigModel config) {
                tmpConfig.removeObserver(this);
                if (config == null) {
                    ConnectivityManager cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo nInfo = cManager.getActiveNetworkInfo();
                    if (nInfo == null || !nInfo.isConnected()) {
                        alertNetworkDialogShow();
                    } else {
                        retrofitInit();
                        loadJson();
                        HomeActivity.start(getApplicationContext());
                    }
                } else {
                    HomeActivity.start(getApplicationContext());
                }
            }
        });

        // setting in viewmodel Utils hashes
        final TransactionViewModel viewModel =
                ViewModelProviders.of(this).get(TransactionViewModel.class);
        AppExecutors.getsInstance().diskIO().execute(() -> viewModelUpdate(mDb, viewModel));
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
                }
            }

            @Override
            public void onFailure(Call<ConfigFile> call, Throwable t) {
                alertNetworkDialogShow();
                Log.d(LOG_TAG, t.getMessage());
            }
        });
    }

    public void configDbUpdate(long timeMillisLong, Response<ConfigFile> response) {
        configDbDateInsert(timeMillisLong);
        // user info to base
        userUpdate(response.body().getUser().getName());
        // accounts info to base
        transactionsImport(response.body().getTransactions());
        currencyImport(response.body().getCurrency());
        accountsUpdate(response.body().getAccounts());
    }

    private void accountsUpdate(List<AccountsItem> accountsItems) {
        AppExecutors.getsInstance().diskIO().execute(() -> {
            for (AccountsItem acc : accountsItems) {
                accountUpdate(acc.getId(),
                        acc.getType(),
                        acc.getTitle(),
                        acc.getInstrument(),
                        acc.getBalance());
            }
        });
    }

    // import currency from config
    private void currencyImport(List<CurrencyItem> currencyItems) {
        AppExecutors.getsInstance().diskIO().execute(() -> {
            for (CurrencyItem cur : currencyItems) {
                mDb.currentDao().insertCurrency(
                        new CurrencyModel(cur.getCode(), cur.getName(), cur.getSymbol()));
            }
        });

    }

    // import sample transactions from config
    private void transactionsImport(List<TransactionsItem> transactionItems) {
        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                for (TransactionsItem tr : transactionItems) {
                    mDb.transactionDao().insertTransaction(
                            new TransactionModel(
                                    new Date(tr.getDate()),
                                    tr.getAccountId(),
                                    getResources().getString(R.string.transaction_import_sample_desc),
                                    tr.getType(),
                                    tr.getSum()));
                }
            }
        });
    }

    // insert new date edit in config DB table
    private void configDbDateInsert(long timeMillisLong) {
        ConfigModel newConfig = new ConfigModel(VimonContract.ConfigEntry.CONFIG_KEY_NAME_DATE_EDIT,
                String.valueOf(timeMillisLong));
        AppExecutors.getsInstance().diskIO().execute(
                () -> mDb.configDao().insertConfig(newConfig));
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
        mDb.accountDao().insertAccount(newAcc);
        accountBalanceUpdateById(mDb, accId);
    }

    private void alertNetworkDialogShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.splash_nonetwork_string))
                .setPositiveButton(getResources().getString(R.string.splash_nonetwork_positive),
                        (dialogInterface, i) -> {
                            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                .setNegativeButton(getResources().getString(R.string.splash_nonetwork_negative),
                        (dialogInterface, i) -> finish());
        builder.create().show();
    }
}
