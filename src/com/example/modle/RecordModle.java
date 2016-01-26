package com.example.modle;


public class RecordModle  {
	//终端编号    terminalno   
	//订单号      orderno          
	// 卡号       cardno         
	//日期时间    datetime   
	//交易状态    transstat      
	//交易金额    amount       
	//交易类型    transtype   mfrchantno;//商户编号  
	//卡号类别     cardtype        
	//交易项目      transitem      fid;//数据库表组件
	private String terminalno; //终端编号 
	private String orderno; //订单号  
	private String cardno; // 卡号 
	private String datetime; //日期时间 
	private String transstat;//交易状态
	private String amount;//交易金额
	private String transtype;//交易类型
	private String cardtype;//卡号类别     
	private String transitem;//交易项目
	private String fid;//数据库表组件
	private String mfrchantno;//商户编号
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	public String getMfrchantno() {
		return mfrchantno;
	}
	public void setMfrchantno(String mfrchantno) {
		this.mfrchantno = mfrchantno;
	}
	public String getTerminalno() {
		return terminalno;
	}
	public void setTerminalno(String terminalno) {
		this.terminalno = terminalno;
	}
	public String getOrderno() {
		return orderno;
	}
	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}
	public String getCardno() {
		return cardno;
	}
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getTransstat() {
		return transstat;
	}
	public void setTransstat(String transstat) {
		this.transstat = transstat;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTranstype() {
		return transtype;
	}
	public void setTranstype(String transtype) {
		this.transtype = transtype;
	}
	public String getCardtype() {
		return cardtype;
	}
	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}
	public String getTransitem() {
		return transitem;
	}
	public void setTransitem(String transitem) {
		this.transitem = transitem;
	}
	
	
}
