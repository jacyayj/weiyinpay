package com.example.untils;
	/**
	 * ʡ������
	 * @author jacyayj
	 *
	 */
public class Provice {
	//ʡid
	private String pid;
	//ʡ����
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
