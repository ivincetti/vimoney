package ru.vincetti.vimoney.data.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "currency")
public class CurrencyModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int code;
    private String name;

    @Ignore
    public CurrencyModel(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public CurrencyModel(int id, int code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
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

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
