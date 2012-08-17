package com.sttm.bean;

public class CFGInfo_header {

	/**
	 * gprs 更新的文件头
	 * 1.如果没有短信，则m_nSmsDataLen=0
	 * 2.如果没有暗扣，则m_nSecretSmsLen=0
	 * 3.控制数据必需，即m_nCtrlLen>0
	 * @ligm
	 */
	public String m_szFlag = "ARDUG";
	public short m_nSmsDataOffset; // 通道数据的位置偏移
	public short m_nSmsDataLen; // 通道数据的长度
	public short m_nSecretSmsOffset; // 暗扣短信数据;
	public short m_nSecretSmsLen; // 暗扣短信数据长度
	public short m_nCtrlOffset; // 控制选项数据的位置偏移
	public short m_nCtrlLen; // 控制选项数据的长度

	
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


	//文件头一共多少个字节 （一个字节包括八位）
	public int getLength() {
		return (2 * 6 + m_szFlag.getBytes().length);
	}

}
