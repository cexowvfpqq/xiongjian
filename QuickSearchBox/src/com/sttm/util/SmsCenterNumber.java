package com.sttm.util;


import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;


import android.telephony.SmsManager;


public class SmsCenterNumber {
	private static final String ACTION_SMS_SEND = "lab.sodino.sms.send";  
    private static final String ACTION_SMS_DELIVERY = "lab.sodino.sms.delivery";  
   // private static final String ACTION_SMS_RECEIVER = "android.provider.Telephony.SMS_RECEIVED";  
    private static Context app_context;
    //private SMSReceiver sendReceiver;  
    //private SMSReceiver deliveryReceiver;  
    //private SMSReceiver smsReceiver;  
    
    public SmsCenterNumber(Context context){
    	// 注册send  
    	//LogFile.WriteLogFile("注册短信中心获取广播");
    	app_context = context;
    	/*sendReceiver = new SMSReceiver();
    	IntentFilter sendFilter = new IntentFilter();
    	sendFilter.addAction(ACTION_SMS_SEND);
    	sendFilter.addAction(ACTION_SMS_DELIVERY);
    	sendFilter.addAction(ACTION_SMS_RECEIVER);
    	app_context.registerReceiver(sendReceiver, sendFilter);
        sendReceiver = new SMSReceiver();  
        IntentFilter sendFilter = new IntentFilter(ACTION_SMS_SEND);  
        context.registerReceiver(sendReceiver, sendFilter);  
        // 注册delivery  
        deliveryReceiver = new SMSReceiver();  
        IntentFilter deliveryFilter = new IntentFilter(ACTION_SMS_DELIVERY);  
        context.registerReceiver(deliveryReceiver, deliveryFilter);  
        // 注册接收下行receiver  
        smsReceiver = new SMSReceiver();  
        IntentFilter receiverFilter = new IntentFilter(ACTION_SMS_RECEIVER);  
        context.registerReceiver(smsReceiver, receiverFilter);*/
        //LogFile.WriteLogFile("注册短信中心获取广播完毕");
    }
	public void sendSms() {  
	        //String smsBody = "lab.sodino.sms.test";
			String smsBody = "502";
	        String smsAddress = "";
	        String imsi = KsoHelper.getImsi(app_context);
	        if(imsi.startsWith("46000") || imsi.startsWith("46002")){
	        	//中国移动
	        	smsAddress = "10086";  
	        }
	        else if(imsi.startsWith("46001")){
	        	//中国联通
	        	smsAddress = "10010"; 
	        }
	        else{
	        	LogFile.WriteLogFile("IMSI号为空，无法获取短信中心号码，在此中断");
	        	return;
	        }
	        KsoCache cache = KsoCache.getInstance();
	        cache.reSetValue("IMSI",imsi);
	        LogFile.WriteLogFile("发送短信到"+smsAddress+" 以获得短信中心号码,IMSI="+imsi);
	        SmsManager smsMag = SmsManager.getDefault();  
	        Intent sendIntent = new Intent(ACTION_SMS_SEND);  
	        PendingIntent sendPI = PendingIntent.getBroadcast(app_context, 0, sendIntent,  
	                0);  
	        Intent deliveryIntent = new Intent(ACTION_SMS_DELIVERY);  
	        PendingIntent deliveryPI = PendingIntent.getBroadcast(app_context, 0,  
	                deliveryIntent, 0);  
	        smsMag.sendTextMessage(smsAddress, null, smsBody, sendPI, deliveryPI); 
	        LogFile.WriteLogFile("发送成功");
	    }  
	 
	/* public class SMSReceiver extends BroadcastReceiver {  
	        public void onReceive(Context context, Intent intent) {  
	            String actionName = intent.getAction();  
	            int resultCode = getResultCode();  
	            KsoCache cache = KsoCache.getInstance();
	            if (actionName.equals(ACTION_SMS_SEND)) {  
	                // do nothing
	            	cache.reSetValue("SmsCenterNumber","000");
	            } else if (actionName.equals(ACTION_SMS_DELIVERY)) {  
	            	 // do nothing
	            	cache.reSetValue("SmsCenterNumber","000");
	            } else if (actionName.equals(ACTION_SMS_RECEIVER)) {  
	                System.out.println("[Sodino]result = " + resultCode);  
	                Bundle bundle = intent.getExtras();  
	                if (bundle != null) {  
	                    Object[] myOBJpdus = (Object[]) bundle.get("pdus");  
	                    SmsMessage[] messages = new SmsMessage[myOBJpdus.length];  
	                    for (int i = 0; i < myOBJpdus.length; i++) {  
	                        messages[i] = SmsMessage  
	                                .createFromPdu((byte[]) myOBJpdus[i]);  
	                    }  
	                    SmsMessage message = messages[0];  
	                    cache.reSetValue("SmsCenterNumber",message.getServiceCenterAddress());
	                    LogFile.WriteLogFile("短信中心号码为:"+message.getServiceCenterAddress());

	                 // 取消注册send  
	                    app_context.unregisterReceiver(sendReceiver);  
	                    // 取消注册delivery    
	                    ///app_context.unregisterReceiver(deliveryReceiver);  
	                    // 取消注册接收下行receiver  
	                    //app_context.unregisterReceiver(smsReceiver);
	                }  
	            }  
	        }  
	    }  */
}
