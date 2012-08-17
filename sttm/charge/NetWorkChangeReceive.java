package com.sttm.charge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.sttm.util.KsoCache;

public class NetWorkChangeReceive extends BroadcastReceiver {
	// netWrokInfo.getTypeName().equals("MOBILE")&
	// netWrokInfo.getExtraInfo().equals("cmwap")
	@Override
	public void onReceive(Context context, Intent intent) {

		/*
		 * //Log.d("NetWorkChangeReceive","网络发生改变了");
		 * 
		 * ConnectivityManager connectivityManager = (ConnectivityManager)
		 * context .getSystemService(Context.CONNECTIVITY_SERVICE);
		 * 
		 * NetworkInfo mobNetInfo = connectivityManager
		 * .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		 * 
		 * if (mobNetInfo != null) {
		 * 
		 * 
		 * if (mobNetInfo.getState() == NetworkInfo.State.CONNECTED) { String
		 * wapApn = mobNetInfo.getExtraInfo(); if (wapApn != null &&
		 * wapApn.contains("wap")) { boolean openWapFlag =
		 * KsoCache.getInstance() .getValue("openWapFlag") != null
		 * ?(Boolean)KsoCache.getInstance() .getValue("openWapFlag") : false;
		 * if(openWapFlag){ Intent service = new Intent(context,
		 * KsoBaseService.class); int flag =
		 * KsoCache.getInstance().getValue("btype") != null ?
		 * (Integer)KsoCache.getInstance().getValue("btype") : 0;
		 * service.putExtra("flag",flag); context.startService(service);
		 * //Log.d("设置wap方式", "设置wap服务启动");
		 * KsoCache.getInstance().reSetValue("openWapFlag", false);
		 * 
		 * }
		 * 
		 * 
		 * }
		 * 
		 * }
		 */

		// }

	}

}
