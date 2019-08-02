package ru.vincetti.vimoney.models;

public class Transaction {
    public static int TRANSACTION_TYPE_INCOME = 1;
    public static int TRANSACTION_TYPE_SPENT = 2;

    private String name;
    private String date;
    private int type;
    private int sum;

    public Transaction(String date, String name, int type, int sum) {
        this.name = name;
        this.date = date;
        this.type = type;
        this.sum = sum;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public int getType() {
        return type;
    }

    public int getSum() {
        return sum;
    }
}
