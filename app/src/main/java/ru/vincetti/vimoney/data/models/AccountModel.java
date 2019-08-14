package ru.vincetti.vimoney.data.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class AccountModel {

    @PrimaryKey (autoGenerate = true)
    private int id;
    @ColumnInfo(name = "acc_id")
    private int accountID;
    private String name;
    private String type;
    private int sum;

    @Ignore
    public AccountModel(int accountID,String name, String type, int sum) {
        this.accountID = accountID;
        this.name = name;
        this.type = type;
        this.sum = sum;
    }

    public AccountModel(int id, int accountID, String name, String type, int sum) {
        this.id = id;
        this.accountID = accountID;
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

    public int getAccountID() {
        return accountID;
    }
}
