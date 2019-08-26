package ru.vincetti.vimoney.transaction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

import ru.vincetti.vimoney.data.models.TransactionModel;

public class TransactionViewModel extends ViewModel {
    private static MutableLiveData<TransactionModel> mTrans;
    private static MutableLiveData<HashMap<Integer, String>> curSymbols;
    private static MutableLiveData<HashMap<Integer, String>> accountNames;
    private static MutableLiveData<HashMap<Integer, String>> accountNotArchiveNames;

    public void setTransaction(TransactionModel tmp) {
        if (mTrans == null) {
            mTrans = new MutableLiveData<>();
        }
        mTrans.setValue(tmp);
    }

    public LiveData<TransactionModel> getTransaction() {
        return mTrans;
    }

    public void setCurrency(HashMap<Integer, String> tmp) {
        if (curSymbols == null) {
            curSymbols = new MutableLiveData<>();
        }
        curSymbols.setValue(tmp);
    }

    public LiveData<HashMap<Integer, String>> getCurrencySymbols() {
        return curSymbols;
    }

    public void setAccountNames(HashMap<Integer, String> tmp) {
        if (accountNames == null) {
            accountNames = new MutableLiveData<>();
        }
        accountNames.setValue(tmp);
    }

    public LiveData<HashMap<Integer, String>> getAccountNames() {
        return accountNames;
    }

    public void setNotArchiveAccountNames(HashMap<Integer, String> tmp) {
        if (accountNotArchiveNames == null) {
            accountNotArchiveNames = new MutableLiveData<>();
        }
        accountNotArchiveNames.setValue(tmp);
    }

    public LiveData<HashMap<Integer, String>> getNotArchiveAccountNames() {
        return accountNotArchiveNames;
    }
}