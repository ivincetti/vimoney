package ru.vincetti.vimoney.data.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "transactions")
public class TransactionModel {
    public final static int TRANSACTION_TYPE_INCOME = 1;
    public final static int TRANSACTION_TYPE_SPENT = 2;
    public final static int TRANSACTION_TYPE_TRANSFER = 3;
    public final static String TRANSACTION_TYPE_TRANSFER_KEY = "transfer_transaction_id";
    public final static int TRANSACTION_TYPE_DEBT = 4;
    public final static int DEFAULT_ID = 0;

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "account_id")
    private int accountId;
    private String description;
    private Date date;
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;
    private int type;
    private float sum;
    @ColumnInfo(name = "extra_key")
    @NonNull
    private String extraKey;
    @ColumnInfo(name = "extra_value")
    @NonNull
    private String extraValue;
    private boolean system;
    private boolean deleted;

    @Ignore
    public TransactionModel() {
        this.id = DEFAULT_ID;
        this.date = new Date();
        this.accountId = DEFAULT_ID;
        this.updatedAt = new Date();
        this.description = "";
        this.type = 0;
        this.sum = DEFAULT_ID;
        this.extraKey = "";
        this.extraValue = "";
        this.system = false;
        this.deleted = false;
    }

    @Ignore
    public TransactionModel(Date date, int accountId, String description, int type, float sum) {
        this();
        this.date = date;
        this.accountId = accountId;
        this.updatedAt = date;
        this.description = description;
        this.type = type;
        this.sum = sum;
    }

    @Ignore
    public TransactionModel(int accountId, String description, Date date, Date updatedAt, int type, float sum) {
        this();
        this.accountId = accountId;
        this.description = description;
        this.date = date;
        this.updatedAt = updatedAt;
        this.type = type;
        this.sum = sum;
    }

    public TransactionModel(int id, int accountId, String description, Date date, Date updatedAt, int type, float sum) {
        this();
        this.id = id;
        this.accountId = accountId;
        this.description = description;
        this.date = date;
        this.updatedAt = updatedAt;
        this.type = type;
        this.sum = sum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getSum() {
        return sum;
    }

    public void setSum(float sum) {
        this.sum = sum;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @NonNull
    public String getExtraKey() {
        return extraKey;
    }

    public void setExtraKey(@NonNull String extraKey) {
        this.extraKey = extraKey;
    }

    @NonNull
    public String getExtraValue() {
        return extraValue;
    }

    public void setExtraValue(@NonNull String extraValue) {
        this.extraValue = extraValue;
    }

    public void copyFrom(TransactionModel tmp) {
        this.id = tmp.getId();
        this.accountId = tmp.getAccountId();
        this.description = tmp.getDescription();
        this.date = tmp.getDate();
        this.updatedAt = tmp.getUpdatedAt();
        this.type = tmp.getType();
        this.sum = tmp.getSum();
        this.extraKey = tmp.getExtraKey();
        this.extraValue = tmp.getExtraValue();
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "TransactionModel{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", updatedAt=" + updatedAt +
                ", type=" + type +
                ", sum=" + sum +
                ", extraKey='" + extraKey + '\'' +
                ", extraValue='" + extraValue + '\'' +
                ", system=" + system +
                ", deleted=" + deleted +
                '}';
    }
}
