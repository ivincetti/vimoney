package ru.vincetti.vimoney.utils;

import android.content.Context;

import java.util.List;

import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.models.AccountListModel;
import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;

public class LogicMath {

    // set correct account (accID) balance
    public static void accountBalanceUpdateById(Context context, int accId) {
        AppDatabase mDb = AppDatabase.getInstance(context);

        class Sum {
            private float sumPlus, sumMinus;

            private Sum() {
                this.sumPlus = 0f;
                this.sumMinus = 0f;
            }

            private void setSumPlus(float sumPlus) {
                this.sumPlus = sumPlus;
            }

            private void setSumMinus(float sumMinus) {
                this.sumMinus = sumMinus;
            }

            public float getSum() {
                return sumPlus - sumMinus;
            }
        }
        Sum mSum = new Sum();

        AppExecutors.getsInstance().diskIO().execute(
                () -> mSum.setSumMinus(mDb.transactionDao().loadSumIncomeByCheckId(accId)));
        AppExecutors.getsInstance().diskIO().execute(
                () -> mSum.setSumPlus(mDb.transactionDao().loadSumExpenseByCheckId(accId)));
        AppExecutors.getsInstance().diskIO().execute(
                () -> mDb.accountDao().updateSumByAccId(accId, mSum.getSum()));
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
