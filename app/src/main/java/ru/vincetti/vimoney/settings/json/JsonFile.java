package ru.vincetti.vimoney.settings.json;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.models.TransactionModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;
import ru.vincetti.vimoney.service.FileService;

public class JsonFile {
    public static final String FILE_NAME_TRANSACTIONS = "transactions.json";
    public static final String FILE_NAME_ACCOUNTS = "accounts.json";

    public static void save(Context context) {
        context.startService(
                new Intent(context, FileService.class)
                        .setAction(FileService.SAVE_ACTION)
        );
    }

    public static void load(Context context) {
        Gson gson = new Gson();
        final AppDatabase db = AppDatabase.getInstance(context);

        AppExecutors.getsInstance().diskIO().execute(() -> {
            FileInputStream fisTransactions = null;
            FileInputStream fisAccounts = null;
            final StringBuilder transactionsJsonBuilder = new StringBuilder();
            final StringBuilder accountsJsonBuilder = new StringBuilder();

            try {
                fisTransactions = context.openFileInput(FILE_NAME_TRANSACTIONS);
                InputStreamReader isr = new InputStreamReader(fisTransactions);
                BufferedReader br = new BufferedReader(isr);
                String text;

                while ((text = br.readLine()) != null) {
                    transactionsJsonBuilder.append(text).append("\n");
                }

                fisAccounts = context.openFileInput(FILE_NAME_ACCOUNTS);
                isr = new InputStreamReader(fisAccounts);
                br = new BufferedReader(isr);

                while ((text = br.readLine()) != null) {
                    accountsJsonBuilder.append(text).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fisTransactions != null) {
                    try {
                        fisTransactions.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (fisAccounts != null) {
                    try {
                        fisAccounts.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (!TextUtils.isEmpty(transactionsJsonBuilder.toString())) {
                db.transactionDao()
                        .deleteAllTransactions()
                        .doOnComplete(() -> {
                            Type listType = new TypeToken<ArrayList<TransactionModel>>() {}.getType();
                            List<TransactionModel> transactions = gson.fromJson(transactionsJsonBuilder.toString(), listType);
                            for (TransactionModel transaction : transactions) {
                                db.transactionDao().insertTransaction(transaction);
                            }
                            db.accountDao()
                                    .deleteAllAccounts()
                                    .doOnComplete(() -> {
                                        Type listType1 = new TypeToken<ArrayList<AccountModel>>() {}.getType();
                                        List<AccountModel> transactions1 = gson.fromJson(accountsJsonBuilder.toString(), listType1);
                                        for (AccountModel account : transactions1) {
                                            db.accountDao().insertAccount(account);
                                        }
                                    })
                                    .subscribe();
                        })
                        .subscribe();
            }
        });
    }

}
