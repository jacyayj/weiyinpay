package com.example.modle;
	/**
	 * ������
	 * @author jacyayj
	 *
	 */
public class Bank {
	//���п���
	private String pnumber;
	//�ֿ�������
	private String username;
	//�������
	private String pname;
	//���п�id
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
