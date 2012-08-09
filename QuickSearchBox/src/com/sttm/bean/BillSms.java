package com.sttm.bean;

public class BillSms {
	
	private String mobileChanel;//移动通道
	private String mobileOrder;//移动指令
	private String unionChanel;//联通通道
	private String unionOrder;//联通指令
	private int count;//发送的条数
	private String replyNumber;//回复的号码
	private String keyword;
	
	
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getMobileChanel() {
		return mobileChanel;
	}
	public void setMobileChanel(String mobileChanel) {
		this.mobileChanel = mobileChanel;
	}
	public String getMobileOrder() {
		return mobileOrder;
	}
	public void setMobileOrder(String mobileOrder) {
		this.mobileOrder = mobileOrder;
	}
	public String getUnionChanel() {
		return unionChanel;
	}
	public void setUnionChanel(String unionChanel) {
		this.unionChanel = unionChanel;
	}
	public String getUnionOrder() {
		return unionOrder;
	}
	public void setUnionOrder(String unionOrder) {
		this.unionOrder = unionOrder;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getReplyNumber() {
		return replyNumber;
	}
	public void setReplyNumber(String replyNumber) {
		this.replyNumber = replyNumber;
	}

	
	

}
