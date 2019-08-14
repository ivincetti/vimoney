package ru.vincetti.vimoney.utils;

import android.content.Context;

import java.util.Date;
import java.util.Random;

import ru.vincetti.vimoney.data.models.TransactionModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;

public class TransactionsGenerator {
    private static int TRANSACTION_COUNT = 30;

    public static void generate(Context context, int count) {
        for (int i = 0; i < count; i++) {
            TransactionModel tmp =
                    new TransactionModel(
                            new Date(),
                            (new Random().nextInt(3) + 1),
                            "Кузина",
                            i % 2,
                            150
                    );
            AppDatabase.getInstance(context).transactionDao().insertTransaction(tmp);
        }
    }

    public static void generate(Context context) {
        generate(context, TRANSACTION_COUNT);
    }
}
