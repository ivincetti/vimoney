package ru.vincetti.vimoney.data.models.json;

import com.google.gson.annotations.SerializedName;

public class TransactionsItem{

	@SerializedName("date")
	private long date;

	@SerializedName("accountId")
	private int accountId;

	@SerializedName("description")
	private String description;

	@SerializedName("sum")
	private int sum;

	@SerializedName("type")
	private int type;

	public long getDate(){
		return date;
	}

	public int getAccountId(){
		return accountId;
	}

	public String getDescription(){
		return description;
	}

	public int getSum(){
		return sum;
	}

	public int getType(){
		return type;
	}
}