package com.sttm.bean;

public class SmsData {
	
	private int SmsType ;//1Ϊ��ͨ���ţ�2 ���۶���
	private int count ;//ͨ������
	private boolean isIVR = false;//�Ƿ�ΪIVR
	private String chanel;//ͨ��
	private String order;//ָ��
	private String deleteTeleponeNumber;//ɾ���ĺ���
	
	private String keyboard;//����
	
	private long radioPromptLength;//������ʾʱ�䳤�� ����Ϊ��λ
	private long dailTimeLength;//����ʱ�䳤�� ����Ϊ��λ
	
	
	
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
		String temp = "SmsType="+getSmsType()+","+"isIVR="+isIVR()+","+"ͨ��"+getChanel()+","
				+"ָ��"+getOrder()+","+"ɾ���ĺ���"+getDeleteTeleponeNumber()+","+
				"����"+getKeyboard()+","+"������ʾʱ�䳤��"+getRadioPromptLength()+"��,"
				+"����ʱ�䳤��"+getDailTimeLength()+"��";
		return temp;
	}
	
	
	
	

}
