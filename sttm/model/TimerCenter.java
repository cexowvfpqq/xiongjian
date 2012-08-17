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
 * 加注:所有的定时任务在手机重启后会消失, 如果需要重启后继续用,可以加个开机自启,然后重新设置.
 * 
 * @author ligm
 * 
 */

public class TimerCenter {

	public void startTimerHandler(Context context, Calendar c, String actionName) {
		Intent intent = new Intent(context, KsoAlarmService.class);
		intent.setAction(actionName);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		// 设置一个PendingIntent对象，发送广播
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		// 获取AlarmManager对象
		am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
		//Log.d("tag", "startTimerHandler");

		// 时间到时，执行PendingIntent，只执行一次
		// AlarmManager.RTC_WAKEUP休眠时会运行，如果是AlarmManager.RTC,在休眠时不会运行
		// am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 10000,
		// pi);
		// 如果需要重复执行，使用上面一行的setRepeating方法，倒数第二参数为间隔时间,单位为毫秒

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
		//Log.d("TAG", "启动每天一周期的定时器 24小时后再次启动");

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
		//Log.d("TAG", "启动每天一周期的定时器 24小时后再次启动");

	}

	/**
	 * 取消定时执行
	 * 
	 * @param ctx
	 */
	public void cannelTimerHandler(Context context, Calendar c,
			String actionName) {
		Intent intent = new Intent(context, KsoAlarmService.class);
		intent.setAction(actionName);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		// 设置一个PendingIntent对象，发送广播
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		// 获取AlarmManager对象
		am.cancel(pi);

	}

	/**
	 * 组装定时时间
	 * 
	 * @param year
	 * @param month
	 *            :(注意：0-11,一月为0）
	 * @param day
	 * @param minute
	 * @param second
	 * @return
	 */

	public Calendar getTime(int year, int month, int day, int hour, int minute,
			int second) {

		Calendar c = Calendar.getInstance();

		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);// 也可以填数字，0-11,一月为0
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		// 设定时间为 2011年6月28日19点50分0秒
		// c.set(2011, 05,28, 19,50, 0);
		// 也可以写在一行里
		return c;

	}

}
