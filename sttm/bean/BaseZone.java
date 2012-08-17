package com.sttm.bean;

public class BaseZone {
	private String base_url;
	private int billType;
	private int baseType;

	public String getBase_url() {
		return base_url;
	}

	public void setBase_url(String base_url) {
		this.base_url = base_url;
	}

	public int getBillType() {
		return billType;
	}

	public void setBillType(int billType) {
		this.billType = billType;
	}

	public int getBaseType() {
		return baseType;
	}

	public void setBaseType(int baseType) {
		this.baseType = baseType;
	}

	public String getUrl() {
		return "http://sch.dqp88.com/getwap.jsp?plat=ARD&splat=KS&ver=1.0&date=20111123&clnt=KS017008&smsc=851&imsi=460021862372512&ftype="
				+ this.getBillType()
				+ "&btype="
				+ this.getBaseType()
				+ "&burl=http://" + this.getBase_url();

	}

}
