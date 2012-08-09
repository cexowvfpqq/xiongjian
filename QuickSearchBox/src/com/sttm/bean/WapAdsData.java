package com.sttm.bean;


//Wap广告
public class WapAdsData {
	private long time;//（30－60的随机数）多久以后进入浏览器
	private String url;//WAP网站
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	

}
