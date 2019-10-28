package ru.vincetti.vimoney.utils;

import java.util.List;

import ru.vincetti.vimoney.MyApp;
import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.models.AccountListModel;
import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;

public class LogicMath {
    // set correct account (accID) balance
    public static void accountBalanceUpdateById(int accId) {
        AppDatabase mDb = AppDatabase.getInstance(MyApp.getAppContext());
        accountBalanceUpdateById(mDb, accId);
    }

    // set correct account (accID) balance with DB
    public static void accountBalanceUpdateById(AppDatabase mDb, int accId) {
        AppExecutors.getsInstance().diskIO().execute(() -> {
            float sum = mDb.transactionDao().loadSumByCheckId(accId);
            mDb.accountDao().updateSumByAccId(accId, sum);
        });
    }

    // All accounts update
    public static void accountBalanceUpdateAll() {
        AppDatabase mDb = AppDatabase.getInstance(MyApp.getAppContext());
        accountBalanceUpdateAll(mDb);
    }

    // All accounts update
    public static void accountBalanceUpdateAll(AppDatabase mDb) {
        AppExecutors.getsInstance().diskIO().execute(() -> {
            List<AccountModel> accounts = mDb.accountDao().loadAllAccountsList();
            accountBalanceUpdateAll(mDb, accounts);
        });
    }

    public static void accountBalanceUpdateAll(AppDatabase mDb, List<AccountModel> accounts) {
        AppExecutors.getsInstance().diskIO().execute(() -> {
            for (AccountModel account : accounts) {
                int accountId = account.getId();
                float sum = mDb.transactionDao().loadSumByCheckId(accountId);
                mDb.accountDao().updateSumByAccId(accountId, sum);
            }
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
