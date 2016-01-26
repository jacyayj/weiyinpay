package com.example.modle;

import android.graphics.Bitmap;

public class TradeModle {
	private String phone = null;
	private String price = null;
	private String name = null;
	private String type = null;
	private String date = null;
	private String ordernumber = null;
	private String id = null;
	private String posid = null;
	private String pwd = null;
	private String sencondno = null;
	private String thirdno = null;
	private Bitmap sign = null;
	private TradeModle(){}
	public static final TradeModle MODLE = new TradeModle();
	public synchronized static TradeModle getInstance(){
		if (MODLE == null) {
			new TradeModle();
		}
		return MODLE;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getOrdernumber() {
		return ordernumber;
	}
	public void setOrdernumber(String ordernumber) {
		this.ordernumber = ordernumber;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPosid() {
		return posid;
	}
	public void setPosid(String posid) {
		this.posid = posid;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getSencondno() {
		return sencondno;
	}
	public void setSencondno(String sencondno) {
		this.sencondno = sencondno;
	}
	public Bitmap getSign() {
		return sign;
	}
	public void setSign(Bitmap sign) {
		this.sign = sign;
	}
	public String getThirdno() {
		return thirdno;
	}
	public void setThirdno(String thirdno) {
		this.thirdno = thirdno;
	}
}
