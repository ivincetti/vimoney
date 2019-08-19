package ru.vincetti.vimoney.data.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class AccountModel {
    public final static String ACCOUNT_TYPE_CASH = "cash";
    public final static String ACCOUNT_TYPE_DEBIT = "debit";
    public final static String ACCOUNT_TYPE_CREDIT = "credit";

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String type;
    private int sum;

    @ColumnInfo(name = "archive")
    private boolean isArhive;

    @Ignore
    public AccountModel(String name, String type, int sum) {
        this.name = name;
        this.type = type;
        this.sum = sum;
    }

    public AccountModel(int id, String name, String type, int sum) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.sum = sum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setArhive(boolean arhive) {
        isArhive = arhive;
    }
}
