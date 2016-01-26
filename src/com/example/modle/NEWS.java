package com.example.modle;
	/**
	 * 消息类
	 * @author jacyayj
	 *
	 */
public class NEWS {
	
	private int id = 0;
	
	private String name = null;
	
	//消息读取状态
	private String fstatus = null;
	//消息内容
	private String fcontent = null;
	//消息时间
	private String fdate = null;
	//消息标题
	private String ftitle = null;
	public NEWS(){}
	public NEWS(String title,String time,String content,String state) {
		this.ftitle = title;
		this.fdate = time;
		this.fstatus = state;
		this.fcontent = content;
	}
	public String getFstatus() {
		return fstatus;
	}
	public void setFstatus(String fstatus) {
		this.fstatus = fstatus;
	}
	public String getFcontent() {
		return fcontent;
	}
	public void setFcontent(String fcontent) {
		this.fcontent = fcontent;
	}
	public String getFdate() {
		return fdate;
	}
	public void setFdate(String fdate) {
		this.fdate = fdate;
	}
	public String getFtitle() {
		return ftitle;
	}
	public void setFtitle(String ftitle) {
		this.ftitle = ftitle;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
