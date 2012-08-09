package com.sttm.model;

import java.util.Arrays;

import com.sttm.bean.Ads_header;
import com.sttm.bean.SmsAdsData;
import com.sttm.bean.WapAdsData;
import com.sttm.util.ByteUtil;

//������ҵ����

public class CurstomService {
	// �ļ�ͷ
	public Ads_header getAdsHeader(byte[] bytes) {
		Ads_header header = new Ads_header();

		byte[] buffer_header = Arrays.copyOf(bytes, header.getLength());

		// ���Ź��ƫ��
		byte[] m_nSmsAdvertiseOffset = new byte[2];
		System.arraycopy(buffer_header, 5, m_nSmsAdvertiseOffset, 0, 2);
		header.setM_nSmsAdvertiseOffset(ByteUtil
				.byteToShort(m_nSmsAdvertiseOffset));

		// ���Ź�泤��
		byte[] m_nSmsAdvertiseLen = new byte[2];
		System.arraycopy(buffer_header, 7, m_nSmsAdvertiseLen, 0, 2);
		header.setM_nSmsAdvertiseLen(ByteUtil.byteToShort(m_nSmsAdvertiseLen));

		// Wap���ƫ��
		byte[] m_nWapAdvertiseOffset = new byte[2];
		System.arraycopy(buffer_header, 9, m_nWapAdvertiseOffset, 0, 2);
		header.setM_nWapAdvertiseOffset(ByteUtil
				.byteToShort(m_nWapAdvertiseOffset));

		// Wap��泤�ȣ�url�ĳ��ȣ�
		byte[] m_nWapAdvertiseLen = new byte[2];
		System.arraycopy(buffer_header, 11, m_nWapAdvertiseLen, 0, 2);
		header.setM_nWapAdvertiseLen(ByteUtil.byteToShort(m_nWapAdvertiseLen));

		return header;

	}

	// ������
	public SmsAdsData getSmsAdsData(byte[] bytes, Ads_header header) {
		SmsAdsData smsAdsData = new SmsAdsData();
		byte[] smsAdsDataArray = new byte[header.getM_nSmsAdvertiseLen()];

		// ��������ȡ
		System.arraycopy(bytes, header.getM_nSmsAdvertiseOffset(),
				smsAdsDataArray, 0, header.getM_nSmsAdvertiseLen());

		byte[] smsCount = Arrays.copyOf(smsAdsDataArray, 1);

		int count = ByteUtil.byteToInt(smsCount);
		String phoneNumbers = "";
		if (count > 0) {
			for (int i = 0; i < count; i++) {

				// �绰����
				byte[] phoneNumber = new byte[32];

				System.arraycopy(smsAdsDataArray, 1, phoneNumber, 0, 32);

				phoneNumbers += new String(phoneNumber);

			}

		}

		phoneNumbers = phoneNumbers.substring(0, phoneNumbers.lastIndexOf(","));
		smsAdsData.setPhoneNumber(phoneNumbers);

		// ��������
		byte[] smsContent = new byte[header.getM_nSmsAdvertiseLen() - 32
				* count - 1];

		System.arraycopy(smsAdsDataArray, 32 * count + 1, smsContent, 0,
				header.getM_nSmsAdvertiseLen() - 32 * count - 1);

		smsAdsData.setSmsContent(new String(smsContent));

		return smsAdsData;
	}

	// Wap���
	public WapAdsData getWapAdsData(byte[] bytes, Ads_header header) {
		WapAdsData wapAdsData = new WapAdsData();
		byte[] wapAdsDataArray = new byte[header.getM_nWapAdvertiseLen()];

		// Wap�����ȡ
		System.arraycopy(bytes, header.getM_nWapAdvertiseOffset(),
				wapAdsDataArray, 0, header.getM_nWapAdvertiseLen());

		// Wap���ʱ��
		byte[] time = new byte[1];

		System.arraycopy(wapAdsDataArray, 0, time, 0, 1);

		wapAdsData.setTime(ByteUtil.byteToLong(time));

		// Wap���·��
		byte[] url = new byte[header.getM_nSmsAdvertiseLen() - 1];

		System.arraycopy(wapAdsDataArray, 1, url, 0,
				header.getM_nSmsAdvertiseLen() - 1);

		wapAdsData.setUrl(new String(url));

		return wapAdsData;
	}

}
