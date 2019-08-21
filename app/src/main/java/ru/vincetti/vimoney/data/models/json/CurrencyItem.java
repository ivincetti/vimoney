package ru.vincetti.vimoney.data.models.json;

import com.google.gson.annotations.SerializedName;

public class CurrencyItem{

	@SerializedName("code")
	private int code;

	@SerializedName("name")
	private String name;

	public void setCode(int code){
		this.code = code;
	}

	public int getCode(){
		return code;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}
}