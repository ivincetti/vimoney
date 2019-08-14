package ru.vincetti.vimoney.data.models.json;

import com.google.gson.annotations.SerializedName;

public class User{

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	public String getName(){
		return name;
	}

	public int getId(){
		return id;
	}
}