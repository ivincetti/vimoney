package ru.vincetti.vimoney.settings.json;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
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
            String transactionsJson;
            String accountsJson;

            if (isExternalMounted()){
                transactionsJson = readExternalFile(context, FILE_NAME_TRANSACTIONS);
                accountsJson = readExternalFile(context, FILE_NAME_ACCOUNTS);

                if (!TextUtils.isEmpty(transactionsJson) && !TextUtils.isEmpty(accountsJson)) {
                    db.transactionDao()
                            .deleteAllTransactions()
                            .doOnComplete(() -> {
                                Type listType = new TypeToken<ArrayList<TransactionModel>>() {}.getType();
                                List<TransactionModel> transactions = gson.fromJson(transactionsJson, listType);
                                for (TransactionModel transaction : transactions) {
                                    db.transactionDao().insertTransaction(transaction);
                                }
                                db.accountDao()
                                        .deleteAllAccounts()
                                        .doOnComplete(() -> {
                                            Type listType1 = new TypeToken<ArrayList<AccountModel>>() {}.getType();
                                            List<AccountModel> transactions1 = gson.fromJson(accountsJson, listType1);
                                            for (AccountModel account : transactions1) {
                                                db.accountDao().insertAccount(account);
                                            }
                                        })
                                        .subscribe();
                            })
                            .subscribe();
                }
            }
        });
    }

    private static String readExternalFile(Context context, String fileName) {
        FileInputStream fis = null;
        final StringBuilder JsonBuilder = new StringBuilder();
        try {
            File myExternalFile = new File(context.getExternalFilesDir(null), fileName);
            fis = new FileInputStream(myExternalFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;

            while ((text = br.readLine()) != null) {
                JsonBuilder.append(text).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return JsonBuilder.toString();
    }

    private static boolean isExternalMounted() {
        //Checking the availability state of the External Storage.
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

}
