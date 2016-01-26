package com.example.untils;
	/**
	 * 交易记录类
	 * @author jacyayj
	 *
	 */
public class Recordhistory {
	// 终端编号 terminalno
	// 订单号 orderno
	// 卡号 cardno
	// 日期时间 datetime
	// 交易状态 transstat
	// 交易金额 amount
	// 交易类型 transtype
	// 卡号类别 cardtype
	// 交易项目 transitem
	// 数据库表主键 fid
	// 商户编号 mfrchantno
	private String terminalno = null;
	private String orderno = null;
	private String cardno = null;
	private String datetime = null;
	private String transstat = null;
	private String amount = null;
	private String transtype = null;
	private String cardtype = null;
	private String transitem = null;
	private String fid = null;
	private String merchantno = null;
	private String mfrchantno = null;
	private String receiptFile = null;

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

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getMerchantno() {
		return merchantno;
	}

	public void setMerchantno(String merchantno) {
		this.merchantno = merchantno;
	}

	public String getMfrchantno() {
		return mfrchantno;
	}

	public void setMfrchantno(String mfrchantno) {
		this.mfrchantno = mfrchantno;
	}

	public String getReceiptFile() {
		return receiptFile;
	}

	public void setReceiptFile(String receiptFile) {
		this.receiptFile = receiptFile;
	}
}
