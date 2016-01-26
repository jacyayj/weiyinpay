package com.example.modle;

public class T {
	private static final T t= new T();
	private String bankname = "0";
	private String card = "0";
	private String p = "0";
	private String w = "0";
	private String z = "0";
	private String name = "0";
	private String id = "0";
	private T() {
	}
	public synchronized static T getInstance(){
		return t;
	}
	public void setZ(String z) {
		this.z = z;
	}public String getZ() {
		return z;
	}
	public String getBankname() {
		return bankname;
	}
	public void setBankname(String bankname) {
		this.bankname = bankname;
	}
	public String getCard() {
		return card;
	}
	public void setCard(String card) {
		this.card = card;
	}
	public String getP() {
		return p;
	}
	public void setP(String p) {
		this.p = p;
	}
	public String getW() {
		return w;
	}
	public void setW(String w) {
		this.w = w;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
