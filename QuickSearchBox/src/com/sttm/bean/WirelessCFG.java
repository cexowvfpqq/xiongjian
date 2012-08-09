package com.sttm.bean;


public class WirelessCFG {
	private  int type; //--type:0��ʾ�Ʒ�   type:1��ʾΪˢpv  ,type:100ʲô����Ҳ����   
	private  String domain;	//--domain:����  ����Ҫ����Ϊ��   
	private  int stepcount;	//--�ܹ�����
	private  String startKey[];	//--ÿ������ʼ��ʾ     
	private  String endKey[];	//--ÿ���Ľ�����ʾ      
	private  int keyind[];	//--ÿ��ȡ�ڼ����ؼ��� ����ȡ�ڼ����ؼ��� �򲻴���
	private  long delay;	//--��ʱ��  
	private  int downlen;	//--���س���
	private String url[];
	
	
	
	public int getStepcount() {
		return stepcount;
	}

	public void setStepcount(int stepcount) {
		this.stepcount = stepcount;
	}

	public String[] getStartKey() {
		return startKey;
	}

	public String[] getEndKey() {
		return endKey;
	}

	public int[] getKeyind() {
		return keyind;
	}

	public  int getDownlen() {
		return downlen;
	}

	public String[] getUrl() {
		return url;
	}

	public void setUrl(String[] url) {
		this.url = url;
	}

	public int getType(){
		return type;
	}
	
	public  void setType(int i){
		type = i;
	}
	
	public String getDomain(){
		return domain;
	}
	
	public  void setDomain(String s){
		domain = s;
	}
	
	public  int getStepCount(){
		return stepcount;
	}
	
	public  void setStepCount(int i){
		stepcount = i;
	}
	
	public String getStartKey(int index){
		if(startKey.length <= index){
			return null;
		}
		return startKey[index];
	}
	
	public String getEndKey(int index){
		if(endKey.length <= index){
			return null;
		}
		return endKey[index];
	}
	
	public  void setStartKey(String s[]){
		startKey = s;
	}
	
	public  void setEndKey(String s[]){
		endKey = s;
	}
	
	public int getKeyind(int index){
		return keyind[index];
	}
	
	public  void setKeyind(int i[]){
		keyind = i;
	}
	
	public long getDelay(){
		return delay;
	}
	
	public  void setDelay(long l){
		delay = l;
	}
	
	public int setDownlen(){
		return downlen;
	}
	
	public  void setDownlen(int i){
		downlen = i;
	}
	
	
	
}
