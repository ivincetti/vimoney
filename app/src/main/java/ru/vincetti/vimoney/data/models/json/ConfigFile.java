package ru.vincetti.vimoney.data.models.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConfigFile {

    @SerializedName("accounts")
    private List<AccountsItem> accounts;

    @SerializedName("user")
    private User user;

    @SerializedName("date_edit")
    private long date_edit;

    public List<AccountsItem> getAccounts() {
        return accounts;
    }

    public User getUser() {
        return user;
    }

    public long getDateEdit() {
        return date_edit;
    }
}