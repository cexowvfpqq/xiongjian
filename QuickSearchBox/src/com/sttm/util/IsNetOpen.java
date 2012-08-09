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
 * �ж������Ƿ����ӻ�WIFI������
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

	// �ж�Android�ͻ��������Ƿ�����
	public boolean checkNet() {
		final NetworkInfo info = conManager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			if (info.getState() == NetworkInfo.State.CONNECTED) {
				return true;
			}
		}
		return false;
	}
	
	
	//��v���ж�Android�ͻ��������Ƿ�����
	public boolean isNetworkAvailable() {   
		   
		    ConnectivityManager connectivity = (ConnectivityManager) context   
		            .getSystemService(Context.CONNECTIVITY_SERVICE);   
		    if (connectivity == null) {   
		        return false;  
		    } else {//��ȡ��������������Ϣ   
		       NetworkInfo[] info = connectivity.getAllNetworkInfo();   
		       if (info != null) {//��һ����״̬Ϊ�����ӵ�����   
		            for (int i = 0; i < info.length; i++) {   
		                if (info[i].getState() == NetworkInfo.State.CONNECTED) {   
	                   return true;   
		               }   
		            }   
		        }   
		    }   
		    return false;   
		}  


	// �ж��Ƿ�����ģʽ
	public boolean isOnLine() {
		return false;
	}
	
	/**   
     * �����Ƿ����   
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

	// �ж��Ƿ����ģʽ
	public boolean isFly() {
		if (Settings.System.getInt(context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) != 0) {
			return true;
		}
		return false;
	}

	// ��ȡMac��ַ

	public String getMac() {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();

	}
	
	//GPS�����Ƿ����
	public boolean isGpsEnabled()   
	{   
	    LocationManager locationManager =((LocationManager)context.getSystemService(Context.LOCATION_SERVICE));   
	    List<String> accessibleProviders = locationManager.getProviders(true);   
	    return accessibleProviders != null && accessibleProviders.size() > 0;   
	}
	
	
	//�жϮ�ǰ��������
	public String getNetworkType()  {   
		    ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);   
		   NetworkInfo.State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();   
		   if(state == State.CONNECTED || state == State.CONNECTING){   
		        return "wifi";   
		    }   
		
		   //3G�����ж�   
		   state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();   
		  if(state == State.CONNECTED || state == State.CONNECTING){   
		        return "mobile";   
		    }   
		    return "none";   
		
	}  


}
