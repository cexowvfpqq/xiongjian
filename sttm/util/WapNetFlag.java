package com.sttm.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;

public class WapNetFlag {
	private final String TAG = "WapNetFlag";
	 public boolean isCMWAP(Context context) {

	        String currentAPN = "";
	        ConnectivityManager conManager = (ConnectivityManager) context
	              .getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo info = conManager
	            .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	        currentAPN = info.getExtraInfo();

	        if (currentAPN == null || "".equals(currentAPN)) {
	            return false;
	        }else{
	            if (currentAPN.equals("cmwap")){
	                return true;
	            } else {
	                return false;
	            }

	        }
	 }
	 
	 
	 
	 /**
	     * 获取网络连接对象
	     */
	    @SuppressWarnings("unused")
		private HttpURLConnection getConnection(String url,Context context,int timeout) throws IOException
	    {
	        HttpURLConnection httpUrlConnection = null;
	        if (isCMWAP(context))
	        {

	            //Log.d("ray", "isCMWAP");

	            //int contentBeginIdx = task.url.indexOf('/', 7);
	            StringBuffer urlStringBuffer = new StringBuffer(
	                "http://10.0.0.172:80");

	            //urlStringBuffer.append(task.url.substring(contentBeginIdx));
	            URL urltemp = new URL(urlStringBuffer.toString());
	            httpUrlConnection = (HttpURLConnection) urltemp.openConnection();
	            httpUrlConnection.setRequestProperty("X-Online-Host",url);
	        }
	        else
	        {

	            URL connUrl = new URL(url);
	            //Log.d(TAG, "getConnection() url=" + url);
	            httpUrlConnection = (HttpURLConnection) connUrl.openConnection();

	        }

	        httpUrlConnection.setRequestProperty("Accept", "*/*");
	        httpUrlConnection.setRequestProperty("Pragma", "No-cache");
	        httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
	        httpUrlConnection.setRequestProperty("connection", "keep-alive");
	        httpUrlConnection.setRequestProperty("accept-charset", "utf-8");

	        //根据网络类型设置超时时间
	        int net_type = getNetworkType(context);

	        if (net_type == ConnectivityManager.TYPE_MOBILE)
	        {
	            httpUrlConnection.setConnectTimeout(timeout / 2);
	        }
	        else if (net_type == ConnectivityManager.TYPE_WIFI)
	        {
	            httpUrlConnection.setConnectTimeout(timeout);
	        }
	        return httpUrlConnection;
	    }
	    
	    
	  //判断當前网络类型
		public int getNetworkType(Context context)  {   
			    ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);   
			   NetworkInfo.State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();   
			   if(state == State.CONNECTED || state == State.CONNECTING){   
			        return 1;   
			    }   
			
			   //3G网络判断   
			   state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();   
			  if(state == State.CONNECTED || state == State.CONNECTING){   
			        return 2;   
			    }   
			    return 0;   
			
		}  



}
