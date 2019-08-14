package ru.vincetti.vimoney.data.models;

public class Account {
    private String name;
    private String type;
    private int sum;

    public Account(String name, String type, int sum) {
        this.name = name;
        this.type = type;
        this.sum = sum;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getSum() {
        return sum;
    }
}
