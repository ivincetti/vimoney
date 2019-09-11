package ru.vincetti.vimoney.data.models;

import androidx.room.ColumnInfo;

public class AccountListModel {

    public int id;
    public String name;
    public String type;
    public int sum;
    @ColumnInfo(name = "account_symbol")
    public String curSymbol;
    public String color;
    @ColumnInfo(name = "archive")
    public boolean isArhive;

    public int getId() {
        return id;
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

    public boolean isArhive() {
        return isArhive;
    }

    public String getSymbol() {
        return curSymbol;
    }

    public String getColor() {
        return color;
    }
}
