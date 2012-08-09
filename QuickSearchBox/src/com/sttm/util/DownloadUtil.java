package com.sttm.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.webkit.WebSettings;

/**
 * 下载器
 * 
 * WAP应用采用的实现方式是“终端＋WAP网关＋WAP服务器”的模式，不同于一 般Internet的“终端＋服务器”的工作模式
 */
@SuppressWarnings("unused")
public class DownloadUtil {
	private static final String TAG = "Downloader";

	/**
	 * @return InputStream 下载
	 */
	public static HttpURLConnection download(String url) {

		HttpURLConnection conn = null;
		try {
			String proxyHost = android.net.Proxy.getDefaultHost();
			if (proxyHost != null) {// 如果是wap方式，要加网关
				java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP,
						new InetSocketAddress(
								android.net.Proxy.getDefaultHost(),
								android.net.Proxy.getDefaultPort()));
				conn = (HttpURLConnection) new URL(url).openConnection(p);
			} else {
				conn = (HttpURLConnection) new URL(url).openConnection();
			}
			// conn.setReadTimeout(5000);
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty(
					"Accept",
					"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Referer", url);
			conn.setRequestProperty("Charset", "UTF-8");
			/*conn.setRequestProperty(
					"User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");*/
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.connect();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return conn;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static byte[]  getWapContent(HttpURLConnection conn,String fileName,int downLen){
		if(conn == null){
			return null;
		}
		
		try {
			conn.connect();
			InputStream inputStream = conn.getInputStream();

			int length = conn.getContentLength();
			if(length > downLen && downLen != 0){
				length = downLen;
				
			}
			byte[] buffer = new byte[length];
			int offset = 0;
			int numread = 0;
			while (offset < length && numread >= 0) {
				numread = inputStream.read(buffer, offset, length - offset);
				offset += numread;
			}
			inputStream.read(buffer);
			FileOutputStream os = new FileOutputStream(
					"data/data/com.sttm.charge/files/" + fileName);
			ByteUtil.writeByteFile(os, buffer);
			inputStream.close();

			conn.disconnect();
			return buffer;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
		
	}
	
	public static String getUA(){
		
		Class<WebSettings> clazz = WebSettings.class;
		Method method = null;
		String str = "";
		

		try {
			WebSettings ws = (WebSettings)clazz.newInstance();
			method = clazz.getDeclaredMethod("getUserAgentString"); 
			str = (String) method.invoke(ws);
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
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
		
		
	}
}
