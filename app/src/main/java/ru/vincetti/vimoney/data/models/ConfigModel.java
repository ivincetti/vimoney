package ru.vincetti.vimoney.data.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "config")
public class ConfigModel {

    @PrimaryKey (autoGenerate = true)
    private int id;
    @ColumnInfo(name = "key_name")
    private String keyName;
    private String value;

    public ConfigModel(int id, String keyName, String value) {
        this.id = id;
        this.keyName = keyName;
        this.value = value;
    }

    @Ignore
    public ConfigModel(String keyName, String value) {
        this.keyName = keyName;
        this.value = value;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getValue() {
        return value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
