package com.example.modle;


public class RecordModle  {
	//�ն˱��    terminalno   
	//������      orderno          
	// ����       cardno         
	//����ʱ��    datetime   
	//����״̬    transstat      
	//���׽��    amount       
	//��������    transtype   mfrchantno;//�̻����  
	//�������     cardtype        
	//������Ŀ      transitem      fid;//���ݿ�����
	private String terminalno; //�ն˱�� 
	private String orderno; //������  
	private String cardno; // ���� 
	private String datetime; //����ʱ�� 
	private String transstat;//����״̬
	private String amount;//���׽��
	private String transtype;//��������
	private String cardtype;//�������     
	private String transitem;//������Ŀ
	private String fid;//���ݿ�����
	private String mfrchantno;//�̻����
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
