package ru.vincetti.vimoney.data.models.json;

import com.google.gson.annotations.SerializedName;

public class AccountsItem{

	@SerializedName("static_id")
	private String staticId;

	@SerializedName("created")
	private String created;

	@SerializedName("date_limit_interval")
	private String dateLimitInterval;

	@SerializedName("instrument")
	private int instrument;

	@SerializedName("sum")
	private String sum;

	@SerializedName("payoff_type")
	private String payoffType;

	@SerializedName("type")
	private String type;

	@SerializedName("title")
	private String title;

	@SerializedName("percent")
	private String percent;

	@SerializedName("date_limit")
	private String dateLimit;

	@SerializedName("balance")
	private int balance;

	@SerializedName("id")
	private int id;

	@SerializedName("payoff_period")
	private String payoffPeriod;

	public String getStaticId(){
		return staticId;
	}

	public String getCreated(){
		return created;
	}

	public String getDateLimitInterval(){
		return dateLimitInterval;
	}

	public int getInstrument(){
		return instrument;
	}

	public String getSum(){
		return sum;
	}

	public String getPayoffType(){
		return payoffType;
	}

	public String getType(){
		return type;
	}

	public String getTitle(){
		return title;
	}

	public String getPercent(){
		return percent;
	}

	public String getDateLimit(){
		return dateLimit;
	}

	public int getBalance(){
		return balance;
	}

	public int getId(){
		return id;
	}

	public String getPayoffPeriod(){
		return payoffPeriod;
	}
}