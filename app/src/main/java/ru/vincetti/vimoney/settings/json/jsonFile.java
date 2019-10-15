package ru.vincetti.vimoney.settings.json;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import com.google.gson.Gson;

import ru.vincetti.vimoney.MyApp;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;

public class jsonFile {
    private static final String TAG = "DEBUG";

    public static void export(LifecycleOwner lfOwner) {
        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder();
        Log.d(TAG, "start export");

        AppDatabase
                .getInstance(MyApp.getAppContext())
                .transactionDao()
                .loadAllTransactions().observe(lfOwner, transactionModels -> {
                    sb.append(gson.toJson(transactionModels));
                    Log.d(TAG, "onChanged: " + sb.toString());
                });
    }

    public static void load(String json) {

    }

}
