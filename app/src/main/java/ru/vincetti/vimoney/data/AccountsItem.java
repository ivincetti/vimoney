package ru.vincetti.vimoney.data;

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

	public void setStaticId(String staticId){
		this.staticId = staticId;
	}

	public String getStaticId(){
		return staticId;
	}

	public void setCreated(String created){
		this.created = created;
	}

	public String getCreated(){
		return created;
	}

	public void setDateLimitInterval(String dateLimitInterval){
		this.dateLimitInterval = dateLimitInterval;
	}

	public String getDateLimitInterval(){
		return dateLimitInterval;
	}

	public void setInstrument(int instrument){
		this.instrument = instrument;
	}

	public int getInstrument(){
		return instrument;
	}

	public void setSum(String sum){
		this.sum = sum;
	}

	public String getSum(){
		return sum;
	}

	public void setPayoffType(String payoffType){
		this.payoffType = payoffType;
	}

	public String getPayoffType(){
		return payoffType;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setPercent(String percent){
		this.percent = percent;
	}

	public String getPercent(){
		return percent;
	}

	public void setDateLimit(String dateLimit){
		this.dateLimit = dateLimit;
	}

	public String getDateLimit(){
		return dateLimit;
	}

	public void setBalance(int balance){
		this.balance = balance;
	}

	public int getBalance(){
		return balance;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setPayoffPeriod(String payoffPeriod){
		this.payoffPeriod = payoffPeriod;
	}

	public String getPayoffPeriod(){
		return payoffPeriod;
	}
}