package ru.vincetti.vimoney.data.models;

import androidx.annotation.NonNull;
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
    private int currency;
    @ColumnInfo(name = "extra_key")
    @NonNull
    private String extraKey;
    @ColumnInfo(name = "extra_value")
    @NonNull
    private String extraValue;
    @NonNull
    private String color;
    @ColumnInfo(name = "archive")
    private boolean isArhive;

    @Ignore
    public AccountModel(String name, String type, int sum, int currency, String color) {
        this.name = name;
        this.type = type;
        this.sum = sum;
        this.currency = currency;
        this.extraKey = "";
        this.extraValue = "";
        this.color = color;
    }

    public AccountModel(int id, String name, String type, int sum, int currency, String color) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.sum = sum;
        this.currency = currency;
        this.extraKey = "";
        this.extraValue = "";
        this.color = color;
    }

    @Ignore
    public AccountModel() {
        this.name = "";
        this.type = ACCOUNT_TYPE_CASH;
        this.sum = 0;
        this.extraKey = "";
        this.extraValue = "";
        this.color = "";
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

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public boolean isArhive() {
        return isArhive;
    }

    public void setArhive(boolean arhive) {
        isArhive = arhive;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public String getExtraKey() {
        return extraKey;
    }

    public void setExtraKey(String extraKey) {
        this.extraKey = extraKey;
    }

    public String getExtraValue() {
        return extraValue;
    }

    public void setExtraValue(String extraValue) {
        this.extraValue = extraValue;
    }

    @NonNull
    public String getColor() {
        return color;
    }

    public void setColor(@NonNull String color) {
        this.color = color;
    }
}
