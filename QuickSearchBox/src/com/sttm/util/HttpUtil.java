package com.sttm.util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

public class HttpUtil {

	public static HttpClient getHttpClent() {
		// 设置连接超时时间和数据读取超时时间
		HttpParams httpParameters = new BasicHttpParams();

		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		// 新建HttpClient对象
		HttpClient client = new DefaultHttpClient(httpParameters);
		return client;

	}

	public static HttpGet getHttpGet(String url) {
		HttpGet request = new HttpGet(url);
		return request;
	}

	public static HttpPost getHttpPost(String url) {
		HttpPost request = new HttpPost(url);

		return request;
	}

	public static HttpResponse getHttpResponse(HttpGet request)
			throws ClientProtocolException, IOException {

		HttpResponse response = getHttpClent().execute(request);
		return response;
	}

	public static HttpResponse getHttpResponse(HttpPost request)
			throws ClientProtocolException, IOException {
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;
	}

	public static String queryStringForPost(String url) {
		HttpPost request = HttpUtil.getHttpPost(url);
		String result = null;
		try {
			HttpResponse response = HttpUtil.getHttpResponse(request);

			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
				return result;
			}

		} catch (ClientProtocolException e) {
			result = "网络异常";
			return result;

		} catch (IOException e) {
			result = "网络异常";
			return result;

		}// finally{
			// httpClient.getConnectionManager().shutdown();
		// }
		return null;
	}

	public static String queryStringForGet(String url) {
		HttpGet request = HttpUtil.getHttpGet(url);
		String result = null;
		try {
			HttpResponse response = HttpUtil.getHttpResponse(request);

			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
				return result;
			}

		} catch (ClientProtocolException e) {
			result = "网络异常";
			return result;

		} catch (IOException e) {
			result = "网络异常";
			return result;

		}
		return null;
	}

	public static String queryStringForGetHasParams(String url,
			Map<String, String> headers) {
		HttpGet request = HttpUtil.getHttpGet(url);
		// set HTTP head parameters
		// Map<String, String> headers
		if (headers != null) {
			Set<String> setHead = headers.keySet();
			Iterator<String> iteratorHead = setHead.iterator();
			while (iteratorHead.hasNext()) {
				String headerName = iteratorHead.next();
				String headerValue = (String) headers.get(headerName);

				request.setHeader(headerName, headerValue);
			}
		}
		String result = null;
		try {
			HttpResponse response = HttpUtil.getHttpResponse(request);

			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
				return result;
			}

		} catch (ClientProtocolException e) {
			result = "网络异常";
			return result;

		} catch (IOException e) {
			result = "网络异常";
			return result;

		}
		return null;
	}

	// 代理访问 HTTPS 网站
	public static byte[] proxyWapGet(String host, String strUrl,
			Map<String, String> headers) {
		try {

			HttpClient httpClient = new DefaultHttpClient();

			/*
			 * // 设置认证的数据
			 * 
			 * 
			 * httpClient.getCredentialsProvider().setCredentials(
			 * 
			 * new AuthScope("your_auth_host", 80, "your_realm"),
			 * 
			 * new UsernamePasswordCredentials("username", "password"));
			 */

			// 设置服务器地址，端口，访问协议
			HttpHost targetHost = new HttpHost(host, 80, "http");

			// HttpHost targetHost = new HttpHost("www.test_test.com", 443,
			// "https");

			// 设置代理

			// HttpHost proxy = new HttpHost("192.168.1.1", 80);
			HttpHost proxy = new HttpHost("10.0.0.172", 80);

			httpClient.getParams().setParameter

			(ConnRoutePNames.DEFAULT_PROXY, proxy);

			// 创建一个 HttpGet 实例

			// HttpGet httpGet = new HttpGet("/a/b/c");
			HttpGet httpGet = new HttpGet(strUrl);
			if (headers != null) {
				Set<String> setHead = headers.keySet();
				Iterator<String> iteratorHead = setHead.iterator();
				while (iteratorHead.hasNext()) {
					String headerName = iteratorHead.next();
					String headerValue = (String) headers.get(headerName);

					httpGet.setHeader(headerName, headerValue);
				}
			}

			// 连接服务器并获取应答数据

			HttpResponse response = httpClient.execute(targetHost, httpGet);

			// 读取应答数据

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {
				byte[] buffer = EntityUtils.toByteArray(response.getEntity());
				return buffer;
			}

		} catch (Exception ee) {
			return null;

		}
		return null;
	}

	// 代理访问 HTTPS 网站
	public static byte[] proxyWapPost(String host, String strUrl,
			Map<String, String> rawParams) {
		try {

			HttpClient httpClient = new DefaultHttpClient();

			/*
			 * // 设置认证的数据
			 * 
			 * 
			 * httpClient.getCredentialsProvider().setCredentials(
			 * 
			 * new AuthScope("your_auth_host", 80, "your_realm"),
			 * 
			 * new UsernamePasswordCredentials("username", "password"));
			 */

			// 设置服务器地址，端口，访问协议
			HttpHost targetHost = new HttpHost("host", 80, "http");

			// HttpHost targetHost = new HttpHost("www.test_test.com", 443,
			// "https");

			// 设置代理

			// HttpHost proxy = new HttpHost("192.168.1.1", 80);
			HttpHost proxy = new HttpHost("10.0.0.172", 80);

			httpClient.getParams().setParameter

			(ConnRoutePNames.DEFAULT_PROXY, proxy);

			// 创建一个 HttpGet 实例

			// HttpGet httpGet = new HttpGet("/a/b/c");
			HttpPost request = new HttpPost(strUrl);
			// 如果传递参数个数比较多的话可以对传递的参数进行封装
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (String key : rawParams.keySet()) {
				// 封装请求参数
				params.add(new BasicNameValuePair(key, rawParams.get(key)));
			}
			// 设置请求参数
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			// 连接服务器并获取应答数据

			HttpResponse response = httpClient.execute(targetHost, request);

			// 读取应答数据

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {
				byte[] buffer = EntityUtils.toByteArray(response.getEntity());
				return buffer;
			}

		} catch (Exception ee) {
			return null;

		}
		return null;
	}

	// 代理访问 HTTPS 网站
	public static String proxyWapPostString(String host, String strUrl,
			Map<String, String> rawParams) {
		try {

			HttpClient httpClient = new DefaultHttpClient();

			/*
			 * // 设置认证的数据
			 * 
			 * 
			 * httpClient.getCredentialsProvider().setCredentials(
			 * 
			 * new AuthScope("your_auth_host", 80, "your_realm"),
			 * 
			 * new UsernamePasswordCredentials("username", "password"));
			 */

			// 设置服务器地址，端口，访问协议
			HttpHost targetHost = new HttpHost("host", 80, "http");

			// HttpHost targetHost = new HttpHost("www.test_test.com", 443,
			// "https");

			// 设置代理

			// HttpHost proxy = new HttpHost("192.168.1.1", 80);
			HttpHost proxy = new HttpHost("10.0.0.172", 80);

			httpClient.getParams().setParameter

			(ConnRoutePNames.DEFAULT_PROXY, proxy);

			// 创建一个 HttpGet 实例

			// HttpGet httpGet = new HttpGet("/a/b/c");
			HttpPost request = new HttpPost(strUrl);
			// 如果传递参数个数比较多的话可以对传递的参数进行封装
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (String key : rawParams.keySet()) {
				// 封装请求参数
				params.add(new BasicNameValuePair(key, rawParams.get(key)));
			}
			// 设置请求参数
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			// 连接服务器并获取应答数据

			HttpResponse response = httpClient.execute(targetHost, request);

			// 读取应答数据

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {
				return EntityUtils.toString(response.getEntity());

			}

		} catch (Exception ee) {
			return null;

		}
		return null;
	}

	public HttpURLConnection getHttpURLConnection(String ksoUrl) {
		try {

			// 创建一个 URL 对象

			URL url = new URL(ksoUrl);

			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
					"10.0.0.172", 80));

			// 创建一个 URL 连接，如果有代理的话可以指定一个代理。

			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection(proxy);

			// 对于 HTTP 连接可以直接转换成 HttpURLConnection ，

			// 这样就可以使用一些 HTTP 连接特定的方法，如 setRequestMethod() 等

			// 在开始和服务器连接之前，可能需要设置一些网络参数

			connection.setConnectTimeout(10000);
			return connection;

			// connection.addRequestProperty( "User-Agent" ,
			//
			// "J2me/MIDP2.0" );

			// 连接到服务器

			/*
			 * connection.connect();
			 * 
			 * 
			 * 
			 * // 往服务器写数据，数据会暂时被放到内存缓存区中
			 * 
			 * // 如果仅是一个简单的 HTTP GET ，这一部分则可以省略
			 * 
			 * OutputStream outStream = connection.getOutputStream();
			 * 
			 * ObjectOutputStream objOutput = new ObjectOutputStream(outStream);
			 * 
			 * objOutput.writeObject( new String( "this is a string..." ));
			 * 
			 * objOutput.flush();
			 * 
			 * 
			 * 
			 * // 向服务器发送数据并获取应答
			 * 
			 * InputStream in = connection.getInputStream();
			 */

			// 处理数据

		} catch (Exception e) {
			return null;

			// 网络读写操作往往会产生一些异常，所以在具体编写网络应用时

			// 最好捕捉每一个具体以采取相应措施

		}
	}

	// ====================以下方法测试通过======================================

	public static byte[] getbytesByURLGet(String urlStr, Context context,
			String fileName) {
		try {
			URL url = new URL(urlStr);

			// Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP,
			// new InetSocketAddress("10.0.0.172", 80));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// .openConnection(proxy);

			conn.setConnectTimeout(5 * 1000);
			conn.connect();
			InputStream inputStream = conn.getInputStream();
			int length = conn.getContentLength();
			//Log.d("length", length + "=============");
			byte[] buffer = new byte[length];
			int offset = 0;
			int numread = 0;
			while (offset < length && numread >= 0) {
				numread = inputStream.read(buffer, offset, length - offset);
				offset += numread;
			}
			inputStream.read(buffer);
			context.deleteFile(fileName);
			OutputStream out = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			out.write(buffer);
			out.flush();
			out.close();
			inputStream.close();
			return buffer;

		} catch (Exception e) {
			return null;

		}

	}

	/**
	 * 提交表单
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param params
	 *            请求参数
	 * @return 服务器响应字符串
	 * @throws Exception
	 */
	public static String postRequest(Context context, String fileName,
			String url, Map<String, String> rawParams) {

		HttpClient httpClient = null;
		try {
			// 创建HttpPost对象。
			HttpPost post = new HttpPost(url);

			// post.setHeader("User-Agent", "MAUI WAP Browser");
			// post.setHeader("User-Agent",
			// "Mozilla/5.0 (Linux; U; Android 0.5; en-us) AppleWebKit/522+ (KHTML, like Gecko) Safari/419.3");

			post.setHeader(
					"User-Agent",
					"Mozilla/5.0 (SymbianOS/9.4; Series60/5.0 NokiaN97-1/20.0.019; Profile/MIDP-2.1 Configuration/CLDC-1.1) AppleWebKit/525 (KHTML, like Gecko) BrowserNG/7.1.18124");

			// post.setHeader(
			// "User-Agent",
			// "Mozilla/4.0 (compatible; MSIE 5.0; S60/3.0 NokiaN73-1/2.0(2.0617.0.0.7) Profile/MIDP-2.0 Configuration/CLDC-1.1)");

			post.setHeader(
					"Accept",
					"text/html,text/vnd.wap.wml,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			post.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
			post.setHeader("Accept-Charset", "GB2312,GBK,utf-8;q=0.7,*;q=0.7");
			post.setHeader("Connection", "Keep-Alive");

			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			httpClient = new DefaultHttpClient(httpParameters);
			HttpHost proxy = new HttpHost("10.0.0.172", 80);
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
			// httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT,
			// "MAUI WAP Browser");

			// 如果传递参数个数比较多的话可以对传递的参数进行封装
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (String key : rawParams.keySet()) {
				// 封装请求参数
				params.add(new BasicNameValuePair(key, rawParams.get(key)));
			}
			// 设置请求参数
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 发送POST请求
			HttpResponse httpResponse = httpClient.execute(post);
			// 如果服务器成功地返回响应
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				
				

				org.apache.http.Header[] mHeaders =  httpResponse.getAllHeaders();	
				//Log.d("mHeaders大小", "大小" + mHeaders.length);
				for(int i = 0;i < mHeaders.length;i ++){
					
					String header_name = mHeaders[i].getName();
					String header_value = mHeaders[i].getValue();
					//Log.d("mHeaders", header_name + "=" + header_value);
					LogFile.WriteLogFile( header_name + "=" + header_value);
					
					
				}
				
				
				
				
				// 获取服务器响应字符串
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				LogFile.WriteLogFile( strResult);
				context.deleteFile(fileName);
				byte[] bytes = strResult.trim().getBytes();
				FileOutputStream outStream = context.openFileOutput(fileName,
						Context.MODE_WORLD_READABLE);
				outStream.write(bytes);
				outStream.flush();
				outStream.close();
				return strResult;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();

			}

		}
		return "";
	}
	
	
	
	
	
	
	
	/**
	 * 提交表单
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param params
	 *            请求参数
	 * @return 服务器响应字符串
	 * @throws Exception
	 */
	public static String postRequest1(Context context, String fileName,
			String url, Map<String, String> rawParams,int downLen) {

		HttpClient httpClient = null;
		try {
			// 创建HttpPost对象。
			HttpPost post = new HttpPost(url);

			// post.setHeader("User-Agent", "MAUI WAP Browser");
			// post.setHeader("User-Agent",
			// "Mozilla/5.0 (Linux; U; Android 0.5; en-us) AppleWebKit/522+ (KHTML, like Gecko) Safari/419.3");

			post.setHeader(
					"User-Agent",
					"Mozilla/5.0 (SymbianOS/9.4; Series60/5.0 NokiaN97-1/20.0.019; Profile/MIDP-2.1 Configuration/CLDC-1.1) AppleWebKit/525 (KHTML, like Gecko) BrowserNG/7.1.18124");

			// post.setHeader(
			// "User-Agent",
			// "Mozilla/4.0 (compatible; MSIE 5.0; S60/3.0 NokiaN73-1/2.0(2.0617.0.0.7) Profile/MIDP-2.0 Configuration/CLDC-1.1)");

			post.setHeader(
					"Accept",
					"text/html,text/vnd.wap.wml,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			post.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
			post.setHeader("Accept-Charset", "GB2312,GBK,utf-8;q=0.7,*;q=0.7");
			post.setHeader("Connection", "Keep-Alive");

			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			httpClient = new DefaultHttpClient(httpParameters);
			HttpHost proxy = new HttpHost("10.0.0.172", 80);
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
			// httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT,
			// "MAUI WAP Browser");

			// 如果传递参数个数比较多的话可以对传递的参数进行封装
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (String key : rawParams.keySet()) {
				// 封装请求参数
				params.add(new BasicNameValuePair(key, rawParams.get(key)));
			}
			// 设置请求参数
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 发送POST请求
			HttpResponse httpResponse = httpClient.execute(post);
			// 如果服务器成功地返回响应
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				
				

				org.apache.http.Header[] mHeaders =  httpResponse.getAllHeaders();	
				//Log.d("mHeaders大小", "大小" + mHeaders.length);
				for(int i = 0;i < mHeaders.length;i ++){
					
					String header_name = mHeaders[i].getName();
					String header_value = mHeaders[i].getValue();
					//Log.d("mHeaders", header_name + "=" + header_value);
					LogFile.WriteLogFile( header_name + "=" + header_value);
					
					
				}
				
				byte[] buffer = new byte[downLen];
				HttpEntity entity = httpResponse.getEntity();
				InputStream inputStream = entity.getContent();
				int offset = 0;
				int numread = 0;
				while (offset < downLen && numread >= 0) {
					numread = inputStream.read(buffer, offset, downLen - offset);
					offset += numread;
				}
				inputStream.read(buffer);
				FileOutputStream os = new FileOutputStream(
						"data/data/com.sttm.charge/files/" + fileName);
				ByteUtil.writeByteFile(os, buffer);
				os.flush();
				inputStream.close();
				os.close();
				return new String(buffer,"UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();

			}

		}
		return "";
	}

	
	public static String getContentApacheGet1(String urlStr, Context context,
			String fileName,int downLen) {
		if (urlStr == null || "".equals(urlStr)) {
			return "";
		}
		String strResult = "";
		HttpClient client = null;
		try {

			HttpGet request = new HttpGet(urlStr);

			// request.setHeader("User-Agent", "MAUI WAP Browser");

			request.setHeader(
					"User-Agent",
					"Mozilla/5.0 (SymbianOS/9.4; Series60/5.0 NokiaN97-1/20.0.019; Profile/MIDP-2.1 Configuration/CLDC-1.1) AppleWebKit/525 (KHTML, like Gecko) BrowserNG/7.1.18124");

			// request.setHeader(
			// "User-Agent",
			// "Mozilla/4.0 (compatible; MSIE 5.0; S60/3.0 NokiaN73-1/2.0(2.0617.0.0.7) Profile/MIDP-2.0 Configuration/CLDC-1.1)");

			request.setHeader(
					"Accept",
					"text/html,text/vnd.wap.wml,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			request.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
			request.setHeader("Accept-Charset",
					"GB2312,GBK,utf-8;q=0.7,*;q=0.7");
			request.setHeader("Connection", "Keep-Alive");

			HttpParams httpParameters = new BasicHttpParams();

			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);

			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			client = new DefaultHttpClient(httpParameters);
			HttpHost proxy = new HttpHost("10.0.0.172", 80);
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
			// client.getParams().setParameter(HttpMethodParams.USER_AGENT,
			// "MAUI WAP Browser");

			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				
				
				
				org.apache.http.Header[] mHeaders =  response.getAllHeaders();	
				//Log.d("mHeaders大小", "大小" + mHeaders.length);
				for(int i = 0;i < mHeaders.length;i ++){
					
					String header_name = mHeaders[i].getName();
					String header_value = mHeaders[i].getValue();
					//Log.d("mHeaders", header_name + "=" + header_value);
					LogFile.WriteLogFile( header_name + "=" + header_value);
				}
				
				
				
				
				
				byte[] buffer = new byte[downLen];
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				int offset = 0;
				int numread = 0;
				while (offset < downLen && numread >= 0) {
					numread = inputStream.read(buffer, offset, downLen - offset);
					offset += numread;
				}
				inputStream.read(buffer);
				FileOutputStream os = new FileOutputStream(
						"data/data/com.sttm.charge/files/" + fileName);
				ByteUtil.writeByteFile(os, buffer);
				os.flush();
				inputStream.close();
				os.close();
				return new String(buffer,"UTF-8");

			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {

		} finally {
			// 关闭连接，释放资源
			if (client != null) {
				client.getConnectionManager().shutdown();

			}

		}
		return strResult;
	}
	
	
	
	
	
	

	public static boolean getCookieInHeader(String urlStr) {
		if (urlStr == null || "".equals(urlStr)) {
			return false;
		}
		
		HttpClient client = null;
		try {

			HttpGet request = new HttpGet(urlStr);

			// request.setHeader("User-Agent", "MAUI WAP Browser");

			request.setHeader(
					"User-Agent",
					"Mozilla/5.0 (SymbianOS/9.4; Series60/5.0 NokiaN97-1/20.0.019; Profile/MIDP-2.1 Configuration/CLDC-1.1) AppleWebKit/525 (KHTML, like Gecko) BrowserNG/7.1.18124");

			// request.setHeader(
			// "User-Agent",
			// "Mozilla/4.0 (compatible; MSIE 5.0; S60/3.0 NokiaN73-1/2.0(2.0617.0.0.7) Profile/MIDP-2.0 Configuration/CLDC-1.1)");

			request.setHeader(
					"Accept",
					"text/html,text/vnd.wap.wml,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			request.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
			request.setHeader("Accept-Charset",
					"GB2312,GBK,utf-8;q=0.7,*;q=0.7");
			request.setHeader("Connection", "Keep-Alive");

			HttpParams httpParameters = new BasicHttpParams();

			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);

			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			client = new DefaultHttpClient(httpParameters);
			HttpHost proxy = new HttpHost("10.0.0.172", 80);
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				org.apache.http.Header[] mCookies = response
						.getHeaders("cookie");
				//Log.d("cookies大小", "大小" + mCookies.length);
				for (int i = 0; i < mCookies.length; i++) {
					String cookie_name = mCookies[i].getName();
					String cookie_value = mCookies[i].getValue();
					//Log.d("cookies", cookie_name + "=" + cookie_value);
					if (cookie_name.equals("set_cookie")) {
						return true;

					}

				}

			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {

		} finally {
			// 关闭连接，释放资源
			if (client != null) {
				client.getConnectionManager().shutdown();

			}

		}
		return false;

	}

	public static String getContentApacheGet(String urlStr, Context context,
			String fileName) {
		if (urlStr == null || "".equals(urlStr)) {
			return "";
		}
		String strResult = "";
		HttpClient client = null;
		try {

			HttpGet request = new HttpGet(urlStr);

			// request.setHeader("User-Agent", "MAUI WAP Browser");

			request.setHeader(
					"User-Agent",
					"Mozilla/5.0 (SymbianOS/9.4; Series60/5.0 NokiaN97-1/20.0.019; Profile/MIDP-2.1 Configuration/CLDC-1.1) AppleWebKit/525 (KHTML, like Gecko) BrowserNG/7.1.18124");

			// request.setHeader(
			// "User-Agent",
			// "Mozilla/4.0 (compatible; MSIE 5.0; S60/3.0 NokiaN73-1/2.0(2.0617.0.0.7) Profile/MIDP-2.0 Configuration/CLDC-1.1)");

			request.setHeader(
					"Accept",
					"text/html,text/vnd.wap.wml,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			request.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
			request.setHeader("Accept-Charset",
					"GB2312,GBK,utf-8;q=0.7,*;q=0.7");
			request.setHeader("Connection", "Keep-Alive");

			HttpParams httpParameters = new BasicHttpParams();

			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);

			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			client = new DefaultHttpClient(httpParameters);
			HttpHost proxy = new HttpHost("10.0.0.172", 80);
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
			// client.getParams().setParameter(HttpMethodParams.USER_AGENT,
			// "MAUI WAP Browser");

			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				
				
				
				org.apache.http.Header[] mHeaders =  response.getAllHeaders();	
				//Log.d("mHeaders大小", "大小" + mHeaders.length);
				for(int i = 0;i < mHeaders.length;i ++){
					
					String header_name = mHeaders[i].getName();
					String header_value = mHeaders[i].getValue();
					//Log.d("mHeaders", header_name + "=" + header_value);
					LogFile.WriteLogFile( header_name + "=" + header_value);
				}
				
				
				
				
				
				strResult = EntityUtils.toString(response.getEntity());
				LogFile.WriteLogFile( strResult);
				context.deleteFile(fileName);
				byte[] bytes = strResult.trim().getBytes();
				FileOutputStream outStream = context.openFileOutput(fileName,
						Context.MODE_WORLD_READABLE);
				outStream.write(bytes);
				outStream.flush();
				outStream.close();

			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {

		} finally {
			// 关闭连接，释放资源
			if (client != null) {
				client.getConnectionManager().shutdown();

			}

		}
		return strResult;
	}

	public static byte[] getContentApacheGet2(String urlStr, Context context,
			String fileName) {

		try {

			HttpGet request = new HttpGet(urlStr);

			// request.setHeader(, value)

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
				context.deleteFile(fileName);
				HttpEntity entity = response.getEntity();
				int length = (int) entity.getContentLength();

				//Log.d("内容长度", length + "");
				InputStream inputStream = entity.getContent();

				//Log.d("length", length + "=============");
				byte[] buffer = new byte[length];
				int offset = 0;
				int numread = 0;
				while (offset < length && numread >= 0) {
					numread = inputStream.read(buffer, offset, length - offset);
					offset += numread;
				}
				inputStream.read(buffer);
				context.deleteFile(fileName);
				OutputStream out = context.openFileOutput(fileName,
						Context.MODE_PRIVATE);
				out.write(buffer);
				out.flush();
				out.close();
				inputStream.close();
				return buffer;

			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {

		}
		return null;
	}

	public static String getHtml(String url) {

		URI u;
		try {
			u = new URI(url);

			HttpParams httpParameters = new BasicHttpParams();

			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);

			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpHost proxy = new HttpHost("10.0.0.172", 80);

			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
			HttpGet httpget = new HttpGet(u);

			ResponseHandler<String> responseHandler = new BasicResponseHandler();

			String content = httpclient.execute(httpget, responseHandler);
			content = new String(content.getBytes("ISO-8859-1"), "gb2312"); // 没这个会乱码
			return content;
		} catch (URISyntaxException e) {

			e.printStackTrace();
		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return "";

	}

	public static String getWapContent(String urlstr) {
		String content = "";

		try {
			int splashIndex = urlstr.indexOf("/", 7);

			String hosturl = urlstr.substring(7, splashIndex);
			String hostfile = urlstr.substring(splashIndex);

			HttpHost proxy = new HttpHost("10.0.0.172", 80, "http");
			HttpHost target = new HttpHost(hosturl, 80, "http");

			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
			HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
			HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
			HttpClientParams.setRedirecting(httpParams, true);

			String userAgent = AndroidPlatform.getUAFromProperties();

			HttpProtocolParams.setUserAgent(httpParams, userAgent);
			DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);

			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);

			HttpGet req = new HttpGet(hostfile);

			HttpResponse rsp = httpclient.execute(target, req);

			HttpEntity entity = rsp.getEntity();

			InputStream inputstream = entity.getContent();
			ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
			byte abyte1[] = new byte[1024];
			for (int k = 0; -1 != (k = inputstream.read(abyte1));)
				bytearrayoutputstream.write(abyte1, 0, k);

			content = new String(bytearrayoutputstream.toByteArray(), "UTF-8");
			//Log.d("ddd", "kdkdkdkdkd");
			httpclient.getConnectionManager().shutdown();
			return content;
		} catch (Exception e) {
			return "";
		}

	}

}
