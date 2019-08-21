package ru.vincetti.vimoney.check;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;

public class AccountsViewModel extends AndroidViewModel {
    private LiveData<List<AccountModel>> accList;

    public AccountsViewModel(@NonNull Application application) {
        super(application);
        accList = AppDatabase.getInstance(this.getApplication()).accountDao().loadAllAccounts();
    }

    public LiveData<List<AccountModel>> getAccounts() {
        return accList;
    }
}