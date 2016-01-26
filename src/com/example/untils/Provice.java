package com.example.untils;
	/**
	 * 省数据类
	 * @author jacyayj
	 *
	 */
public class Provice {
	//省id
	private String pid;
	//省名称
	private String pname;

	public Provice(String pid, String pname) {
		this.pid = pid;
		this.pname = pname;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}
}
