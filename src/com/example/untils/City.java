package com.example.untils;
	/**
	 * 城市类
	 * @author jacyayj
	 *
	 */
public class City {
	//市ID
	private String cid;
	//是名称
	private String cname;
	//市所属省ID
	private String pid;
	
	public City(String cid, String cname, String pid) {
		this.cid = cid;
		this.cname = cname;
		this.pid = pid;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

}
