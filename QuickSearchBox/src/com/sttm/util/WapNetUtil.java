package com.sttm.util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class WapNetUtil {

	// ConnectionManager提供了一个conManager.startUsingNetworkFeature(type, value)，
	// 第一个设置为ConnectivityManager.TYPE_MOBILE， 第二个为”mms”的时候，
	// 也可以进行网络的切换，不涉及到setting里面的APN值的改变
	// 。第二个参数为APN接入点中的APN类型。但是这需要framework层的支持

	public DefaultHttpClient getClient() {
		HttpParams baseParams = new BasicHttpParams();
		baseParams.setParameter("http.route.default-proxy", new HttpHost(
				"10.0.0.172", 80));
		HttpConnectionParams.setConnectionTimeout(baseParams, 30 * 1000);
		HttpConnectionParams.setSoTimeout(baseParams, 45 * 1000);
		// establish HttpClient
		DefaultHttpClient client = new DefaultHttpClient(baseParams);

		return client;
	}

	public String doGet_kso(String usrStr) {

		// 根据httpclient的默认策略，是会重试三次，但是三次以后就直接抛出了ConnectException这个异常
		try {
			HttpGet request = new HttpGet(usrStr);
			HttpResponse response = getClient().execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				String strResult = EntityUtils.toString(response.getEntity());

				return strResult;
			}
		} catch (ClientProtocolException e) {
			return "";

		} catch (IOException e) {
			// 在这里处理一下
			return "";

		}
		return "";

	}
	
	public byte[] doGet_kso2(String usrStr) {
		 /** 
         * 因为直接调用toString可能会导致某些中文字符出现乱码的情况。所以此处使用toByteArray 
         * 如果需要转成String对象，可以先调用EntityUtils.toByteArray()方法将消息实体转成byte数组， 
         * 在由new String(byte[] bArray)转换成字符串。 
         */ 

		// 根据httpclient的默认策略，是会重试三次，但是三次以后就直接抛出了ConnectException这个异常
		try {
			HttpGet request = new HttpGet(usrStr);
			HttpResponse response = getClient().execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				byte[] buffer = EntityUtils.toByteArray(response.getEntity());

				return buffer;
			}
		} catch (ClientProtocolException e) {
			return null;

		} catch (IOException e) {
			// 在这里处理一下
			return null;

		}
		return null;

	}

	public byte[] getBytes(String usrStr, String localPath) {

		// 根据httpclient的默认策略，是会重试三次，但是三次以后就直接抛出了ConnectException这个异常

		try {
			HttpGet request = new HttpGet(usrStr);
			HttpResponse response = getClient().execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String strResult = EntityUtils.toString(response.getEntity());
				byte[] bytes = strResult.getBytes();
				FileOutputStream os = new FileOutputStream(
						"data/data/com.sttm.charge/files/" + localPath);
				ByteUtil.writeByteFile(os, bytes);
				return bytes;
			}
		} catch (ClientProtocolException e) {
			return null;

		} catch (IOException e) {
			// 在这里处理一下
			return null;

		}
		return null;

	}

	public byte[] getBytes2(String usrStr, String fileName) {

		// 根据httpclient的默认策略，是会重试三次，但是三次以后就直接抛出了ConnectException这个异常

		try {
			HttpGet request = new HttpGet(usrStr);
			HttpResponse response = getClient().execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// String strResult =
				// EntityUtils.toString(response.getEntity());
				// byte[] bytes = strResult.getBytes();
				InputStream inputStream = response.getEntity().getContent();
				int length = (int) response.getEntity().getContentLength();
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

				return buffer;
			}
		} catch (ClientProtocolException e) {
			return null;

		} catch (IOException e) {
			// 在这里处理一下
			return null;

		}
		return null;

	}

	// URL代理访问方式，标准
	public static byte[] getBytesByUrl(String urlStr, String fileName)
			throws IOException {
		URL url = new URL(urlStr);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
				"10.0.0.172", 80));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
		conn.setDoInput(true);
		conn.connect();
		InputStream inputStream = conn.getInputStream();

		int length = conn.getContentLength();
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

	}
	
	
	public static byte[] getBytesByUrl(String urlStr, String fileName,int downLen)
			throws IOException {
		URL url = new URL(urlStr);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
				"10.0.0.172", 80));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		
		conn.connect();
		InputStream inputStream = conn.getInputStream();

		int length = conn.getContentLength();
		if(length > downLen){
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

	}

	// URL代理访问方式，标准2
	public byte[] getBytesByUrl2(String urlStr, String host, String fileName)
			throws IOException {
		URL url = new URL("http://10.0.0.172:80/" + urlStr);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("X-Online-Host", host);
		conn.setDoInput(true);
		conn.connect();
		InputStream inputStream = conn.getInputStream();

		int length = conn.getContentLength();
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

	}

	public InputStream getInputStream(String usrStr) {
		InputStream in;
		try {
			HttpGet request = new HttpGet(usrStr);
			HttpResponse response = getClient().execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				in = response.getEntity().getContent();
				return in;
			}
		} catch (ClientProtocolException e) {
			return null;

		} catch (IOException e) {
			// 在这里处理一下
			return null;

		}
		return null;

	}

	public String getContentBySocketGet(String url, String host) {

		// url格式log.sina.com.cn/rss/blogmc.xml;
		// host格式log.sina.com.cn
		// cmwap实例Socket用new Socket("10.0.0.172", 80)，
		// 并将报头对应内容改成"GET blog.sina.com.cn/rss/blogmc.xml HTTP/1.1\r\n"
		Socket socket;
		try {
			// 实例化Socket
			socket = new Socket("10.0.0.172", 80);
			// 输出流
			OutputStream os = socket.getOutputStream();
			// 输入流
			InputStream ins = socket.getInputStream();
			StringBuffer sb = new StringBuffer();
			String method = "GET";
			// 第1行：方法，请求的内容，HTTP协议的版本
			// 下载一般可以用GET方法，请求的内容是“/rss/blogmc.xml”，HTTP协议的版本是指浏 //
			// 览器支持的版本，对于下载软件来说无所谓，所以用1.1版
			// “HTTP/1.1”；
			sb.append(method + " " + url + " HTTP/1.1\r\n");
			// 主机名、格式为“Host:主机”
			sb.append("Host:" + host + "\r\n");
			// 接收的数据类型
			sb.append("Accept: :*/* \r\n");
			// 接收的数据语言，可以不设置
			sb.append("Accept-Language: zh-cn\r\n");
			// 连接设置 设定为一直保持连接
			sb.append("Connection: Keep-Alive\r\n");
			// 注意最后一定要有\r\n回车换行
			sb.append("\r\n");
			// 接收Web服务器，返回HTTP响应包
			os.write(sb.toString().getBytes());
			os.flush();
			InputStreamReader ireader = new InputStreamReader(ins);
			BufferedReader reader = new BufferedReader(ireader);
			String str = "";
			sb = new StringBuffer();
			while ((str = reader.readLine()) != null) {
				sb.append(str + "\n");
			}
			System.out.println(sb.toString());// 读取内容
			reader.close();
			ireader.close();
			os.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	public String getContentBySocketPost(String url, String host,
			HashMap<String, String> params) {

		// url格式log.sina.com.cn/rss/blogmc.xml;
		// host格式log.sina.com.cn
		// cmwap实例Socket用new Socket("10.0.0.172", 80)，
		// 并将报头对应内容改成"GET blog.sina.com.cn/rss/blogmc.xml HTTP/1.1\r\n"

		StringBuffer param = new StringBuffer();
		Set keys = params.keySet();
		Iterator iter = keys.iterator();
		while (iter.hasNext()) {
			param.append((String) iter.next() + "="
					+ params.get((String) iter.next()) + "&");
		}

		String postParam = param.toString().substring(0,
				param.toString().lastIndexOf("&"));

		Socket socket;
		try {
			// 实例化Socket
			socket = new Socket("10.0.0.172", 80);
			// 输出流
			OutputStream os = socket.getOutputStream();
			// 输入流
			InputStream ins = socket.getInputStream();
			StringBuffer sb = new StringBuffer();
			String method = "POST";
			// 第1行：方法，请求的内容，HTTP协议的版本
			// 下载一般可以用GET方法，请求的内容是“/rss/blogmc.xml”，HTTP协议的版本是指浏 //
			// 览器支持的版本，对于下载软件来说无所谓，所以用1.1版
			// “HTTP/1.1”；
			sb.append(method + " " + url + " HTTP/1.1\r\n");
			// 主机名、格式为“Host:主机”
			sb.append("Host:" + host + "\r\n");
			// 接收的数据类型
			sb.append("Accept: :*/* \r\n");
			// 接收的数据语言，可以不设置
			sb.append("Accept-Language: zh-cn\r\n");
			// 连接设置 设定为一直保持连接
			sb.append("Connection: Keep-Alive\r\n");
			// 注意，最后一定要有\r\n回车换行
			sb.append("\r\n");
			// sb.append("data=abc\r\n"); //Post提交的参数

			sb.append("postParam" + "\r\n");
			// 注意最后一定要有\r\n回车换行
			sb.append("\r\n");
			// 接收Web服务器，返回HTTP响应包
			os.write(sb.toString().getBytes());
			os.flush();
			InputStreamReader ireader = new InputStreamReader(ins);
			BufferedReader reader = new BufferedReader(ireader);
			String str = "";
			sb = new StringBuffer();
			while ((str = reader.readLine()) != null) {
				sb.append(str + "\n");
			}
			System.out.println(sb.toString());// 读取内容
			reader.close();
			ireader.close();
			os.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unused")
	public void wapConectionByDoPost(String host, String purl,
			String contentType, Map<String, String> rawParams)
			throws ClientProtocolException, IOException {
		// host = symmt18.tiros.com.cn
		// purl = "http://10.0.0.172:80//MTServer3.5/mts?snouse=0";
		HttpHost proxy = new HttpHost("10.0.0.172", 80, "http");
		HttpHost target = new HttpHost(host, 80, "http");

		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);
		HttpPost request = new HttpPost(purl);
		//“Content-type”,”application/xxx”
		request.setHeader("Content-Type", contentType);

		// 如果传递参数个数比较多的话可以对传递的参数进行封装
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (String key : rawParams.keySet()) {
			// 封装请求参数
			params.add(new BasicNameValuePair(key, rawParams.get(key)));
		}
		// 设置请求参数
		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = httpclient.execute(target, request);

	}

	public void wapConectionByDoGet(String host,String urlStr)
			throws ClientProtocolException, IOException {

		HttpHost proxy = new HttpHost("10.0.0.172", 80, "http");
		HttpHost target = new HttpHost(host, 80, "http");

		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);

		//HttpGet req = new HttpGet("/");
		HttpGet req = new HttpGet(urlStr);

		System.out.println("executing request to " + target + " via " + proxy);
		HttpResponse rsp = httpclient.execute(target, req);
		HttpEntity entity = rsp.getEntity();

		System.out.println("----------------------------------------");
		System.out.println(rsp.getStatusLine());
		Header[] headers = rsp.getAllHeaders();
		for (int i = 0; i < headers.length; i++) {
			System.out.println(headers);
		}
		System.out.println("----------------------------------------");

		if (entity != null) {
			System.out.println(EntityUtils.toString(entity));
		}

		httpclient.getConnectionManager().shutdown();

	}

	public InputStream getInputStream(String host, String urlStr)
			throws IOException {
		/**
		 * Android 使用cmwap GPRS 方式联网
		 * CMWAP和CMNET只是中国移动为其划分的两个GPRS接入方式。中国移动对CMWAP作了一定的限制，主要表现在CMWAP接入时只能访问
		 * GPRS网络内的IP（10.*.*.*），而无法通过路由访问Internet，我们用CMWAP浏览Internet上的网页
		 * 就是通过WAP网关协议或它提供的HTTP代理服务实现的。 因此，只有满足以下两个条件的应用 才能在中国移动的CMWAP接入方式下正常工作：
		 * 1.应用程序 的网络请求基于HTTP协议。 2.应用程序 支持HTTP代理协议或WAP网关协议。
		 * 这也就是为什么我们的G1无法正常用CMWAP的原因。
		 * 一句话：CMWAP是移动限制的，理论上只能上WAP网，而CMNET可以用GPRS浏览WWW 方法一:
		 */
		URL url = new URL("http://10.0.0.172:80/" + urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("X-Online-Host", "host");
		conn.setDoInput(true);
		conn.connect();
		InputStream is = conn.getInputStream();
		return is;

		// is.close();
		// conn.disconnect();
	}
	
	public static Bitmap getImage(String urlStr){
		URL url;
		try {
			url = new URL(urlStr);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));   
			HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);   
			conn.setDoInput(true);   
			conn.connect();   
			InputStream is = conn.getInputStream();   
			//Log.d("图片下载字节长度", is.available() + "");
			Bitmap bitmap = BitmapFactory.decodeStream(is);   
			is.close();   
			conn.disconnect();  
			return bitmap;
		} catch (MalformedURLException e) {
			return null;
			
		} catch (IOException e) {
			
			e.printStackTrace();
			return null;
		}   
		
		
	}
	
	
	public static Bitmap getImage2(String urlStr,Context context){
	
		try {
			//Log.d("测试","下载图片");

			HttpGet request = new HttpGet(urlStr);
			HttpParams httpParameters = new BasicHttpParams();

			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);

			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpHost proxy = new HttpHost("10.0.0.172", 80);
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);

			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				byte[] bytes = EntityUtils.toByteArray(response.getEntity());
				
				//Log.d("图片下载字节长度", bytes.length + "");
				context.deleteFile("read.img");
				FileOutputStream outStream = context.openFileOutput(
						"read.img", Context.MODE_WORLD_READABLE);
				outStream.write(bytes);
				outStream.flush();
				outStream.close();
				
				Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				return bitmap;

			}else {
				//Log.d("没有连接成功","test=========");
			}

		
		} catch (IOException e) {
			//Log.d("没有", "IO异常");

		}
		return null;
		
		
	}
}
