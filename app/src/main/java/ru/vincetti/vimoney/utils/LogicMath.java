package ru.vincetti.vimoney.utils;

import android.content.Context;

import java.util.List;

import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;

public class LogicMath {

    // set correct account (accID) balance
    public static void accountBalanceUpdateById(Context context, int accId) {
        Float sumPlus = AppDatabase.getInstance(context)
                .transactionDao()
                .loadSumIncomeByCheckId(accId);
        Float sumMinus = AppDatabase.getInstance(context)
                .transactionDao()
                .loadSumExpenseByCheckId(accId);
        float sum = sumPlus - sumMinus;
        AppExecutors.getsInstance().diskIO().execute(
                () -> AppDatabase.getInstance(context).accountDao().updateSumByAccId(accId, sum));
    }

    // Math allUser balance
    public static int userBalanceChange(List<AccountModel> accList) {
        int bal = 0;
        for (AccountModel o : accList) {
            bal += o.getSum();
        }
        return bal;
    }
}
