package ru.vincetti.vimoney.utils;

import android.content.Context;

import java.util.Date;
import java.util.Random;

import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.models.TransactionModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;

public class TransactionsGenerator {
    private static final int TRANSACTION_COUNT = 5;

    // sample accounts transactions generate
    public static void generate(Context context, int count) {
        for (int i = 0; i < count; i++) {
            TransactionModel tmp =
                    new TransactionModel(
                            new Date(),
                            (new Random().nextInt(3) + 1),
                            "Sample",
                            (i % 2 + 1),
                            150
                    );
            AppExecutors.getsInstance().diskIO().execute(
                    () -> AppDatabase.getInstance(context).transactionDao().insertTransaction(tmp));
        }
    }

    // use default count
    public static void generate(Context context) {
        generate(context, TRANSACTION_COUNT);
    }
}
