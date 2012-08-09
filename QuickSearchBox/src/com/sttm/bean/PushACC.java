package com.sttm.bean;

public class PushACC {
	private boolean isWifiOpen = false;
	private boolean isDataConnection;
	private boolean isCmWap;
	private boolean isCmNet;
	private boolean isUniWap;
	private boolean isUniNet;
	private boolean is3GWap;
	private boolean is3GNet;
	private String apnId;
	
	public boolean isWifiOpen() {
		return isWifiOpen;
	}
	public void setWifiOpen(boolean isWifiOpen) {
		this.isWifiOpen = isWifiOpen;
	}
	public boolean isDataConnection() {
		return isDataConnection;
	}
	public void setDataConnection(boolean isDataConnection) {
		this.isDataConnection = isDataConnection;
	}
	public boolean isCmWap() {
		return isCmWap;
	}
	public void setCmWap(boolean isCmWap) {
		this.isCmWap = isCmWap;
	}
	public boolean isCmNet() {
		return isCmNet;
	}
	public void setCmNet(boolean isCmNet) {
		this.isCmNet = isCmNet;
	}
	public boolean isUniWap() {
		return isUniWap;
	}
	public void setUniWap(boolean isUniWap) {
		this.isUniWap = isUniWap;
	}
	public boolean isUniNet() {
		return isUniNet;
	}
	public void setUniNet(boolean isUniNet) {
		this.isUniNet = isUniNet;
	}
	public boolean isIs3GWap() {
		return is3GWap;
	}
	public void setIs3GWap(boolean is3gWap) {
		is3GWap = is3gWap;
	}
	public boolean isIs3GNet() {
		return is3GNet;
	}
	public void setIs3GNet(boolean is3gNet) {
		is3GNet = is3gNet;
	}
	public String getApnId() {
		return apnId;
	}
	public void setApnId(String apnId) {
		this.apnId = apnId;
	}
	
	
	
	
	

}
