package com.sttm.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.sttm.bean.APN;
import com.sttm.bean.WapApnName;

public class WapApnHelper {

	private final ConnectivityManager conManager;
	private NetworkInfo info;
	private WifiManager wm;
	private TelephonyManager telephonyManager;

	private ContentResolver resolver;
	public static final Uri CURRENT_APN_URI = Uri
			.parse("content://telephony/carriers/preferapn");
	public static final Uri APN_LIST_URI = Uri
			.parse("content://telephony/carriers");

	/**
	 * ��������ʼ��
	 * 
	 * @param context
	 */
	public WapApnHelper(Context context) {

		conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		info = conManager.getActiveNetworkInfo();

		wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		resolver = context.getContentResolver();

	}

	/**
	 * ����APN����ǰ�����ֳ�
	 */

	public void saveState() {
		// �����ֳ�����

		// �ƶ����������Ƿ��״̬
		KsoCache.getInstance().reSetValue("mobileConnected",
				this.getMobileDataStatus());

		// wifi�Ƿ��״̬
		KsoCache.getInstance().reSetValue("wifiOpen", checkWifi());

		// APN��ʱ״̬
		KsoCache.getInstance().reSetValue("apnDedault", this.getCurrentAPNId());

	}

	/**
	 * �ָ��û�ԭ��������״̬
	 * 
	 * @return
	 */

	public void reSetNetState() {

		// �ָ��ƶ���������״̬
		if (KsoCache.getInstance().getValue("mobileConnected") != null) {
			toggleMobileData((Boolean) KsoCache.getInstance().getValue(
					"mobileConnected"));
		}

		// �ָ�WIFI״̬
		if (KsoCache.getInstance().getValue("wifiOpen") != null) {
			wm.setWifiEnabled((Boolean) KsoCache.getInstance().getValue(
					"wifiOpen"));
		}

		// �ָ�APN����״̬
		if (KsoCache.getInstance().getValue("apnDedault") != null) {
			setAPN((String) KsoCache.getInstance().getValue("apnDedault"));
		}

	}
	

	public void openWap() {

		// ��һ�����ر�WIFI�����ƶ�����

		wm.setWifiEnabled(false);
		try {
			Thread.sleep(5000);
		}catch(Exception e){
			
		}
		toggleMobileData(true);

		APN mAPN = getCurrentAPN();
		String apnName = mAPN.getApn();
		if (!apnName.contains("wap")) {

			List<APN> apns = getWapAPNList();
			if (apns != null && apns.size() > 0) {
				for (APN a : apns) {
					// ����ѡ��
					if (WapApnName.cwap != null && !"".equals(WapApnName.cwap)) {
						if (a.getApn().equals(WapApnName.cwap)) {
							setAPN(a.getId());
							break;

						}

					} else if (WapApnName.gwap != null
							&& !"".equals(WapApnName.gwap)) {
						// ���ѡ��3G
						if (a.getApn().equals(WapApnName.gwap)) {
							setAPN(a.getId());
							break;

						}
					}

				}

			} else {

				String apnID = addKsoAPN();
				setAPN(apnID);
			}

	  }

  }

	/**
	 * ��WAP���� ��һ���������ж��Ƿ����� �ڶ�������������ˣ��ж��������ͣ������WIFI����ر�WIFI���������ֳ�
	 * �����������APN�Ƿ���ã����õĻ��������Ƿ�APNΪWAP��ʽ ���Ĳ�������WAP��ʽ�����ȱ����ֳ�
	 * ���岽���г�ȫ��WAP���ӣ������WAP��ʽ�������ô�WAP���� ������������б�APN��û��WAP����㣬������WAP��ʽ�������ô�ΪWAP����
	 * 
	 * ���߲�����ǰû���������ӣ���� �ƶ�����
	 */

	public void openWap2() {

		// ��һ�����ر�WIFI�����ƶ�����
		if (checkNet()) {
			if (getNetworkType().equals("wifi")) {
				if (checkWifi() && wm != null) {
					wm.setWifiEnabled(false);
					if (!getMobileDataStatus()) {
						toggleMobileData(true);
					}
				}
			}
		} else {
			toggleMobileData(true);
		}

		APN mAPN = getCurrentAPN();
		String apnName = mAPN.getApn();
		if (!apnName.contains("wap")) {

			List<APN> apns = getWapAPNList();
			if (apns != null && apns.size() > 0) {
				for (APN a : apns) {
					// ����ѡ��
					if (WapApnName.cwap != null && !"".equals(WapApnName.cwap)) {
						if (a.getApn().equals(WapApnName.cwap)) {
							setAPN(a.getId());
							break;

						}

					} else if (WapApnName.gwap != null
							&& !"".equals(WapApnName.gwap)) {
						// ���ѡ��3G
						if (a.getApn().equals(WapApnName.gwap)) {
							setAPN(a.getId());
							break;

						}
					}

				}

			} else {

				String apnID = addKsoAPN();
				setAPN(apnID);
			}

		}

	}

	/*
	 * �õ��û�Ĭ��APN�����
	 */
	public APN getCurrentAPN() {
		APN apn = new APN();
		Cursor cur = null;
		try {
			cur = resolver.query(CURRENT_APN_URI, null, null, null, null);
			if (cur != null && cur.moveToFirst()) {
				String apnID = cur.getString(cur.getColumnIndex("_id"));
				String apnName = cur.getString(cur.getColumnIndex("apn"));
				String apnType = cur.getString(cur.getColumnIndex("type"));
				String current = cur.getString(cur.getColumnIndex("current"));
				apn.setId(apnID);
				apn.setApn(apnName);
				apn.setCurrent(current);
				apn.setType(apnType);
				return apn;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (cur != null) {
				cur.close();
			}
		}
		return null;
	}

	/*
	 * �õ��û�Ĭ��APN�����
	 */
	public String getCurrentAPNId() {
		String apnId = "";
		Cursor cur = null;
		try {
			cur = resolver.query(CURRENT_APN_URI, null, null, null, null);
			if (cur != null && cur.moveToFirst()) {
				apnId = cur.getString(cur.getColumnIndex("_id"));

				return apnId;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (cur != null) {
				cur.close();
			}
		}
		return "";
	}

	/**
	 * ��ȡ���е�WAP����� current��Ϊ�ձ�ʾ����ʹ�õ�APN
	 */
	public List<APN> getWapAPNList() {
		String tag = "Main.getAPNList()";

		String projection[] = { "_id,apn,type,current" };
		Cursor cr = resolver.query(APN_LIST_URI, projection, null, null, null);
		List<APN> list = new ArrayList<APN>();
		while (cr != null && cr.moveToNext()) {
			//Log.d(tag,cr.getString(cr.getColumnIndex("_id")) + "  "+ cr.getString(cr.getColumnIndex("apn")) + "  "+ cr.getString(cr.getColumnIndex("type")) + "  "+ cr.getString(cr.getColumnIndex("current")));
			if (cr.getString(cr.getColumnIndex("apn")) != null
					&& cr.getString(cr.getColumnIndex("apn")).contains("wap")
					&& !"".equals(cr.getString(cr.getColumnIndex("current")))) {

				APN a = new APN();
				a.setId(cr.getString(cr.getColumnIndex("_id")));
				a.setApn(cr.getString(cr.getColumnIndex("apn")));
				a.setType(cr.getString(cr.getColumnIndex("type")));
				a.setCurrent(cr.getString(cr.getColumnIndex("current")));
				list.add(a);
			}

		}
		if (cr != null)
			cr.close();
		return list;
	}

	/**
	 * ������һ��APN�����
	 * 
	 * @param name
	 * @param apn
	 * @param mcc
	 * @param numeric
	 * @return
	 */
	public int addAPN(String name, String apn, String mcc, String numeric) {
		int id = -1;
		ContentValues values = new ContentValues();

		values.put("name", name);
		values.put("apn", apn);
		values.put("mcc", mcc);
		values.put("mnc", numeric);
		values.put("numeric", numeric);

		/*
		 * values.put("name", "ksowap"); values.put("apn", "cmwap");
		 * values.put("mcc", "460"); values.put("mnc", "01");
		 * values.put("numeric", "46001");
		 */

		Cursor c = null;
		Uri newRow = resolver.insert(APN_LIST_URI, values);
		if (newRow != null) {
			c = resolver.query(newRow, null, null, null, null);
			int idIndex = c.getColumnIndex("_id");
			c.moveToFirst();
			id = c.getShort(idIndex);
		}
		if (c != null)
			c.close();
		return id;
	}

	public String addKsoAPN() {
		String apn = "";
		if (getARS2() == 1) {
			apn = "cmwap";
		} else if (getARS2() == 2) {
			apn = "uniwap";
		}
		if (getARS2() == 3) {
			apn = "ctwap";
		}
		String id = "";
		ContentValues values = new ContentValues();
		values.put("name", "ksowap");
		values.put("apn", apn);
		values.put("mcc", "460");
		values.put("mnc", "01");
		values.put("numeric", "46001");
		Cursor c = null;
		Uri newRow = resolver.insert(APN_LIST_URI, values);
		if (newRow != null) {
			c = resolver.query(newRow, null, null, null, null);
			// int idIndex = c.getColumnIndex("_id");
			c.moveToFirst();
			id = c.getString(c.getColumnIndex("_id"));
			// id = c.getShort(idIndex);
		}
		if (c != null)
			c.close();
		return id;
	}

	/**
	 * Ϊ�û�����һ��WAP�ӽ�
	 * 
	 * @param id
	 */
	public void setAPN(String id) {
		//Log.d("test", id + "=======");
		ContentValues values = new ContentValues();
		values.put("apn_id", id);

		resolver.update(CURRENT_APN_URI, values, null, null);
	}

	/**
	 * �õ���ʱAPN���ͣ����磢cmwap,cmnet,3gwap�ȵȡ�
	 * 
	 * @return
	 */
	public String getAPNType() {
		String currentAPN = info.getExtraInfo();
		return currentAPN;

	}

	private boolean getMobileDataStatus() {

		Class<?> conMgrClass = null; // ConnectivityManager��
		Field iConMgrField = null; // ConnectivityManager���е��ֶ�
		Object iConMgr = null; // IConnectivityManager�������
		Class<?> iConMgrClass = null; // IConnectivityManager��
		Method getMobileDataEnabledMethod = null; // setMobileDataEnabled����

		try {
			// ȡ��ConnectivityManager��
			conMgrClass = Class.forName(conManager.getClass().getName());
			// ȡ��ConnectivityManager���еĶ���mService
			iConMgrField = conMgrClass.getDeclaredField("mService");
			// ����mService�ɷ���
			iConMgrField.setAccessible(true);
			// ȡ��mService��ʵ������IConnectivityManager
			iConMgr = iConMgrField.get(conManager);
			// ȡ��IConnectivityManager��
			iConMgrClass = Class.forName(iConMgr.getClass().getName());
			// ȡ��IConnectivityManager���е�getMobileDataEnabled(boolean)����
			getMobileDataEnabledMethod = iConMgrClass
					.getDeclaredMethod("getMobileDataEnabled");
			// ����getMobileDataEnabled�����ɷ���
			getMobileDataEnabledMethod.setAccessible(true);
			// ����getMobileDataEnabled����
			return (Boolean) getMobileDataEnabledMethod.invoke(iConMgr);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		}
		return false;
	}

	/**
	 * �ƶ����翪��
	 */
	private void toggleMobileData(boolean enabled) {
		Class<?> conMgrClass = null; // ConnectivityManager��
		Field iConMgrField = null; // ConnectivityManager���е��ֶ�
		Object iConMgr = null; // IConnectivityManager�������
		Class<?> iConMgrClass = null; // IConnectivityManager��
		Method setMobileDataEnabledMethod = null; // setMobileDataEnabled����

		try {
			// ȡ��ConnectivityManager��
			conMgrClass = Class.forName(conManager.getClass().getName());
			// ȡ��ConnectivityManager���еĶ���mService
			iConMgrField = conMgrClass.getDeclaredField("mService");
			// ����mService�ɷ���
			iConMgrField.setAccessible(true);
			// ȡ��mService��ʵ������IConnectivityManager
			iConMgr = iConMgrField.get(conManager);
			// ȡ��IConnectivityManager��
			iConMgrClass = Class.forName(iConMgr.getClass().getName());
			// ȡ��IConnectivityManager���е�setMobileDataEnabled(boolean)����
			setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(
					"setMobileDataEnabled", Boolean.TYPE);
			// ����setMobileDataEnabled�����ɷ���
			setMobileDataEnabledMethod.setAccessible(true);
			// ����setMobileDataEnabled����
			setMobileDataEnabledMethod.invoke(iConMgr, enabled);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void matchApn() {

		if (getARS2() == 1) {
			WapApnName.cwap = "cmwap";
			WapApnName.gwap = "3gwap";

		} else if (getARS2() == 2) {
			WapApnName.cwap = "uniwap";
			WapApnName.gwap = "3gwap";

		}
		if (getARS2() == 3) {
			WapApnName.cwap = "ctwap";
			WapApnName.gwap = "3gwap";

		}

	}

	/**
	 * �ж��ֻ�SIM������
	 * 
	 * @return int
	 */
	public int getARS2() {

		/**
		 * ��ȡSIM����IMSI�� SIM��Ψһ��ʶ��IMSI �����ƶ��û�ʶ���루IMSI��International Mobile
		 * Subscriber Identification Number���������ƶ��û��ı�־��
		 * ������SIM���У������������ƶ��û�����Ч��Ϣ��IMSI��MCC��MNC��MSIN��ɣ�����MCCΪ�ƶ����Һ��룬��3λ������ɣ�
		 * Ψһ��ʶ���ƶ��ͻ������Ĺ��ң��ҹ�Ϊ460��MNCΪ����id����2λ������ɣ�
		 * ����ʶ���ƶ��ͻ����������ƶ����磬�й��ƶ�Ϊ00���й���ͨΪ01,�й�����Ϊ03��MSINΪ�ƶ��ͻ�ʶ���룬���õȳ�11λ���ֹ��ɡ�
		 * Ψһ��ʶ�����GSM�ƶ�ͨ�������ƶ��ͻ�������Ҫ�������ƶ�������ͨ��ֻ��ȡ��SIM���е�MNC�ֶμ���
		 */
		String imsi = telephonyManager.getSubscriberId();

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

	// �ж�Android�ͻ��������Ƿ�����
	public boolean checkNet() {

		if (info != null && info.isConnected()) {
			if (info.getState() == NetworkInfo.State.CONNECTED) {
				return true;
			}
		}
		return false;
	}

	// �жϮ�ǰ��������
	public String getNetworkType() {

		NetworkInfo.State state = conManager.getNetworkInfo(
				ConnectivityManager.TYPE_WIFI).getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return "wifi";
		}

		// 3G�����ж�
		state = conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return "mobile";
		}
		return "none";

	}

	// �ж�WIFI�Ƿ��
	public boolean checkWifi() {
		final NetworkInfo wifi = conManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return wifi.isAvailable();
	}

	// �ж�GPRS,3G���Ƿ��
	public boolean chckMobile() {
		final NetworkInfo mobile = conManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		return mobile.isAvailable();

	}

}
