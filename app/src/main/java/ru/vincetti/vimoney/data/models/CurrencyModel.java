package ru.vincetti.vimoney.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "currency")
public class CurrencyModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int code;
    private String name;
    @NonNull
    private String symbol;

    @Ignore
    public CurrencyModel(int code, String name, String symbol) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
    }

    public CurrencyModel(int id, int code, String name, String symbol) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.symbol = symbol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
