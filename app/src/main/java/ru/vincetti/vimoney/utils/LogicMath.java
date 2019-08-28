package ru.vincetti.vimoney.utils;

import android.content.Context;

import java.util.List;

import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.models.AccountListModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;

public class LogicMath {
    // set correct account (accID) balance
    public static void accountBalanceUpdateById(Context context, int accId) {
        AppDatabase mDb = AppDatabase.getInstance(context);
        AppExecutors.getsInstance().diskIO().execute(() -> {
            float Sum = mDb.transactionDao().loadSumByCheckId(accId);
            mDb.accountDao().updateSumByAccId(accId, Sum);
        });
    }

    // Math allUser balance
    public static int userBalanceChange(List<AccountListModel> accList) {
        int bal = 0;
        for (AccountListModel o : accList) {
            bal += o.getSum();
        }
        return bal;
    }
}
