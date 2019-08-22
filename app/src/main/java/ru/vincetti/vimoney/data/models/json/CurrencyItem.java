package ru.vincetti.vimoney.data.models.json;

import com.google.gson.annotations.SerializedName;

public class CurrencyItem{

	@SerializedName("code")
	private int code;

	@SerializedName("name")
	private String name;

	private String symbol;

	public int getCode(){
		return code;
	}

	public String getName(){
		return name;
	}

	public String getSymbol() {
		return symbol;
	}
}