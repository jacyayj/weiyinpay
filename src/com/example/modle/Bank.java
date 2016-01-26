package com.example.modle;
	/**
	 * 银行类
	 * @author jacyayj
	 *
	 */
public class Bank {
	//银行卡号
	private String pnumber;
	//持卡人姓名
	private String username;
	//银行民称
	private String pname;
	//银行卡id
	private String pid;
	public Bank(String bank1,String bank2,String user) {
		this.pname = bank1;
		this.pnumber = bank2;
		this.username = user;
	}
	public String getPnumber() {
		return pnumber;
	}
	public void setPnumber(String pnumber) {
		this.pnumber = pnumber;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
}	
