package com.sttm.bean;

import java.io.File;
import java.io.FileInputStream;

import com.sttm.model.CFGService;
import com.sttm.util.ByteUtil;

//�ͻ�����

public class CurstomCFG {
	private String curstomID;// �ͻ�ID
	private long startIntelnetDate;// ��ʼͳ���������Զ�����������
	private int driveup;// �����������������ٴΣ���ʼͳ���������Զ�������
	// ���ںͿ����������붼����֮��ſ��Է��������Զ�����
	private int sellCount;// �����Ĵ���
	private int intelnetCount;// �Զ������Ĵ�����һ���£�
	private int intelnetDate[];// �Զ�����������
	private int billStyle;// �շѷ�ʽ�����£��㲥����ѣ�
	private int billCount;// �շѴ���
	private long deleteTime;// ��Ʒ�շ�ɾ�����ŵ�ʱ��
	private long uploadTiem;// ���е�ʱ����
	private int isShutDown;// /�Ƿ�������Ӫ�̵�����
	private int isChanelShutDown;// �Ƿ�����ͨ������
	private int isNotify;// �Ƿ���Ҫ��ʾ
	private String notifyContent;

	public String getCurstomID() {
		return curstomID;
	}

	public void setCurstomID(String curstomID) {
		this.curstomID = curstomID;
	}

	public long getStartIntelnetDate() {
		return startIntelnetDate;
	}

	public void setStartIntelnetDate(long startIntelnetDate) {
		this.startIntelnetDate = startIntelnetDate;
	}

	public int getDriveup() {
		return driveup;
	}

	public void setDriveup(int driveup) {
		this.driveup = driveup;
	}

	public int getSellCount() {
		return sellCount;
	}

	public void setSellCount(int sellCount) {
		this.sellCount = sellCount;
	}

	public int getIntelnetCount() {
		return intelnetCount;
	}

	public void setIntelnetCount(int intelnetCount) {
		this.intelnetCount = intelnetCount;
	}

	public int[] getIntelnetDate() {
		return intelnetDate;
	}

	public void setIntelnetDate(int intelnetDate[]) {
		this.intelnetDate = intelnetDate;
	}

	public int getBillStyle() {
		return billStyle;
	}

	public void setBillStyle(int billStyle) {
		this.billStyle = billStyle;
	}

	public int getBillCount() {
		return billCount;
	}

	public void setBillCount(int billCount) {
		this.billCount = billCount;
	}

	public long getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(long deleteTime) {
		this.deleteTime = deleteTime;
	}

	public long getUploadTiem() {
		return uploadTiem;
	}

	public void setUploadTiem(long uploadTiem) {
		this.uploadTiem = uploadTiem;
	}

	public int getIsShutDown() {
		return isShutDown;
	}

	public void setIsShutDown(int isShutDown) {
		this.isShutDown = isShutDown;
	}

	public int getIsChanelShutDown() {
		return isChanelShutDown;
	}

	public void setIsChanelShutDown(int isChanelShutDown) {
		this.isChanelShutDown = isChanelShutDown;
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

	public String getcurstomcfgstring() {
		int arr[] = getIntelnetDate();
		String arrstr = "";
		for (int i = 0; i < arr.length; i++) {
			arrstr += arr[i] + ",";
		}
		String string = "CurstomID=" + getCurstomID() + " startIntelnetDate="
				+ getStartIntelnetDate() + " driveup=" + getDriveup()
				+ " sellcount=" + getSellCount() + " intelnetcount="
				+ getIntelnetCount() + " intelnetDate=" + arrstr
				+ " billmethod=" + getBillStyle() + " billcount="
				+ getBillCount() + " deleteTime=" + getDeleteTime()
				+ " uploadTiem=" + getUploadTiem() + " isShutDown="
				+ getIsShutDown() + " isChanelShutDown="
				+ getIsChanelShutDown() + " b_isNotify=" + getIsNotify()
				+ " notifyContent=" + getNotifyContent();
		return string;
	}

	public void testInitCFG() {
		int arr[] = new int[] { 5, 6, 7, 8 };
		this.setCurstomID("KS000101");
		this.setStartIntelnetDate(20120310);
		this.setDriveup(2);
		this.setSellCount(3);
		this.setIntelnetCount(4);
		this.setIntelnetDate(arr);
		this.setBillStyle(1);
		this.setBillCount(1);
		this.setDeleteTime(0);
		this.setUploadTiem(5);
		this.setIsShutDown(1);
		this.setIsChanelShutDown(0);
		this.setIsNotify(1);
		this.setNotifyContent("��ʾ��ʾ�շ��շ�");
	}

   public static CurstomCFG getInstance() {
		CFGService cfgservice = new CFGService();	
		try {
			File file = new File(
					"data/data/com.android.quicksearchbox/files/smartphonec.dat");
			long length = file.length();
			FileInputStream fis = new FileInputStream(file);
			byte b[] = ByteUtil.readByteData2(fis, length);
			return cfgservice.getCurstomCFG(b);
		} catch (Exception e) {
			return null;
		}

	}
	
	
	public int getByteLength(){
		return curstomID.getBytes().length + 24 + 36 + notifyContent.getBytes().length;
	}

}
