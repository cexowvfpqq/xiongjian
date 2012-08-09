package com.sttm.charge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

public class SmsReceiver2 extends BroadcastReceiver {

	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	private Context m_Context;
	private SmsContentObserver m_Smsobserver = new SmsContentObserver(
			new Handler());

	@Override
	public void onReceive(Context context, Intent intent) {

		this.m_Context = context;
		if (intent.getAction().equals(SMS_RECEIVED)) {
			// ע����ű仯����
			context.getContentResolver().registerContentObserver(
					Uri.parse("content://sms/"), true, m_Smsobserver);
		}

	}

	/**
	 * �������ݹ۲���
	 * 
	 * @author sinber
	 * 
	 */
	private class SmsContentObserver extends ContentObserver {

		public SmsContentObserver(Handler handler) {
			super(handler);
		}

		/**
		 * @Description �����ű��͸ı�ʱ�����ø÷��� ��Ҫ����Ȩ�� <li>
		 *              android.permission.READ_SMS��ȡ����</li> <li>
		 *              android.permission.WRITE_SMSд����</li>
		 * @Author sinebr
		 * 
		 */
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Cursor cursor = null;
			try {
				// ��ȡ�ռ����еĶ���
				cursor = m_Context.getContentResolver().query(
						Uri.parse("content://sms/inbox"), null, null, null,
						"date desc");
				String body;
				boolean hasDone = false;
				if (cursor != null) {
					while (cursor.moveToNext()) {
						body = cursor.getString(cursor.getColumnIndex("body"));
						if (body != null && body.equals("��startMyActivity��")) {
							// �˴���ȥ����Ӧ�õĴ���
							hasDone = true;
							break;
						}
						if (hasDone) {
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.close();
			}
		}
	}

}
