package ru.vincetti.vimoney.data.models;

import androidx.room.ColumnInfo;

import java.util.Date;

public class TransactionListModel {

    public int id;
    @ColumnInfo(name = "account_name")
    public String accountName;
    public String description;
    public Date date;
    public int type;
    public float sum;
    @ColumnInfo(name = "account_symbol")
    public String curSymbol;

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public int getType() {
        return type;
    }

    public float getSum() {
        return sum;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getCurSymbol() {
        return curSymbol;
    }
}
