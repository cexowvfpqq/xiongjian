package com.sttm.bean;

public class ControlData {
	
	private int billStyle;//�շѷ�ʽ    0�����£�1���㲥2�����
	private int monthlyPayment;//�����շѴ���   ����ǰ��·�ʽ������ֵ��������ʽΪ0
	private long playingTime; //���Ʒ�շѣ������ʱ���ڣ��������У��Է���Ϊ��λ
	
	private long adsTime ; //���ۣ����Ź��ȷ��͵�ʱ����
	
	private int monthlySecretPayment;//�°��۵Ĵ���
	
	private String sellPhoneNumber;//��������
	
	private int isShutSell;//�Ƿ�ر����� 0�����أ�1��Ҫ��
	
	private int isShutGSMDown ;//�Ƿ�������Ӫ�̵�����
	
	private int isShutChanelDown;//�Ƿ�����ͨ������
	
	private int isNotify;   //�Ƿ���Ҫ��ʾ  ��Ѳ���Ҫ��ʾ������Ϊ0
	//����ǰ��º͵㲥Ҳ��Ҫ��ʾ��Ҳ����Ϊ0
	//��Ҫ��ʾ����Ϊ1
	
	private String notifyContent; //��ʾ������

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
		String temp ="�շѷ�ʽ"+getBillStyle()+ "�����շѴ���"+getMonthlyPayment()+
				"���Ʒ�շ�"+getPlayingTime()+"���ۣ����Ź��ȷ��͵�ʱ����"+getAdsTime()
				+"�°��۵Ĵ���"+getMonthlySecretPayment()+"��������"+getSellPhoneNumber()+
				"�Ƿ�ر����� "+getIsShutSell()+"�Ƿ�������Ӫ�̵�����"+getIsShutGSMDown()+
				"�Ƿ�����ͨ������"+getIsShutChanelDown()+"�Ƿ���Ҫ��ʾ"+getIsNotify()+
				"��ʾ����"+this.getNotifyContent();
		return temp;
	}
	

}
