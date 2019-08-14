package ru.vincetti.vimoney.utils;

import java.util.ArrayList;

import ru.vincetti.vimoney.data.models.Transaction;

public class TransactionsGenerator {
    private static int TRANSACTION_COUNT = 10;

    public static ArrayList<Transaction> generate(int count) {
        ArrayList<Transaction> trList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            trList.add(new Transaction(
                    "2019-02-19",
                    "Кузина",
                    i % 2,
                    150

            ));
        }
        return trList;
    }

    public static ArrayList<Transaction> generate() {
        return generate(TRANSACTION_COUNT);
    }
}
