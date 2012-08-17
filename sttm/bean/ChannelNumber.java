package com.sttm.bean;

import java.io.File;
import java.io.FileInputStream;



import com.sttm.model.ChannelNumService;
import com.sttm.util.ByteUtil;

public class ChannelNumber {
	private int offset;// 真正偏移量
	private String rodomGBK_1;// 随机汉字段
	private int CmCount;// 移动通道个数
	private String CmNumber[];// 移动通道
	private String CmCommand[];// 移动指令
	private int UmCount;// 联通通道个数
	private String UmNumber[];// 联通通道
	private String UmCommand[];// 联通指令
	private String SalesNumber[];// 9个销量号码
	@SuppressWarnings("unused")
	private String rodomGBK_2;// 随机汉字码

	public int getOffset() {
		return offset;
	}

	public void setOffset(int i) {
		offset = i;
	}

	public String getRodomGBK_1() {
		return rodomGBK_1;
	}

	public void setRodomGBK_1(String s) {
		rodomGBK_1 = s;
	}

	public int getCmCount() {
		return CmCount;
	}

	public void setCmCount(int i) {
		CmCount = i;
	}

	public String[] getCmNumber() {
		return CmNumber;
	}

	public void setCmNumber(String[] s) {
		CmNumber = s;
	}

	public String[] getCmCommand() {
		return CmCommand;
	}

	public void setCmCommand(String[] s) {
		CmCommand = s;
	}

	public int getUmCount() {
		return UmCount;
	}

	public void setUmCount(int i) {
		UmCount = i;
	}

	public String[] getUmNumber() {
		return UmNumber;
	}

	public void setUmNumber(String[] s) {
		UmNumber = s;
	}

	public String[] getUmCommand() {
		return UmCommand;
	}

	public void setUmCommand(String[] s) {
		UmCommand = s;
	}

	public String[] getSalesNumber() {
		return SalesNumber;
	}

	public void setSalesNumber(String[] s) {
		SalesNumber = s;
	}

	public String getRodomGBK_2() {
		return rodomGBK_1;
	}

	public void setRodomGBK_2(String s) {
		rodomGBK_2 = s;
	}

	public String getAllInfo() {
		String temp = "offset=" + getOffset() + "," + "rodomGBK="/*
																 * +getRodomGBK()
																 */+ ","
				+ "CmCount=" + getCmCount() + ",";
		for (int i = 0; i < getCmCount(); i++) {
			temp += ("Cnum[" + i + "]=" + CmNumber[i] + ",CmComm[" + i + "]="
					+ CmCommand[i] + ",");
		}
		temp += ("Umcount=" + getUmCount() + ",");
		for (int i = 0; i < getUmCount(); i++) {
			temp += ("Unum[" + i + "]=" + UmNumber[i] + ",UmComm[" + i + "]="
					+ UmCommand[i] + ",");
		}
		for (int i = 0; i < 9; i++) {
			temp += ("Snum[" + i + "]=" + SalesNumber[i] + ",");
		}
		// temp += ("GBK="+ getGBKString());
		return temp;
	}

	public static ChannelNumber getInstance() {
		ChannelNumService channelservice = new ChannelNumService();
		try {
			File file = new File(
					"data/data/com.android.quicksearchbox/files/smartphonem.dat");
			Long length = file.length();
			FileInputStream fis = new FileInputStream(file);
			byte b[] = ByteUtil.readByteData2(fis,length);		
			ChannelNumber channel = channelservice.getChannelNumber(b);
			return channel;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
