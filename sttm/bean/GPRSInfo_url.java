package com.sttm.bean;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import com.sttm.util.KsoCache;
import com.sttm.util.KsoHelper;

public class GPRSInfo_url {
	/*
	 * ���ûȡ���������ĺ��룬IMSI�룬�����
	smsc=000
	imsi=000000000000000
	rce=00000000000000
	 */
	
	
	private final String BASE_URL = "http://www.shuihubinggan.com/";
	private String plat = "ARD";//�ֻ�ƽ̨
	private String splat = "KS";//��˾��־
	private String ver = "1.0";//����汾��
	private String date ;//�����ʱ��
	private String clnt ;//�ͻ����
	private String smsc = "000";//�������ĺ���
	private String imsi = "000000000000000";//IMSI��
	private String rce = "00000000000000" ;//�����
	private String fname;//�ļ���;
	
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
		gprs_url.setClnt((String) cache.getValue("curstomID"));//�ͻ�ID
		if(cache.getValue("SmsCenterNumber") != null){
			gprs_url.setSmsc((String) cache.getValue("SmsCenterNumber"));//�������ĺ���
			
		}
		
		gprs_url.setImsi((String) cache.getValue("IMSI"));//IMSI��
		gprs_url.setRce(KsoHelper.getRandomCode());//�����
		try {
			gprs_url.setDate(KsoHelper.getVersionDate(context));
			gprs_url.setVer(KsoHelper.getAppVersionName(context));
		} catch (NameNotFoundException e) {
			
			e.printStackTrace();
		}//�汾ʱ��
		
		gprs_url.setFname(fileName);//�ļ���
		return gprs_url;
	}
	
	

}
