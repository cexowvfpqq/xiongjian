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

	// ConnectionManager�ṩ��һ��conManager.startUsingNetworkFeature(type, value)��
	// ��һ������ΪConnectivityManager.TYPE_MOBILE�� �ڶ���Ϊ��mms����ʱ��
	// Ҳ���Խ���������л������漰��setting�����APNֵ�ĸı�
	// ���ڶ�������ΪAPN������е�APN���͡���������Ҫframework���֧��

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

		// ����httpclient��Ĭ�ϲ��ԣ��ǻ��������Σ����������Ժ��ֱ���׳���ConnectException����쳣
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
			// �����ﴦ��һ��
			return "";

		}
		return "";

	}
	
	public byte[] doGet_kso2(String usrStr) {
		 /** 
         * ��Ϊֱ�ӵ���toString���ܻᵼ��ĳЩ�����ַ������������������Դ˴�ʹ��toByteArray 
         * �����Ҫת��String���󣬿����ȵ���EntityUtils.toByteArray()��������Ϣʵ��ת��byte���飬 
         * ����new String(byte[] bArray)ת�����ַ����� 
         */ 

		// ����httpclient��Ĭ�ϲ��ԣ��ǻ��������Σ����������Ժ��ֱ���׳���ConnectException����쳣
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
			// �����ﴦ��һ��
			return null;

		}
		return null;

	}

	public byte[] getBytes(String usrStr, String localPath) {

		// ����httpclient��Ĭ�ϲ��ԣ��ǻ��������Σ����������Ժ��ֱ���׳���ConnectException����쳣

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
			// �����ﴦ��һ��
			return null;

		}
		return null;

	}

	public byte[] getBytes2(String usrStr, String fileName) {

		// ����httpclient��Ĭ�ϲ��ԣ��ǻ��������Σ����������Ժ��ֱ���׳���ConnectException����쳣

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
			// �����ﴦ��һ��
			return null;

		}
		return null;

	}

	// URL������ʷ�ʽ����׼
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

	// URL������ʷ�ʽ����׼2
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
			// �����ﴦ��һ��
			return null;

		}
		return null;

	}

	public String getContentBySocketGet(String url, String host) {

		// url��ʽlog.sina.com.cn/rss/blogmc.xml;
		// host��ʽlog.sina.com.cn
		// cmwapʵ��Socket��new Socket("10.0.0.172", 80)��
		// ������ͷ��Ӧ���ݸĳ�"GET blog.sina.com.cn/rss/blogmc.xml HTTP/1.1\r\n"
		Socket socket;
		try {
			// ʵ����Socket
			socket = new Socket("10.0.0.172", 80);
			// �����
			OutputStream os = socket.getOutputStream();
			// ������
			InputStream ins = socket.getInputStream();
			StringBuffer sb = new StringBuffer();
			String method = "GET";
			// ��1�У���������������ݣ�HTTPЭ��İ汾
			// ����һ�������GET����������������ǡ�/rss/blogmc.xml����HTTPЭ��İ汾��ָ� //
			// ����֧�ֵİ汾���������������˵����ν��������1.1��
			// ��HTTP/1.1����
			sb.append(method + " " + url + " HTTP/1.1\r\n");
			// ����������ʽΪ��Host:������
			sb.append("Host:" + host + "\r\n");
			// ���յ���������
			sb.append("Accept: :*/* \r\n");
			// ���յ��������ԣ����Բ�����
			sb.append("Accept-Language: zh-cn\r\n");
			// �������� �趨Ϊһֱ��������
			sb.append("Connection: Keep-Alive\r\n");
			// ע�����һ��Ҫ��\r\n�س�����
			sb.append("\r\n");
			// ����Web������������HTTP��Ӧ��
			os.write(sb.toString().getBytes());
			os.flush();
			InputStreamReader ireader = new InputStreamReader(ins);
			BufferedReader reader = new BufferedReader(ireader);
			String str = "";
			sb = new StringBuffer();
			while ((str = reader.readLine()) != null) {
				sb.append(str + "\n");
			}
			System.out.println(sb.toString());// ��ȡ����
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

		// url��ʽlog.sina.com.cn/rss/blogmc.xml;
		// host��ʽlog.sina.com.cn
		// cmwapʵ��Socket��new Socket("10.0.0.172", 80)��
		// ������ͷ��Ӧ���ݸĳ�"GET blog.sina.com.cn/rss/blogmc.xml HTTP/1.1\r\n"

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
			// ʵ����Socket
			socket = new Socket("10.0.0.172", 80);
			// �����
			OutputStream os = socket.getOutputStream();
			// ������
			InputStream ins = socket.getInputStream();
			StringBuffer sb = new StringBuffer();
			String method = "POST";
			// ��1�У���������������ݣ�HTTPЭ��İ汾
			// ����һ�������GET����������������ǡ�/rss/blogmc.xml����HTTPЭ��İ汾��ָ� //
			// ����֧�ֵİ汾���������������˵����ν��������1.1��
			// ��HTTP/1.1����
			sb.append(method + " " + url + " HTTP/1.1\r\n");
			// ����������ʽΪ��Host:������
			sb.append("Host:" + host + "\r\n");
			// ���յ���������
			sb.append("Accept: :*/* \r\n");
			// ���յ��������ԣ����Բ�����
			sb.append("Accept-Language: zh-cn\r\n");
			// �������� �趨Ϊһֱ��������
			sb.append("Connection: Keep-Alive\r\n");
			// ע�⣬���һ��Ҫ��\r\n�س�����
			sb.append("\r\n");
			// sb.append("data=abc\r\n"); //Post�ύ�Ĳ���

			sb.append("postParam" + "\r\n");
			// ע�����һ��Ҫ��\r\n�س�����
			sb.append("\r\n");
			// ����Web������������HTTP��Ӧ��
			os.write(sb.toString().getBytes());
			os.flush();
			InputStreamReader ireader = new InputStreamReader(ins);
			BufferedReader reader = new BufferedReader(ireader);
			String str = "";
			sb = new StringBuffer();
			while ((str = reader.readLine()) != null) {
				sb.append(str + "\n");
			}
			System.out.println(sb.toString());// ��ȡ����
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
		//��Content-type��,��application/xxx��
		request.setHeader("Content-Type", contentType);

		// ������ݲ��������Ƚ϶�Ļ����ԶԴ��ݵĲ������з�װ
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (String key : rawParams.keySet()) {
			// ��װ�������
			params.add(new BasicNameValuePair(key, rawParams.get(key)));
		}
		// �����������
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
		 * Android ʹ��cmwap GPRS ��ʽ����
		 * CMWAP��CMNETֻ���й��ƶ�Ϊ�仮�ֵ�����GPRS���뷽ʽ���й��ƶ���CMWAP����һ�������ƣ���Ҫ������CMWAP����ʱֻ�ܷ���
		 * GPRS�����ڵ�IP��10.*.*.*�������޷�ͨ��·�ɷ���Internet��������CMWAP���Internet�ϵ���ҳ
		 * ����ͨ��WAP����Э������ṩ��HTTP�������ʵ�ֵġ� ��ˣ�ֻ��������������������Ӧ�� �������й��ƶ���CMWAP���뷽ʽ������������
		 * 1.Ӧ�ó��� �������������HTTPЭ�顣 2.Ӧ�ó��� ֧��HTTP����Э���WAP����Э�顣
		 * ��Ҳ����Ϊʲô���ǵ�G1�޷�������CMWAP��ԭ��
		 * һ�仰��CMWAP���ƶ����Ƶģ�������ֻ����WAP������CMNET������GPRS���WWW ����һ:
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
			//Log.d("ͼƬ�����ֽڳ���", is.available() + "");
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
			//Log.d("����","����ͼƬ");

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
				
				//Log.d("ͼƬ�����ֽڳ���", bytes.length + "");
				context.deleteFile("read.img");
				FileOutputStream outStream = context.openFileOutput(
						"read.img", Context.MODE_WORLD_READABLE);
				outStream.write(bytes);
				outStream.flush();
				outStream.close();
				
				Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				return bitmap;

			}else {
				//Log.d("û�����ӳɹ�","test=========");
			}

		
		} catch (IOException e) {
			//Log.d("û��", "IO�쳣");

		}
		return null;
		
		
	}
}
