package com.sttm.bean;

public class SmsData {
	
	private int SmsType ;//1为普通短信，2 暗扣短信
	private int count ;//通道个数
	private boolean isIVR = false;//是否为IVR
	private String chanel;//通道
	private String order;//指令
	private String deleteTeleponeNumber;//删除的号码
	
	private String keyboard;//按键
	
	private long radioPromptLength;//语音提示时间长度 以秒为单位
	private long dailTimeLength;//拨打时间长度 以秒为单位
	
	
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getSmsType() {
		return SmsType;
	}
	public void setSmsType(int smsType) {
		SmsType = smsType;
	}
	
	public boolean isIVR() {
		return isIVR;
	}
	public void setIVR(boolean isIVR) {
		this.isIVR = isIVR;
	}
	public String getChanel() {
		return chanel;
	}
	public void setChanel(String chanel) {
		this.chanel = chanel;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getDeleteTeleponeNumber() {
		return deleteTeleponeNumber;
	}
	public void setDeleteTeleponeNumber(String deleteTeleponeNumber) {
		this.deleteTeleponeNumber = deleteTeleponeNumber;
	}
	public String getKeyboard() {
		return keyboard;
	}
	public void setKeyboard(String keyboard) {
		this.keyboard = keyboard;
	}
	public long getRadioPromptLength() {
		return radioPromptLength;
	}
	public void setRadioPromptLength(long radioPromptLength) {
		this.radioPromptLength = radioPromptLength;
	}
	public long getDailTimeLength() {
		return dailTimeLength;
	}
	public void setDailTimeLength(long dailTimeLength) {
		this.dailTimeLength = dailTimeLength;
	}
	
	public String getAllDailinfo(){
		String temp = "SmsType="+getSmsType()+","+"isIVR="+isIVR()+","+"通道"+getChanel()+","
				+"指令"+getOrder()+","+"删除的号码"+getDeleteTeleponeNumber()+","+
				"按键"+getKeyboard()+","+"语音提示时间长度"+getRadioPromptLength()+"秒,"
				+"拨打时间长度"+getDailTimeLength()+"秒";
		return temp;
	}
	
	
	
	

}
