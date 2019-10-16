package ru.vincetti.vimoney.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import java.io.FileOutputStream;

import io.reactivex.schedulers.Schedulers;
import ru.vincetti.vimoney.MyApp;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;
import ru.vincetti.vimoney.settings.json.JsonFile;

public class FileService extends IntentService {
    public final static String SAVE_ACTION = "save-file-service";

    public FileService() {
        super("FileService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (SAVE_ACTION.equals(intent.getAction())) {
            export();
        }
    }

    public static void export() {
        Gson gson = new Gson();
        Context context = MyApp.getAppContext();

        AppDatabase.getInstance(context)
                .accountDao()
                .loadAllAccounts()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        accountModels -> {
                            String accountJson = gson.toJson(accountModels);
                            FileOutputStream fosAccount = context.openFileOutput(JsonFile.FILE_NAME_ACCOUNTS, MODE_PRIVATE);

                            fosAccount.write(accountJson.getBytes());

                            AppDatabase
                                    .getInstance(context)
                                    .transactionDao()
                                    .loadAllTransactions()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.io())
                                    .subscribe(
                                            transactionModels -> {
                                                String transactionsJson = gson.toJson(transactionModels);
                                                FileOutputStream fos = context.openFileOutput(JsonFile.FILE_NAME_TRANSACTIONS, MODE_PRIVATE);

                                                fos.write(transactionsJson.getBytes());

                                                MyApp.getAppContext().startService(new Intent(context, NotificationService.class)
                                                        .setAction(NotificationService.NOTIFICATION_SAVE_ACTION));
                                            });

                        }, throwable -> {
                            MyApp.getAppContext().startService(new Intent(context, NotificationService.class)
                                    .setAction(NotificationService.NOTIFICATION_SAVE_ERROR_ACTION));
                        });
    }
}
