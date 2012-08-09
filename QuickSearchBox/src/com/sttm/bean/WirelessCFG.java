package com.sttm.bean;


public class WirelessCFG {
	private  int type; //--type:0标示计费   type:1标示为刷pv  ,type:100什么类型也不是   
	private  String domain;	//--domain:域名  不需要域名为空   
	private  int stepcount;	//--总共几步
	private  String startKey[];	//--每步的起始标示     
	private  String endKey[];	//--每步的结束标示      
	private  int keyind[];	//--每步取第几个关键字 不需取第几个关键字 则不存在
	private  long delay;	//--延时秒  
	private  int downlen;	//--下载长度
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
