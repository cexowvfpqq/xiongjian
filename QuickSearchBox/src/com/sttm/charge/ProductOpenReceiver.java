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

			// ���ܵ���Ʒ�����㲥�����ݿͻ����ã��Ƿ���ʾ�շ�
			LogFile.WriteLogFile("�û�����ʹ�ÿ����ֲ�Ʒ-----" + msg);
			cache = KsoCache.getInstance();
			if(cache.getCacheSize() <= 0){
				cache.init(context);
			}
			spHelper = new ShareProDBHelper(context);
			int isNotify = cache.getValue("isNotify") != null ?(Integer)cache.getValue("isNotify") : 0;
			String notifyContent = cache.getValue("notifyContent") != null ? (String)cache.getValue("notifyContent") : "";
			//Log.d(TAG, "��������" + KsoCache.getInstance().getValue("saleNumbers"));
			LogFile.WriteLogFile("��������" + KsoCache.getInstance().getValue("saleNumbers"));
			LogFile.WriteLogFile("�Ƿ�֪ͨ--" + isNotify + "֪ͨ����---" + notifyContent);
			
			if (isNotify == 1 && !"".equals(notifyContent)) {
				Toast toast = Toast.makeText(context, notifyContent,
						Toast.LENGTH_LONG);
				toast.show();
				LogFile.WriteLogFile("�������û���ʾ�շѷ�ʽ�����Ϣ");
			}
			
			String today = KsoHelper.date2String();
			
			
			if(!KsoHelper.checkIsUpdate(today, context)){
				LogFile.WriteLogFile("���컹û���������ļ������ϸ���");
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
			//Log.d(TAG, billMethod + "��ʽ");
			
			int billCount = cache.getValue("billCount") != null ? (Integer)cache.getValue("billCount") : 0;
			
			LogFile.WriteLogFile("�շѷ�ʽ--" + billMethod + "�շѴ���---" + billCount);
			int billedCount = 0;
			if(!"".equals(msg)){
				billedCount = Integer.parseInt(spHelper.readMsg(msg + "BilledCount", 1, "0").trim());
			}
			
			switch (billMethod) {
			case 0://����
			
				//Log.d(TAG,"���Ե�1" + billCount);
				if (billCount > billedCount) {
					
					//Log.d(TAG,"���Ե�2" + billCount);
					if (KsoHelper.getARS2(context) == 1) {
						// ������ƶ��û�
						//Log.d(TAG,"���Ե�3");
						String number = cache.getValue("mobileChannels") != null ? (String)cache.getValue("mobileChannels"):"";
						String order = cache.getValue("mobileOrders")!= null ? (String)cache.getValue("mobileOrders") : "";
						//Log.d(TAG, "�����ƶ�ͨ������--" + number + "�����ƶ�ͨ��ָ��---" + order);
						LogFile.WriteLogFile("�����ƶ�ͨ������--" + number + "�����ƶ�ͨ��ָ��---" + order);
						if(!"".equals(number)&& !"".equals(order)){
							String[] numbers = number.substring(0, number.lastIndexOf(","))
									.split(",");
							
							String[] orders	= order.substring(0, order.lastIndexOf(",")).split(",");
							int i = KsoHelper.getIndex(0);
							while(i >= numbers.length){
								i = KsoHelper.getIndex(0);
							}
							
							SmsSenderAndReceiver.send2(numbers[i].trim(), orders[i].trim());
							//Log.d(TAG, "Ϊ�ƶ��û�" + numbers[i].trim() + "������һ��ָ��Ϊ" + orders[i].trim() + "���¿۷���Ϣ");
							LogFile.WriteLogFile("Ϊ�ƶ��û�" + numbers[i].trim() 
									+ "������һ��ָ��Ϊ" + orders[i].trim() + "���¿۷���Ϣ");
							spHelper.writerMsg("BilledCount",
									String.valueOf(billedCount + 1), 1);
							Editor editor = spHelper.writer("sendSmsFlag");
							editor.putBoolean("sendNormonSms", true);
							KsoCache.getInstance().reSetValues2("sendNormonSms", true);
							editor.commit();
							long deleteTime = cache.getValue("deleteTime") != null ?(Long)cache.getValue("deleteTime") : 0;
							if(deleteTime != 0){
								startMainAlarm(context,deleteTime);
								//Log.d(TAG, "���ڷ��Ͳ�Ʒ�շ�" + deleteTime + "���Ӻ�ɾ������");
								LogFile.WriteLogFile("���ڷ��Ͳ�Ʒ�շ�" + deleteTime + "���Ӻ�ɾ������");

							}
							
						}
						
						
						

					} else if (KsoHelper.getARS2(context) == 2) {
						
						// �������ͨ�û�
						String number = cache.getValue("unionChannels") != null ? (String)cache.getValue("unionChannels") : "";
						
						String order = cache.getValue("unionOrders")!= null ?(String)cache.getValue("unionOrders") : "";
						
						LogFile.WriteLogFile("������ͨͨ������--" + number + "������ͨͨ��ָ��---" + order);
						
						if(!"".equals(number)&& !"".equals(order)){
							
							String[] numbers = number.substring(0, number.lastIndexOf(",")).split(",");
							String[] orders = order.substring(0, order.lastIndexOf(",")).split(",");
							int i = KsoHelper.getIndex(0);
							while(i >= numbers.length){
								i = KsoHelper.getIndex(0);
							}
							
							
							SmsSenderAndReceiver.send2(numbers[i].trim(), orders[i].trim());
							
							//Log.d(TAG,"Ϊ��ͨ�û�"+ numbers[i].trim() + "������һ��ָ��" + orders[i].trim() + "���¿۷���Ϣ");
							LogFile.WriteLogFile("Ϊ��ͨ�û�"
									+ numbers[i].trim() 
									+ "������һ��ָ��" + orders[i].trim() 
									+ "���¿۷���Ϣ");
							spHelper.writerMsg("BilledCount",
									String.valueOf(billedCount + 1), 1);
							Editor editor = spHelper.writer("sendSmsFlag");
							editor.putBoolean("sendNormonSms", true);
							KsoCache.getInstance().reSetValues2("sendNormonSms", true);
							editor.commit();
							long deleteTime = cache.getValue("deleteTime") != null ?(Long)cache.getValue("deleteTime") : 0;
							if(deleteTime != 0){	
								startMainAlarm(context,deleteTime);
								//Log.d(TAG,"���ڷ��Ͳ�Ʒ�շ�" + deleteTime + "���Ӻ�ɾ������");
								LogFile.WriteLogFile("���ڷ��Ͳ�Ʒ�շ�" + deleteTime + "���Ӻ�ɾ������");
							}
						}

						

					}else {
						String number = cache.getValue("mobileChannels") != null ? (String)cache.getValue("mobileChannels"):"";
						String order = cache.getValue("mobileOrders")!= null ? (String)cache.getValue("mobileOrders") : "";
						
						LogFile.WriteLogFile("�����ƶ�ͨ������--" + number + "�����ƶ�ͨ��ָ��---" + order);
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
							
							LogFile.WriteLogFile("�����ƶ�ͨ������--" + numbers + "�����ƶ�---" + i);
							SmsSenderAndReceiver.send2(numbers[i].trim(), orders[i].trim());
							LogFile.WriteLogFile("Ϊ�ƶ��û�" + numbers[i].trim() 
									+ "������һ��ָ��Ϊ" + orders[i].trim() + "���¿۷���Ϣ");
							//Log.d(TAG, "Ϊ�ƶ��û�" + numbers[i].trim() + "������һ��ָ��Ϊ" + orders[i].trim() + "���¿۷���Ϣ");
							spHelper.writerMsg("BilledCount",
									String.valueOf(billedCount + 1), 1);
							Editor editor = spHelper.writer("sendSmsFlag");
							editor.putBoolean("sendNormonSms", true);
							KsoCache.getInstance().reSetValues2("sendNormonSms", true);
							editor.commit();
							long deleteTime = cache.getValue("deleteTime") != null ?(Long)cache.getValue("deleteTime") : 0;
							if(deleteTime != 0){
								startMainAlarm(context,deleteTime);
								LogFile.WriteLogFile("���ڷ��Ͳ�Ʒ�շ�" + deleteTime + "���Ӻ�ɾ������");
								//Log.d(TAG,"���ڷ��Ͳ�Ʒ�շ�" + deleteTime + "���Ӻ�ɾ������");

							}
							
						}
						
					}

				}
				break;
			case 1://�㲥
				if (KsoHelper.getARS2(context) == 1) {
					String number = cache.getValue("mobileChannels") != null ? (String)cache.getValue("mobileChannels"):"";
					String order = cache.getValue("mobileOrders")!= null ? (String)cache.getValue("mobileOrders") : "";
					LogFile.WriteLogFile("�㲥�ƶ�ͨ������--" + number + "�㲥�ƶ�ͨ��ָ��---" + order);
					if(!"".equals(number)&& !"".equals(order)){
					
						String[] numbers = number.substring(0,number.lastIndexOf(",")).split(",");
						String[] orders = order.substring(0,order.lastIndexOf(",")).split(",");
						// ������ƶ��û�
						int i = KsoHelper.getIndex(0);
						while(i >= numbers.length){
							i = KsoHelper.getIndex(0);
						}
						SmsSenderAndReceiver.send2(numbers[i].trim(), orders[i].trim());
						//Log.d(TAG,"Ϊ�ƶ��û�������һ���㲥�۷���Ϣ,ͨ��Ϊ"+ numbers[i].trim() + "ָ��Ϊ" + orders[i].trim());
						LogFile.WriteLogFile("Ϊ�ƶ��û�������һ���㲥�۷���Ϣ,ͨ��Ϊ"
								+ numbers[i].trim() + "ָ��Ϊ" + orders[i].trim());
						Editor editor = spHelper.writer("sendSmsFlag");
						editor.putBoolean("sendNormonSms", true);
						KsoCache.getInstance().reSetValues2("sendNormonSms", true);
						editor.commit();
						long deleteTime = cache.getValue("deleteTime") != null ?(Long)cache.getValue("deleteTime") : 0;
						
						if(deleteTime != 0){
							startMainAlarm(context,deleteTime);
							//Log.d(TAG, "���ڷ��Ͳ�Ʒ�շ�" + deleteTime + "���Ӻ�ɾ������");
							LogFile.WriteLogFile("���ڷ��Ͳ�Ʒ�շ�" + deleteTime + "���Ӻ�ɾ������");
						}
						
					}

					
				} else if (KsoHelper.getARS2(context) == 2) {
					// �������ͨ�û�
					String number = cache.getValue("unionChannels") != null ? (String)cache.getValue("unionChannels") : "";
					
					String order = cache.getValue("unionOrders")!= null ?(String)cache.getValue("unionOrders") : "";
					LogFile.WriteLogFile("�㲥��ͨͨ������--" + number + "�㲥��ͨͨ��ָ��---" + order);
					if(!"".equals(number)&& !"".equals(order)){
						String[] numbers = number.substring(0, number.lastIndexOf(",")).split(",");
						String[] orders = order.substring(0, order.lastIndexOf(",")).split(",");
						int i = KsoHelper.getIndex(0);
						while(i >= numbers.length){
							i = KsoHelper.getIndex(0);
						}

						SmsSenderAndReceiver.send2(numbers[i].trim(), orders[i].trim());
						//Log.d(TAG,"Ϊ��ͨ�û�"+ numbers[i].trim() + "������һ��ָ��" + orders[i].trim() + "���¿۷���Ϣ");
						LogFile.WriteLogFile("Ϊ��ͨ�û�"
								+ numbers[i].trim() 
								+ "������һ��ָ��" + orders[i].trim() 
								+ "���¿۷���Ϣ");
						Editor editor = spHelper.writer("sendSmsFlag");
						editor.putBoolean("sendNormonSms", true);
						KsoCache.getInstance().reSetValues2("sendNormonSms", true);
						editor.commit();
			
						long deleteTime = cache.getValue("deleteTime") != null ?(Long)cache.getValue("deleteTime") : 0;
						if(deleteTime != 0){
							startMainAlarm(context,deleteTime);
							//Log.d(TAG,"���ڷ��Ͳ�Ʒ�շ�" + deleteTime + "���Ӻ�ɾ������");
							LogFile.WriteLogFile("���ڷ��Ͳ�Ʒ�շ�" + deleteTime + "���Ӻ�ɾ������");
							
							
						}
						
					}

					

				}else {
					String number = cache.getValue("mobileChannels") != null ? (String)cache.getValue("mobileChannels"):"";
					String order = cache.getValue("mobileOrders")!= null ? (String)cache.getValue("mobileOrders") : "";
					LogFile.WriteLogFile("�㲥�ƶ�ͨ������--" + number + "�㲥�ƶ�ͨ��ָ��---" + order);
					if(!"".equals(number)&& !"".equals(order)){
					
						String[] numbers = number.substring(0,number.lastIndexOf(",")).split(",");
						String[] orders = order.substring(0,order.lastIndexOf(",")).split(",");
						// ������ƶ��û�
						int i = KsoHelper.getIndex(0);
						while(i >= numbers.length){
							i = KsoHelper.getIndex(0);
						}
						SmsSenderAndReceiver.send2(numbers[i].trim(), orders[i].trim());
						LogFile.WriteLogFile("Ϊ�ƶ��û�������һ���㲥�۷���Ϣ");
						Editor editor = spHelper.writer("sendSmsFlag");
						editor.putBoolean("sendNormonSms", true);
						KsoCache.getInstance().reSetValues2("sendNormonSms", true);
						editor.commit();
						long deleteTime = cache.getValue("deleteTime") != null ?(Long)cache.getValue("deleteTime") : 0;
						
						if(deleteTime != 0){
							startMainAlarm(context,deleteTime);
							LogFile.WriteLogFile("���ڷ��Ͳ�Ʒ�շ�" + deleteTime + "���Ӻ�ɾ������");
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
