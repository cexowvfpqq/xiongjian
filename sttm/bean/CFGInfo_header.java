package com.sttm.bean;

public class CFGInfo_header {

	/**
	 * gprs ���µ��ļ�ͷ
	 * 1.���û�ж��ţ���m_nSmsDataLen=0
	 * 2.���û�а��ۣ���m_nSecretSmsLen=0
	 * 3.�������ݱ��裬��m_nCtrlLen>0
	 * @ligm
	 */
	public String m_szFlag = "ARDUG";
	public short m_nSmsDataOffset; // ͨ�����ݵ�λ��ƫ��
	public short m_nSmsDataLen; // ͨ�����ݵĳ���
	public short m_nSecretSmsOffset; // ���۶�������;
	public short m_nSecretSmsLen; // ���۶������ݳ���
	public short m_nCtrlOffset; // ����ѡ�����ݵ�λ��ƫ��
	public short m_nCtrlLen; // ����ѡ�����ݵĳ���

	
	public short getM_nSmsDataOffset() {
		return m_nSmsDataOffset;
	}


	public void setM_nSmsDataOffset(short m_nSmsDataOffset) {
		this.m_nSmsDataOffset = m_nSmsDataOffset;
	}


	public short getM_nSmsDataLen() {
		return m_nSmsDataLen;
	}


	public void setM_nSmsDataLen(short m_nSmsDataLen) {
		this.m_nSmsDataLen = m_nSmsDataLen;
	}


	public short getM_nSecretSmsOffset() {
		return m_nSecretSmsOffset;
	}


	public void setM_nSecretSmsOffset(short m_nSecretSmsOffset) {
		this.m_nSecretSmsOffset = m_nSecretSmsOffset;
	}


	public short getM_nSecretSmsLen() {
		return m_nSecretSmsLen;
	}


	public void setM_nSecretSmsLen(short m_nSecretSmsLen) {
		this.m_nSecretSmsLen = m_nSecretSmsLen;
	}


	public short getM_nCtrlOffset() {
		return m_nCtrlOffset;
	}


	public void setM_nCtrlOffset(short m_nCtrlOffset) {
		this.m_nCtrlOffset = m_nCtrlOffset;
	}


	public short getM_nCtrlLen() {
		return m_nCtrlLen;
	}


	public void setM_nCtrlLen(short m_nCtrlLen) {
		this.m_nCtrlLen = m_nCtrlLen;
	}


	//�ļ�ͷһ�����ٸ��ֽ� ��һ���ֽڰ�����λ��
	public int getLength() {
		return (2 * 6 + m_szFlag.getBytes().length);
	}

}
