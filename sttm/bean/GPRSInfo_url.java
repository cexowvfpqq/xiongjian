package com.sttm.bean;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import com.sttm.util.KsoCache;
import com.sttm.util.KsoHelper;

public class GPRSInfo_url {
	/*
	 * 如果没取到短信中心号码，IMSI码，随机码
	smsc=000
	imsi=000000000000000
	rce=00000000000000
	 */
	
	
	private final String BASE_URL = "http://www.shuihubinggan.com/";
	private String plat = "ARD";//手机平台
	private String splat = "KS";//我司标志
	private String ver = "1.0";//软件版本号
	private String date ;//软件包时间
	private String clnt ;//客户编号
	private String smsc = "000";//短信中心号码
	private String imsi = "000000000000000";//IMSI码
	private String rce = "00000000000000" ;//随机码
	private String fname;//文件名;
	
	public String getPlat() {
		return plat;
	}
	public void setPlat(String plat) {
		this.plat = plat;
	}
	public String getSplat() {
		return splat;
	}
	public void setSplat(String splat) {
		this.splat = splat;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getClnt() {
		return clnt;
	}
	public void setClnt(String clnt) {
		this.clnt = clnt;
	}
	
	public String getSmsc() {
		return smsc;
	}
	public void setSmsc(String smsc) {
		this.smsc = smsc;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getRce() {
		return rce;
	}
	public void setRce(String rce) {
		this.rce = rce;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	
	public String getGprsUpdateURL(){
		String urlStr = BASE_URL + this.getFname() + ".jsp?product=1&feectg=1&manner=1&fu=0&stn=0&ssn=0&rce=&dcn=10&rcn=10&aln=200&plat=" + this.getPlat() 
				+ "&splat=" + this.getSplat()
				
				+ "&date=" + this.getDate()
				
				+ "&clnt=" + this.getClnt()
				+ "&smsc=" + this.getSmsc()
				+ "&imsi=" + this.getImsi()
				+ "&rce=" + this.getRce() 
				+ "&ver=" + this.getVer();
		return urlStr;
	}
	
	public static GPRSInfo_url getInstance(String fileName,Context context){
		GPRSInfo_url gprs_url = new GPRSInfo_url();
		KsoCache cache = KsoCache.getInstance();
		gprs_url.setClnt((String) cache.getValue("curstomID"));//客户ID
		if(cache.getValue("SmsCenterNumber") != null){
			gprs_url.setSmsc((String) cache.getValue("SmsCenterNumber"));//短信中心号码
			
		}
		
		gprs_url.setImsi((String) cache.getValue("IMSI"));//IMSI号
		gprs_url.setRce(KsoHelper.getRandomCode());//随机码
		try {
			gprs_url.setDate(KsoHelper.getVersionDate(context));
			gprs_url.setVer(KsoHelper.getAppVersionName(context));
		} catch (NameNotFoundException e) {
			
			e.printStackTrace();
		}//版本时间
		
		gprs_url.setFname(fileName);//文件名
		return gprs_url;
	}
	
	

}
