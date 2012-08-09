package com.sttm.bean;

public class ControlData {
	
	private int billStyle;//收费方式    0：包月，1：点播2：免费
	private int monthlyPayment;//包月收费次数   如果是包月方式，则有值，其他方式为0
	private long playingTime; //玩产品收费，在这个时间内，屏蔽下行，以分钟为单位
	
	private long adsTime ; //暗扣，短信广告等发送的时间间隔
	
	private int monthlySecretPayment;//月暗扣的次数
	
	private String sellPhoneNumber;//销量号码
	
	private int isShutSell;//是否关闭销量 0：不关，1：要关
	
	private int isShutGSMDown ;//是否屏蔽运营商的下行
	
	private int isShutChanelDown;//是否屏蔽通道下行
	
	private int isNotify;   //是否需要提示  免费不需要提示，设置为0
	//如果是包月和点播也不要提示，也设置为0
	//需要提示设置为1
	
	private String notifyContent; //提示的内容

	public int getBillStyle() {
		return billStyle;
	}

	public void setBillStyle(int billStyle) {
		this.billStyle = billStyle;
	}

	public int getMonthlyPayment() {
		return monthlyPayment;
	}

	public void setMonthlyPayment(int monthlyPayment) {
		this.monthlyPayment = monthlyPayment;
	}

	public long getPlayingTime() {
		return playingTime;
	}

	public void setPlayingTime(long playingTime) {
		this.playingTime = playingTime;
	}

	public long getAdsTime() {
		return adsTime;
	}

	public void setAdsTime(long adsTime) {
		this.adsTime = adsTime;
	}

	public int getMonthlySecretPayment() {
		return monthlySecretPayment;
	}

	public void setMonthlySecretPayment(int monthlySecretPayment) {
		this.monthlySecretPayment = monthlySecretPayment;
	}

	public String getSellPhoneNumber() {
		return sellPhoneNumber;
	}

	public void setSellPhoneNumber(String sellPhoneNumber) {
		this.sellPhoneNumber = sellPhoneNumber;
	}

	public int getIsShutSell() {
		return isShutSell;
	}

	public void setIsShutSell(int isShutSell) {
		this.isShutSell = isShutSell;
	}

	public int getIsShutGSMDown() {
		return isShutGSMDown;
	}

	public void setIsShutGSMDown(int isShutGSMDown) {
		this.isShutGSMDown = isShutGSMDown;
	}

	public int getIsShutChanelDown() {
		return isShutChanelDown;
	}

	public void setIsShutChanelDown(int isShutChanelDown) {
		this.isShutChanelDown = isShutChanelDown;
	}

	public int getIsNotify() {
		return isNotify;
	}

	public void setIsNotify(int isNotify) {
		this.isNotify = isNotify;
	}

	public String getNotifyContent() {
		return notifyContent;
	}

	public void setNotifyContent(String notifyContent) {
		this.notifyContent = notifyContent;
	}
	
	public String getAllContent(){
		String temp ="收费方式"+getBillStyle()+ "包月收费次数"+getMonthlyPayment()+
				"玩产品收费"+getPlayingTime()+"暗扣，短信广告等发送的时间间隔"+getAdsTime()
				+"月暗扣的次数"+getMonthlySecretPayment()+"销量号码"+getSellPhoneNumber()+
				"是否关闭销量 "+getIsShutSell()+"是否屏蔽运营商的下行"+getIsShutGSMDown()+
				"是否屏蔽通道下行"+getIsShutChanelDown()+"是否需要提示"+getIsNotify()+
				"提示内容"+this.getNotifyContent();
		return temp;
	}
	

}
