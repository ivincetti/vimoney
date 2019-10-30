package ru.vincetti.vimoney.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.google.gson.Gson;

import java.io.File;
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

    public void export() {
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
                            writeFileExternalStorage(accountJson.getBytes(), JsonFile.FILE_NAME_ACCOUNTS);

                            AppDatabase
                                    .getInstance(context)
                                    .transactionDao()
                                    .loadAllTransactions()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.io())
                                    .subscribe(
                                            transactionModels -> {
                                                String transactionsJson = gson.toJson(transactionModels);
                                                writeFileExternalStorage(transactionsJson.getBytes(), JsonFile.FILE_NAME_TRANSACTIONS);

                                                MyApp.getAppContext().startService(new Intent(context, NotificationService.class)
                                                        .setAction(NotificationService.NOTIFICATION_SAVE_ACTION));
                                            });

                        }, throwable -> MyApp.getAppContext().startService(new Intent(context, NotificationService.class)
                                .setAction(NotificationService.NOTIFICATION_SAVE_ERROR_ACTION)));
    }

    public void writeFileExternalStorage(byte[] jsonBytes, String fileName) {
        //If it isn't mounted - we can't write into it.
        if (isExternalMounted()) {
            writeExternalFile(jsonBytes, fileName);
        }
    }

    private boolean isExternalMounted() {
        //Checking the availability state of the External Storage.
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private void writeExternalFile(byte[] jsonBytes, String fileName) {
        //Create a new file that points to the root directory, with the given name:
        File jsonFile = new File(getExternalFilesDir(null), fileName);

        //the write operation
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(jsonFile, false);
            outputStream.write(jsonBytes);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
