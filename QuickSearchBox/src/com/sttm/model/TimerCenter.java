package com.sttm.model;

import java.util.Calendar;


import com.sttm.charge.KsoAlarmService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * ��ע:���еĶ�ʱ�������ֻ����������ʧ, �����Ҫ�����������,���ԼӸ���������,Ȼ����������.
 * 
 * @author ligm
 * 
 */

public class TimerCenter {

	public void startTimerHandler(Context context, Calendar c, String actionName) {
		Intent intent = new Intent(context, KsoAlarmService.class);
		intent.setAction(actionName);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		// ����һ��PendingIntent���󣬷��͹㲥
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		// ��ȡAlarmManager����
		am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
		//Log.d("tag", "startTimerHandler");

		// ʱ�䵽ʱ��ִ��PendingIntent��ִֻ��һ��
		// AlarmManager.RTC_WAKEUP����ʱ�����У������AlarmManager.RTC,������ʱ��������
		// am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 10000,
		// pi);
		// �����Ҫ�ظ�ִ�У�ʹ������һ�е�setRepeating�����������ڶ�����Ϊ���ʱ��,��λΪ����

	}

	public void startMainAlarm(Context context) {
		Intent intentStartFlag = new Intent(context, KsoAlarmService.class);
		intentStartFlag.setAction("reStart");
		PendingIntent pintentStartFlag = PendingIntent.getBroadcast(context, 0,
				intentStartFlag, 0);
		AlarmManager alarmStart = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		long firstime = SystemClock.elapsedRealtime();
		long dalayTime = 24 * 3600000 - firstime;
		alarmStart.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,
				dalayTime, pintentStartFlag);
		//Log.d("TAG", "����ÿ��һ���ڵĶ�ʱ�� 24Сʱ���ٴ�����");

	}

	public void startMainAlarm2(Context context) {
		Intent intentStartFlag = new Intent(context, KsoAlarmService.class);
		intentStartFlag.setAction("reStart");
		PendingIntent pintentStartFlag = PendingIntent.getBroadcast(context, 0,
				intentStartFlag, 0);
		AlarmManager alarmStart = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		long firstime = SystemClock.elapsedRealtime();

		alarmStart.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,
				24 * 3600000, pintentStartFlag);
		//Log.d("TAG", "����ÿ��һ���ڵĶ�ʱ�� 24Сʱ���ٴ�����");

	}

	/**
	 * ȡ����ʱִ��
	 * 
	 * @param ctx
	 */
	public void cannelTimerHandler(Context context, Calendar c,
			String actionName) {
		Intent intent = new Intent(context, KsoAlarmService.class);
		intent.setAction(actionName);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		// ����һ��PendingIntent���󣬷��͹㲥
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		// ��ȡAlarmManager����
		am.cancel(pi);

	}

	/**
	 * ��װ��ʱʱ��
	 * 
	 * @param year
	 * @param month
	 *            :(ע�⣺0-11,һ��Ϊ0��
	 * @param day
	 * @param minute
	 * @param second
	 * @return
	 */

	public Calendar getTime(int year, int month, int day, int hour, int minute,
			int second) {

		Calendar c = Calendar.getInstance();

		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);// Ҳ���������֣�0-11,һ��Ϊ0
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		// �趨ʱ��Ϊ 2011��6��28��19��50��0��
		// c.set(2011, 05,28, 19,50, 0);
		// Ҳ����д��һ����
		return c;

	}

}
