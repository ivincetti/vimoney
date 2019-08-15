package ru.vincetti.vimoney.utils;

import android.content.Context;

import ru.vincetti.vimoney.data.sqlite.AppDatabase;

public class TransactionMath {

    public static void accountUpdate(Context context, int accId) {
        Float sumPlus = AppDatabase.getInstance(context)
                .transactionDao()
                .loadSumIncomeByCheckId(accId);
        Float sumMinus = AppDatabase.getInstance(context)
                .transactionDao()
                .loadSumExpenseByCheckId(accId);
        float sum = sumPlus - sumMinus;
        AppDatabase.getInstance(context).accountDao().updateSumByAccId(accId, sum);
    }
}
