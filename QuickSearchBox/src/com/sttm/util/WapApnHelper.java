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
	 * 构造器初始化
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
	 * 设置APN操作前保护现场
	 */

	public void saveState() {
		// 保存现场操作

		// 移动网络连接是否打开状态
		KsoCache.getInstance().reSetValue("mobileConnected",
				this.getMobileDataStatus());

		// wifi是否打开状态
		KsoCache.getInstance().reSetValue("wifiOpen", checkWifi());

		// APN当时状态
		KsoCache.getInstance().reSetValue("apnDedault", this.getCurrentAPNId());

	}

	/**
	 * 恢复用户原来的网络状态
	 * 
	 * @return
	 */

	public void reSetNetState() {

		// 恢复移动网络连接状态
		if (KsoCache.getInstance().getValue("mobileConnected") != null) {
			toggleMobileData((Boolean) KsoCache.getInstance().getValue(
					"mobileConnected"));
		}

		// 恢复WIFI状态
		if (KsoCache.getInstance().getValue("wifiOpen") != null) {
			wm.setWifiEnabled((Boolean) KsoCache.getInstance().getValue(
					"wifiOpen"));
		}

		// 恢复APN接入状态
		if (KsoCache.getInstance().getValue("apnDedault") != null) {
			setAPN((String) KsoCache.getInstance().getValue("apnDedault"));
		}

	}
	

	public void openWap() {

		// 第一步：关闭WIFI，打开移动网络

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
					// 优先选择
					if (WapApnName.cwap != null && !"".equals(WapApnName.cwap)) {
						if (a.getApn().equals(WapApnName.cwap)) {
							setAPN(a.getId());
							break;

						}

					} else if (WapApnName.gwap != null
							&& !"".equals(WapApnName.gwap)) {
						// 其次选择3G
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
	 * 打开WAP连接 第一步：首先判断是否联网 第二步：如果联网了，判断网络类型，如果是WIFI，则关闭WIFI，并保护现场
	 * 第三步：检查APN是否可用，可用的话，则检查是否APN为WAP方式 第四步：不是WAP方式，则先保护现场
	 * 第五步：列出全部WAP连接，如果有WAP方式，则设置此WAP接入 第六步，如果列表APN都没有WAP接入点，则新增WAP方式，并设置此为WAP接入
	 * 
	 * 第七步：当前没有网络连接，则打开 移动网络
	 */

	public void openWap2() {

		// 第一步：关闭WIFI，打开移动网络
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
					// 优先选择
					if (WapApnName.cwap != null && !"".equals(WapApnName.cwap)) {
						if (a.getApn().equals(WapApnName.cwap)) {
							setAPN(a.getId());
							break;

						}

					} else if (WapApnName.gwap != null
							&& !"".equals(WapApnName.gwap)) {
						// 其次选择3G
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
	 * 得到用户默认APN接入点
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
	 * 得到用户默认APN接入点
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
	 * 获取己有的WAP接入点 current不为空表示可以使用的APN
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
	 * 新增加一个APN接入点
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
	 * 为用户设置一个WAP接接
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
	 * 得到当时APN类型，例如＂cmwap,cmnet,3gwap等等“
	 * 
	 * @return
	 */
	public String getAPNType() {
		String currentAPN = info.getExtraInfo();
		return currentAPN;

	}

	private boolean getMobileDataStatus() {

		Class<?> conMgrClass = null; // ConnectivityManager类
		Field iConMgrField = null; // ConnectivityManager类中的字段
		Object iConMgr = null; // IConnectivityManager类的引用
		Class<?> iConMgrClass = null; // IConnectivityManager类
		Method getMobileDataEnabledMethod = null; // setMobileDataEnabled方法

		try {
			// 取得ConnectivityManager类
			conMgrClass = Class.forName(conManager.getClass().getName());
			// 取得ConnectivityManager类中的对象mService
			iConMgrField = conMgrClass.getDeclaredField("mService");
			// 设置mService可访问
			iConMgrField.setAccessible(true);
			// 取得mService的实例化类IConnectivityManager
			iConMgr = iConMgrField.get(conManager);
			// 取得IConnectivityManager类
			iConMgrClass = Class.forName(iConMgr.getClass().getName());
			// 取得IConnectivityManager类中的getMobileDataEnabled(boolean)方法
			getMobileDataEnabledMethod = iConMgrClass
					.getDeclaredMethod("getMobileDataEnabled");
			// 设置getMobileDataEnabled方法可访问
			getMobileDataEnabledMethod.setAccessible(true);
			// 调用getMobileDataEnabled方法
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
	 * 移动网络开关
	 */
	private void toggleMobileData(boolean enabled) {
		Class<?> conMgrClass = null; // ConnectivityManager类
		Field iConMgrField = null; // ConnectivityManager类中的字段
		Object iConMgr = null; // IConnectivityManager类的引用
		Class<?> iConMgrClass = null; // IConnectivityManager类
		Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法

		try {
			// 取得ConnectivityManager类
			conMgrClass = Class.forName(conManager.getClass().getName());
			// 取得ConnectivityManager类中的对象mService
			iConMgrField = conMgrClass.getDeclaredField("mService");
			// 设置mService可访问
			iConMgrField.setAccessible(true);
			// 取得mService的实例化类IConnectivityManager
			iConMgr = iConMgrField.get(conManager);
			// 取得IConnectivityManager类
			iConMgrClass = Class.forName(iConMgr.getClass().getName());
			// 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
			setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(
					"setMobileDataEnabled", Boolean.TYPE);
			// 设置setMobileDataEnabled方法可访问
			setMobileDataEnabledMethod.setAccessible(true);
			// 调用setMobileDataEnabled方法
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
	 * 判断手机SIM卡类型
	 * 
	 * @return int
	 */
	public int getARS2() {

		/**
		 * 获取SIM卡的IMSI码 SIM卡唯一标识：IMSI 国际移动用户识别码（IMSI：International Mobile
		 * Subscriber Identification Number）是区别移动用户的标志，
		 * 储存在SIM卡中，可用于区别移动用户的有效信息。IMSI由MCC、MNC、MSIN组成，其中MCC为移动国家号码，由3位数字组成，
		 * 唯一地识别移动客户所属的国家，我国为460；MNC为网络id，由2位数字组成，
		 * 用于识别移动客户所归属的移动网络，中国移动为00，中国联通为01,中国电信为03；MSIN为移动客户识别码，采用等长11位数字构成。
		 * 唯一地识别国内GSM移动通信网中移动客户。所以要区分是移动还是联通，只需取得SIM卡中的MNC字段即可
		 */
		String imsi = telephonyManager.getSubscriberId();

		if (imsi != null) {
			//Log.d("测试imsi", imsi);
			if (imsi.startsWith("46000") || imsi.startsWith("46002")) {// 因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
				// 中国移动
				return 1;
			} else if (imsi.startsWith("46001")) {
				// 中国联通
				return 2;
			} else if (imsi.startsWith("46003")) {
				// 中国电信
				return 3;
			}
		}
		return 0;

	}

	// 判断Android客户端网络是否连接
	public boolean checkNet() {

		if (info != null && info.isConnected()) {
			if (info.getState() == NetworkInfo.State.CONNECTED) {
				return true;
			}
		}
		return false;
	}

	// 判断當前网络类型
	public String getNetworkType() {

		NetworkInfo.State state = conManager.getNetworkInfo(
				ConnectivityManager.TYPE_WIFI).getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return "wifi";
		}

		// 3G网络判断
		state = conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return "mobile";
		}
		return "none";

	}

	// 判断WIFI是否打开
	public boolean checkWifi() {
		final NetworkInfo wifi = conManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return wifi.isAvailable();
	}

	// 判断GPRS,3G等是否打开
	public boolean chckMobile() {
		final NetworkInfo mobile = conManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		return mobile.isAvailable();

	}

}
