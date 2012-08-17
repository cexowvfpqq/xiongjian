package com.sttm.util;

import java.util.List;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

/**
 * 
 * @author ligm
 * @version 1.0
 * 
 * date 2011-12-6 10:14
 * 
 * 判断网络是否连接或WIFI已连接
 * 
 */

public class IsNetOpen {
	private Context context;
	private final ConnectivityManager conManager;

	public IsNetOpen(Context context) {
		this.context = context;
		conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
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

	// 判断Android客户端网络是否连接
	public boolean checkNet() {
		final NetworkInfo info = conManager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			if (info.getState() == NetworkInfo.State.CONNECTED) {
				return true;
			}
		}
		return false;
	}
	
	
	//遍歷法判断Android客户端网络是否连接
	public boolean isNetworkAvailable() {   
		   
		    ConnectivityManager connectivity = (ConnectivityManager) context   
		            .getSystemService(Context.CONNECTIVITY_SERVICE);   
		    if (connectivity == null) {   
		        return false;  
		    } else {//获取所有网络连接信息   
		       NetworkInfo[] info = connectivity.getAllNetworkInfo();   
		       if (info != null) {//逐一查找状态为已连接的网络   
		            for (int i = 0; i < info.length; i++) {   
		                if (info[i].getState() == NetworkInfo.State.CONNECTED) {   
	                   return true;   
		               }   
		            }   
		        }   
		    }   
		    return false;   
		}  


	// 判断是否在线模式
	public boolean isOnLine() {
		return false;
	}
	
	/**   
     * 网络是否可用   
     *    
     * @param context   
     * @return   
     */    
    public static boolean isNetworkAvailable(Context context) {    
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);    
        NetworkInfo[] info = mgr.getAllNetworkInfo();    
        if (info != null) {    
            for (int i = 0; i < info.length; i++) {    
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {    
                    return true;    
                }    
            }    
        }    
        return false;    
    }    

	// 判断是否飞行模式
	public boolean isFly() {
		if (Settings.System.getInt(context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) != 0) {
			return true;
		}
		return false;
	}

	// 获取Mac地址

	public String getMac() {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();

	}
	
	//GPS导航是否可用
	public boolean isGpsEnabled()   
	{   
	    LocationManager locationManager =((LocationManager)context.getSystemService(Context.LOCATION_SERVICE));   
	    List<String> accessibleProviders = locationManager.getProviders(true);   
	    return accessibleProviders != null && accessibleProviders.size() > 0;   
	}
	
	
	//判断當前网络类型
	public String getNetworkType()  {   
		    ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);   
		   NetworkInfo.State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();   
		   if(state == State.CONNECTED || state == State.CONNECTING){   
		        return "wifi";   
		    }   
		
		   //3G网络判断   
		   state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();   
		  if(state == State.CONNECTED || state == State.CONNECTING){   
		        return "mobile";   
		    }   
		    return "none";   
		
	}  


}
