package ru.vincetti.vimoney.data.models;

import androidx.core.net.ConnectivityManagerCompat;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "transactions")
public class Transaction {
    public static int TRANSACTION_TYPE_INCOME = 1;
    public static int TRANSACTION_TYPE_SPENT = 2;

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String description;
    private Date date;
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;
    private int type;
    private float sum;

    @Ignore
    public Transaction(Date date, String description, int type, float sum) {
        this.date = date;
        this.description = description;
        this.type = type;
        this.sum = sum;
    }

    public Transaction(int id, Date date, String description, int type, float sum) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.type = type;
        this.sum = sum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
