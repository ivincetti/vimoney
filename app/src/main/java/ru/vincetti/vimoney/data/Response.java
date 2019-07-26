package ru.vincetti.vimoney.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response{

	@SerializedName("accounts")
	private List<AccountsItem> accounts;

	@SerializedName("user")
	private User user;

	public void setAccounts(List<AccountsItem> accounts){
		this.accounts = accounts;
	}

	public List<AccountsItem> getAccounts(){
		return accounts;
	}

	public void setUser(User user){
		this.user = user;
	}

	public User getUser(){
		return user;
	}
}