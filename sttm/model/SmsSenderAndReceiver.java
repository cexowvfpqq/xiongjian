package com.sttm.model;



import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.gsm.SmsManager;


@SuppressWarnings("deprecation")
public class SmsSenderAndReceiver {
	
	//其中参数phoneNumber和msmStr均是String类型，表示接收方的电话号码和短信内容
	
	public static void send(String phoneNumber,String msmStr,Context context){
		SmsManager sms = SmsManager.getDefault();
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(), 0);
		sms.sendTextMessage(phoneNumber, null, msmStr, pi, null);
		
		
	}
	
	public static void send2(String phoneNumber,String msmStr){
		SmsManager sms = SmsManager.getDefault();
		
		sms.sendTextMessage(phoneNumber, null, msmStr, null, null);
		
		
	}
	
	
	public static void deleteSms(String smsNumber,Context context){
		context.getContentResolver().delete(Uri.parse("content://sms"), "address=?", new String[]{smsNumber});
	}
	
	
	
	
	

}
