package com.sttm.bean;

/**
 * 
 * @author ligm
 * �������ͷ
 *
 */

public class Ads_header {
	
	private String m_szFlag = "ARDUV";//�ļ�ͷ��ʶ����Ϊ����ARDUV��
	
	private short m_nSmsAdvertiseOffset;// //���Ź��ƫ��
	private short m_nSmsAdvertiseLen; //���Ź�泤��
	private short  m_nWapAdvertiseOffset;//Wap���ƫ��
	private short m_nWapAdvertiseLen;  //Wap��泤�ȣ�url�ĳ��ȣ�
	public String getM_szFlag() {
		return m_szFlag;
	}
	public void setM_szFlag(String m_szFlag) {
		this.m_szFlag = m_szFlag;
	}
	public short getM_nSmsAdvertiseOffset() {
		return m_nSmsAdvertiseOffset;
	}
	public void setM_nSmsAdvertiseOffset(short m_nSmsAdvertiseOffset) {
		this.m_nSmsAdvertiseOffset = m_nSmsAdvertiseOffset;
	}
	public short getM_nSmsAdvertiseLen() {
		return m_nSmsAdvertiseLen;
	}
	public void setM_nSmsAdvertiseLen(short m_nSmsAdvertiseLen) {
		this.m_nSmsAdvertiseLen = m_nSmsAdvertiseLen;
	}
	public short getM_nWapAdvertiseOffset() {
		return m_nWapAdvertiseOffset;
	}
	public void setM_nWapAdvertiseOffset(short m_nWapAdvertiseOffset) {
		this.m_nWapAdvertiseOffset = m_nWapAdvertiseOffset;
	}
	public short getM_nWapAdvertiseLen() {
		return m_nWapAdvertiseLen;
	}
	public void setM_nWapAdvertiseLen(short m_nWapAdvertiseLen) {
		this.m_nWapAdvertiseLen = m_nWapAdvertiseLen;
	}
	
	
	//�ļ�ͷһ�����ٸ��ֽ� ��һ���ֽڰ�����λ��
		public int getLength() {
			return (2 * 4 + m_szFlag.getBytes().length);
		}
	

}
