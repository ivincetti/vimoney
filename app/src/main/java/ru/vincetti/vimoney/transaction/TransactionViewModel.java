package ru.vincetti.vimoney.transaction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.vincetti.vimoney.data.models.TransactionModel;

public class TransactionViewModel extends ViewModel {
    private static MutableLiveData<TransactionModel> mTrans;

    public void setTransaction(TransactionModel tmp) {
        if (mTrans == null){
            mTrans = new MutableLiveData<TransactionModel>();
        }
        mTrans.setValue(tmp);
    }

    public LiveData<TransactionModel> getTransaction() {
        return mTrans;
    }
}