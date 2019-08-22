package ru.vincetti.vimoney.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.HashMap;
import java.util.List;

import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;

public class HomeViewModel extends AndroidViewModel {
    private LiveData<List<AccountModel>> accList;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        accList = AppDatabase.getInstance(
                this.getApplication()).accountDao().loadNotArhiveAccounts();
    }

    public LiveData<List<AccountModel>> getAccounts() {
        return accList;
    }

    private void getHash(HashMap<Integer, String> hash, int[] intArray, String[] stringArray) {
        for (int i = 0; i < intArray.length; i++) {
            hash.put(intArray[i], stringArray[i]);
        }
    }
}
